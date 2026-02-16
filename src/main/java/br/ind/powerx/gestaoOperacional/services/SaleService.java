package br.ind.powerx.gestaoOperacional.services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.MechanicApuration;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeSaleDto;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductSaleDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.SaleDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.SaleDetailsToUpdateDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UpdateSaleDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UpdateSaleItemDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleCustomerReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleDateReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleFlagReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleGroupReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.SaleStateReportInstructions;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.SaleSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class SaleService {

	private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

	private final SaleRepository saleRepository;

	private final CustomerRepository customerRepository;

	private final EmployeeRepository empRepository;

	private final ProductRepository productRepository;

	private final CalculeIncentiveService calculeIncentiveService;

	private final IncentiveRepository incentiveRepository;
	
	private final ProductStockService productStockService;
	
	private final CurrentAccountService currentAccountService;

	@Autowired
	public SaleService(SaleRepository saleRepository, CustomerRepository customerRepository,
			EmployeeRepository empRepository, ProductRepository productRepository,
			CalculeIncentiveService calculeIncentiveService, IncentiveRepository incentiveRepository,
			ProductStockService productStockService, CurrentAccountService currentAccountService) {
		this.saleRepository = saleRepository;
		this.customerRepository = customerRepository;
		this.empRepository = empRepository;
		this.productRepository = productRepository;
		this.calculeIncentiveService = calculeIncentiveService;
		this.incentiveRepository = incentiveRepository;
		this.productStockService = productStockService;
		this.currentAccountService = currentAccountService;
	}

	public List<Incentive> saveSales(List<SaleDTO> salesDto) {
		logger.info("Iniciando processamento de {} vendas.", salesDto.size());

		Integer maxDocumentNumeber = saleRepository.findMaxDocumentNumber();
		logger.debug("Número máximo de documento de venda encontrado: {}", maxDocumentNumeber);

		int newDocumentNumeber = (maxDocumentNumeber != null ? maxDocumentNumeber : 0) + 1;
		logger.debug("Novo número de documento de venda calculado: {}", newDocumentNumeber);

		List<Sale> sales = new ArrayList<>();
		for (SaleDTO sale : salesDto) {
			if (sale.getCustomer() == null) {
				logger.error("O id do cliente não pode ser nulo para a venda: {}", sale);
				throw new NullPointerException("O id do cliente não pode ser nulo");
			}
			Long customerId = sale.getCustomer();
			Customer customer = customerRepository.findById(customerId).orElseThrow(() -> {
				logger.error("Cliente com id {} não encontrado.", customerId);
				return new EntityNotFoundException("Cliente não encontrado");
			});

			if (sale.getEmployee() == null) {
				logger.error("O id do vendedor não pode ser nulo para a venda: {}", sale);
				throw new NullPointerException("O id do vendedor não pode ser nulo");
			}
			Long employeeId = sale.getEmployee();
			Employee emp = empRepository.findById(employeeId).orElseThrow(() -> {
				logger.error("Funcionário com id {} não encontrado.", employeeId);
				return new EntityNotFoundException("Funcionário não encontrado");
			});

			Map<Product, Integer> productAndQuantity = new HashMap<>();
			for (ProductSaleDTO product : sale.getProducts()) {
				if (product.getProductId() == null) {
					logger.error("O id do produto não pode ser nulo na venda: {}", sale);
					throw new NullPointerException("O id do produto não pode ser nulo");
				}

				if (product.getQuantity() == null) {
					logger.error("A quantidade não pode ser nula para o produto: {} na venda: {}", product, sale);
					throw new NullPointerException("A quantidade não pode ser nula");
				}

				Product productFinded = productRepository.findById(product.getProductId()).orElseThrow(() -> {
					logger.error("Produto com id {} não encontrado.", product.getProductId());
					return new EntityNotFoundException("Produto não encontrado");
				});

				productAndQuantity.put(productFinded, product.getQuantity());

				Sale saleToSave = new Sale(customer, emp, productFinded, product.getQuantity(), sale.getFunction());
				saleToSave.setReferenceDate(LocalDate.now().minusMonths(1));
				saleToSave.setDocumentNumber(newDocumentNumeber);

				sales.add(saleToSave);
				logger.debug("Venda criada: {}", saleToSave);
			}
		}

		List<Incentive> incentives = calculeIncentiveService.calculateIncentives(sales);
		logger.info("Processamento concluído. Incentives gerados: {}", incentives.size());
		return incentives;
	}

	public List<Sale> filter(SaleGroupReportInstructions instructions) {

		Specification<Sale> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(
					SaleSpecifications.byReferenceDateBetween(instructions.getStartDate(), instructions.getEndDate()));
		}

		if (instructions.getGroups() != null && !instructions.getGroups().isEmpty()) {
			spec = spec.and(SaleSpecifications.hasGroupIn(instructions.getGroups()));
		}

		var all = saleRepository.findAll(spec);
		System.out.println(all);
		return onlySales(all);
	}

	public List<Sale> filter(SaleStateReportInstructions instructions) {

		Specification<Sale> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(
					SaleSpecifications.byReferenceDateBetween(instructions.getStartDate(), instructions.getEndDate()));
		}

		if (instructions.getStates() != null && !instructions.getStates().isEmpty()) {
			spec = spec.and(SaleSpecifications.hasStateIn(instructions.getStates()));
		}

		var all = saleRepository.findAll(spec);
		System.out.println(all);
		return onlySales(all);
	}

	public List<Sale> filter(SaleFlagReportInstructions instructions) {

		Specification<Sale> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(
					SaleSpecifications.byReferenceDateBetween(instructions.getStartDate(), instructions.getEndDate()));
		}

		if (instructions.getFlags() != null && !instructions.getFlags().isEmpty()) {
			spec = spec.and(SaleSpecifications.hasFlagIn(instructions.getFlags()));
		}

		var all = saleRepository.findAll(spec);

		return onlySales(all);
	}

	public List<Sale> filter(SaleCustomerReportInstructions instructions) {

		Specification<Sale> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(
					SaleSpecifications.byReferenceDateBetween(instructions.getStartDate(), instructions.getEndDate()));
		}

		if (instructions.getCustomers() != null && !instructions.getCustomers().isEmpty()) {
			spec = spec.and(SaleSpecifications.hasCustomerIn(instructions.getCustomers()));
		}

		var all = saleRepository.findAll(spec);
		return onlySales(all);
	}

	public List<Sale> filter(SaleDateReportInstructions instructions) {

		Specification<Sale> spec = Specification.where(null);

		if (instructions.getStartDate() != null & instructions.getEndDate() != null) {
			spec = spec.and(
					SaleSpecifications.byReferenceDateBetween(instructions.getStartDate(), instructions.getEndDate()));
		}

		var all = saleRepository.findAll(spec);

		return onlySales(all);
	}

	public List<Sale> onlySales(List<Sale> list) {
		List<Sale> whereHasConsultant = list.stream()
				.filter(s -> !s.getCustomer().getMechanicApuration().getName().equals("Somente mecânicos"))
				.filter(s -> s.getEmployee().getFunctions().stream().anyMatch(
						f -> (f.getName().equals("Consultor Técnico")) && s.getFunction().contains("Consultor Técnico")
								|| (f.getName().equals("Consultor de Funilaria")
										&& s.getFunction().equals("Consultor de Funilaria"))))
				.toList();

		List<Sale> whereNotHasConsultant = list.stream()
				.filter(s -> s.getCustomer().getMechanicApuration().getName().equals("Somente mecânicos"))
				.filter(s -> s.getEmployee().getFunctions().stream().anyMatch(f -> f.getName().equals("Mecânico"))
						&& s.getFunction().contains("Mecânico"))
				.toList();

		List<Sale> allSales = new ArrayList<>();
		allSales.addAll(whereHasConsultant);
		allSales.addAll(whereNotHasConsultant);

		return allSales;
	}

	public SaleDetailsToUpdateDto findSalesByDocNum(Integer num) {
		List<Sale> sales = saleRepository.findByDocumentNumber(num);

		if (sales != null && !sales.isEmpty()) {
			Customer customer = sales.get(0).getCustomer();
			String customerName = customer.getFantasyName() + " " + customer.getCnpj();
			LocalDate referenceDate = sales.get(0).getReferenceDate();
			String date = MonthName.from(referenceDate);

			SaleDetailsToUpdateDto dto = new SaleDetailsToUpdateDto();
			dto.setDocumentNumber(num);
			dto.setCustomerName(customerName);
			dto.setDate(date);

			List<EmployeeSaleDto> consultantSales = sales.stream()
					.filter(s -> s.getFunction().equals("Consultor Técnico"))
					.map(s -> new EmployeeSaleDto(
							s.getEmployee().getId(),
							s.getEmployee().getName(),
							s.getProduct().getId(),
							s.getProduct().getProductCode(),
							s.getQuantity()))
					.toList();

			List<EmployeeSaleDto> tinkerSales = sales.stream()
					.filter(s -> s.getFunction().equals("Consultor de Funilaria"))
					.map(s -> new EmployeeSaleDto(
							s.getEmployee().getId(),
							s.getEmployee().getName(),
							s.getProduct().getId(),
							s.getProduct().getProductCode(),
							s.getQuantity()))
					.toList();

			List<EmployeeSaleDto> mechanicSales = sales.stream()
					.filter(s -> s.getFunction().equals("Mecânico"))
					.map(s -> new EmployeeSaleDto(
							s.getEmployee().getId(),
							s.getEmployee().getName(),
							s.getProduct().getId(),
							s.getProduct().getProductCode(),
							s.getQuantity()))
					.toList();

			dto.setConsultantSales(consultantSales);
			dto.setTinkerSales(tinkerSales);
			dto.setMechanicSales(mechanicSales);

			List<EmployeeDTO> consultants = new ArrayList<>();

			boolean isLinear = "Linear".equalsIgnoreCase(
					Optional.ofNullable(customer.getMechanicApuration())
							.map(MechanicApuration::getName)
							.orElse(""));

			if (!(isLinear && "Consultor Técnico".equalsIgnoreCase("Mecânico"))) {
				consultants = customer.getEmployees().stream()
						.filter(e -> e.getFunctions().stream()
								.anyMatch(f -> f.getName().equalsIgnoreCase("Consultor Técnico")))
						.map(e -> new EmployeeDTO(e.getId(), e.getName(), null))
						.toList();
			}

			List<EmployeeDTO> mechanics = new ArrayList<>();

			if (!(isLinear && "Mecânico".equalsIgnoreCase("Mecânico"))) {
				mechanics = customer.getEmployees().stream()
						.filter(e -> e.getFunctions().stream()
								.anyMatch(f -> f.getName().equalsIgnoreCase("Mecânico")))
						.map(e -> new EmployeeDTO(e.getId(), e.getName(), null))
						.toList();
			}

			List<EmployeeDTO> tinkers = new ArrayList<>();

			if (!(isLinear && "Consultor de Funilaria".equalsIgnoreCase("Mecânico"))) {
				tinkers = customer.getEmployees().stream()
						.filter(e -> e.getFunctions().stream()
								.anyMatch(f -> f.getName().equalsIgnoreCase("Consultor de Funilaria")))
						.map(e -> new EmployeeDTO(e.getId(), e.getName(), null))
						.toList();
			}

			dto.setConsultants(consultants);
			dto.setTinkers(tinkers);
			dto.setMechanics(mechanics);

			List<ProductDTO> products = customer.getGroup().getProducts().stream()
					.map(p -> new ProductDTO(p.getId(), p.getProductCode(), null))
					.toList();

			dto.setProducts(products);

			return dto;
		}

		throw new EntityNotFoundException("Vendas N° " + num + " não encontradas");
	}

	public void updateSales(UpdateSaleDto updateSaleDto) {
		logger.info("Dados para Atualizar: {}", updateSaleDto);

		Integer documentNumber = updateSaleDto.getDocumentNumber();

		List<Sale> currentSales = saleRepository.findByDocumentNumber(documentNumber);
		Customer customer = currentSales.get(0).getCustomer();
		LocalDate referenceDate = currentSales.get(0).getReferenceDate();
		logger.info("Data da venda: {}", referenceDate);

		List<UpdateSaleItemDTO> currentConsultantSales = updateSaleDto.getConsultantSales();
		List<UpdateSaleItemDTO> currentTinkerSales = updateSaleDto.getTinkerSales();
		List<UpdateSaleItemDTO> currentMechanicSales = updateSaleDto.getMechanicSales();

		List<Sale> newConsultantSales = new ArrayList<>();
		for (UpdateSaleItemDTO sale : currentConsultantSales) {
			Long employeeId = sale.getEmployeeId();
			Employee emp = empRepository.findById(employeeId).orElseThrow(() -> {
				return new EntityNotFoundException("Funcionário não encontrado");
			});

			Map<Product, Integer> productAndQuantity = new HashMap<>();

			if (sale.getProductId() == null) {
				throw new NullPointerException("O id do produto não pode ser nulo");
			}

			if (sale.getQuantity() == null) {
				throw new NullPointerException("A quantidade não pode ser nula");
			}

			Product productFinded = productRepository.findById(sale.getProductId()).orElseThrow(() -> {
				return new EntityNotFoundException("Produto não encontrado");
			});

			productAndQuantity.put(productFinded, sale.getQuantity());

			Sale saleToSave = new Sale(customer, emp, productFinded, sale.getQuantity(), "Consultor Técnico");
			saleToSave.setReferenceDate(referenceDate);
			saleToSave.setDocumentNumber(documentNumber);

			newConsultantSales.add(saleToSave);
		}

		List<Sale> newTinkerSales = new ArrayList<>();
		for (UpdateSaleItemDTO sale : currentTinkerSales) {
			Long employeeId = sale.getEmployeeId();
			Employee emp = empRepository.findById(employeeId).orElseThrow(() -> {
				return new EntityNotFoundException("Funcionário não encontrado");
			});

			Map<Product, Integer> productAndQuantity = new HashMap<>();

			if (sale.getProductId() == null) {
				throw new NullPointerException("O id do produto não pode ser nulo");
			}

			if (sale.getQuantity() == null) {
				throw new NullPointerException("A quantidade não pode ser nula");
			}

			Product productFinded = productRepository.findById(sale.getProductId()).orElseThrow(() -> {
				return new EntityNotFoundException("Produto não encontrado");
			});

			productAndQuantity.put(productFinded, sale.getQuantity());

			Sale saleToSave = new Sale(customer, emp, productFinded, sale.getQuantity(), "Consultor de Funilaria");
			saleToSave.setReferenceDate(referenceDate);
			saleToSave.setDocumentNumber(documentNumber);

			newTinkerSales.add(saleToSave);
		}

		List<Sale> newMechanicSales = new ArrayList<>();
		for (UpdateSaleItemDTO sale : currentMechanicSales) {
			if (sale.getEmployeeId() == null) {
				continue;
			}

			if (sale.getProductId() == null) {
				continue;
			}

			if (sale.getQuantity() == null) {
				continue;
			}

			Employee emp = empRepository.findById(sale.getEmployeeId()).orElseThrow(
					() -> new EntityNotFoundException("Premiado não encontrado"));

			Map<Product, Integer> productAndQuantity = new HashMap<>();

			Product productFinded = productRepository.findById(sale.getProductId()).orElseThrow(() -> {
				return new EntityNotFoundException("Produto não encontrado");
			});

			productAndQuantity.put(productFinded, sale.getQuantity());

			Sale saleToSave = new Sale(customer, emp, productFinded, sale.getQuantity(), "Mecânico");
			saleToSave.setReferenceDate(referenceDate);
			saleToSave.setDocumentNumber(documentNumber);

			newConsultantSales.add(saleToSave);
		}

		List<Sale> allNewSales = new ArrayList<>();
		allNewSales.addAll(newConsultantSales);
		allNewSales.addAll(newTinkerSales);
		allNewSales.addAll(newMechanicSales);

		calculeIncentiveService.updateIncentives(allNewSales, documentNumber);
	}

	@Transactional
	public void deleteByDocumentNumber(Integer documentNumber) {
		List<Sale> sales = saleRepository.findByDocumentNumber(documentNumber);
		List<Incentive> incentives = incentiveRepository.findBySaleDocumentNumber(documentNumber);

		if (sales.isEmpty() && incentives.isEmpty()) {
			throw new EntityNotFoundException(
					"Nenhuma venda ou incentivo encontrado para o documento: " + documentNumber);
		}

		boolean wasApproved = incentives.stream()
			.anyMatch(i -> i.getStatus() == br.ind.powerx.gestaoOperacional.model.enums.IncentiveStatus.APPROVED);
		
		Customer customer = null;
		if (!sales.isEmpty()) {
			customer = sales.get(0).getCustomer();
		} else if (!incentives.isEmpty()) {
			customer = incentives.get(0).getCustomer();
		}

		try {
			if (wasApproved && customer != null && !sales.isEmpty()) {
				logger.info("Devolvendo produtos ao estoque (documento estava aprovado)");
				for (Sale sale : sales) {
					productStockService.addToStock(customer, sale.getProduct(), sale.getQuantity());
				}
				logger.info("Produtos devolvidos ao estoque com sucesso");
				
				currentAccountService.updateCurrentAccount(customer);
				logger.info("Conta corrente recalculada após deleção");
			}
			
			saleRepository.deleteAll(sales);
			incentiveRepository.deleteAll(incentives);
			
			logger.info("Documento {} deletado com sucesso", documentNumber);
		} catch (Exception e) {
			throw new RuntimeException("Erro ao deletar vendas/incentivos: " + e.getMessage(), e);
		}
	}

	@Transactional
	public List<Sale> findByDocumentNumber(Integer documentNumber) {
		return saleRepository.findByDocumentNumber(documentNumber);
	}
}
