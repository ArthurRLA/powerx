package br.ind.powerx.gestaoOperacional.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.management.InvalidAttributeValueException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.model.RevenueItem;
import br.ind.powerx.gestaoOperacional.model.TablePrice;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerUniqueInfos;
import br.ind.powerx.gestaoOperacional.model.dtos.DeleteRevenueResponse;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueDetailsEditDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueItemDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueItemDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueItemReportDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueSaveDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueSpreadsheetDto;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueUpdateDto;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.RevenueCustomerReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.RevenueDateReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.RevenueGroupReportInstructions;
import br.ind.powerx.gestaoOperacional.model.dtos.report.instructions.RevenueUserReportInstructions;
import br.ind.powerx.gestaoOperacional.model.enums.OperationType;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import br.ind.powerx.gestaoOperacional.repositories.RevenueRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.RevenueSpecifications;
import br.ind.powerx.gestaoOperacional.util.Spreadsheets;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class RevenueService {
	private static final Logger logger = LoggerFactory.getLogger(RevenueService.class);

	private final RevenueRepository revenueRepository;
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;

	private final ProductStockService productStockService;
	private final CurrentAccountService currentAccountService;

	@Autowired
	public RevenueService(RevenueRepository revenueRepository, CustomerRepository customerRepository,
			ProductRepository productRepository, ProductStockService productStockService,
			CurrentAccountService currentAccountService) {
		this.revenueRepository = revenueRepository;
		this.customerRepository = customerRepository;
		this.productRepository = productRepository;
		this.productStockService = productStockService;
		this.currentAccountService = currentAccountService;
	}

	public Page<Revenue> findAll(Pageable pageable) {
		return revenueRepository.findAll(pageable);
	}

	public Optional<Revenue> findById(Long id) {
		return revenueRepository.findById(id);
	}

	@Transactional
	public void save(RevenueSaveDto revenueDto) throws InvalidAttributeValueException {

		Integer invoiceNumber = revenueDto.getInvoiceNumber();

		OperationType operationType = revenueDto.getOperationType();

		Long customerId = revenueDto.getCustomerId();

		Customer customer = customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("Cliente com id: " + customerId + " não encontrado"));

		Optional<Revenue> checkRevenue = revenueRepository.findByInvoiceNumberAndCustomerAndOperationType(invoiceNumber,
				customer, operationType);

		if (checkRevenue.isPresent()) {
			throw new InvalidAttributeValueException("Faturamento com n° de nota " + invoiceNumber + " Já existe");
		}

		Revenue revenue = fromSaveDto(revenueDto);

		revenueRepository.save(revenue);
		productStockService.updateStock(List.of(revenue));
		currentAccountService.updateCurrentAccount(List.of(revenue));

	}

	private Revenue fromSaveDto(RevenueSaveDto revenueDto) {
		Revenue revenue = new Revenue();
		Customer customer = customerById(revenueDto.getCustomerId());
		User user = customer.getUser();
		revenue.setInvoiceNumber(revenueDto.getInvoiceNumber());
		revenue.setUser(user);
		revenue.setOperationType(revenueDto.getOperationType());
		revenue.setDate(revenueDto.getDate());
		revenue.setCustomer(customer);
		List<RevenueItem> items = fromRevenueDto(revenue, revenueDto.getItems());
		items.forEach(revenue::addRevenueItem);
		items.forEach(item -> item.setSubTotal(item.subTotal()));
		revenue.setTotal(revenue.totalValue());
		return revenue;
	}

	private List<RevenueItem> fromRevenueDto(Revenue revenue, List<RevenueItemDto> revenueItems) {
		return revenueItems.stream().filter(itemDto -> itemDto.getQuantity() != 0).map(itemDto -> new RevenueItem(null,
				null, productById(itemDto.getProductId()), itemDto.getQuantity(), null)).toList();
	}

	private Customer customerById(Long customerId) {
		return customerRepository.findById(customerId)
				.orElseThrow(() -> new EntityNotFoundException("cliente não encotrado"));
	}

	private Product productById(Long productId) {
		return productRepository.findById(productId)
				.orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));
	}

	public void saveBySpreadsheet(MultipartFile file) throws IOException {
		List<RevenueSpreadsheetDto> dtos = new ArrayList<>();
		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			int lastRowWithData = Spreadsheets.getLastRowWithData(sheet);

			Iterator<Row> rowIterator = sheet.iterator();
			boolean isHeader = true;

			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if (isHeader) {
					isHeader = false;
					continue;
				}

				if (row.getRowNum() > lastRowWithData) {
					break;
				}

				if (Spreadsheets.isRowEmpty(row)) {
					System.out.println("Linha " + row.getRowNum() + " vazia - pulando");
					continue;
				}

				try {
					Integer invoiceNumber = Spreadsheets.getIntegerCellValue(row.getCell(0));
					String operationType = Spreadsheets.getStringCellValue(row.getCell(2));
					String customerCnpj = Spreadsheets.getStringCellValue(row.getCell(3));
					Long userId = Spreadsheets.parseLongFromCell(row.getCell(4));
					String productCode = Spreadsheets.getStringCellValue(row.getCell(5));
					Integer quantity = Spreadsheets.getIntegerCellValue(row.getCell(6));

					LocalDate date = null;
					Cell dobCell = row.getCell(1);
					if (dobCell != null) {
						if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {
							date = dobCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						} else if (dobCell.getCellType() == CellType.STRING) {
							try {
								date = LocalDate.parse(dobCell.getStringCellValue().toString());
							} catch (DateTimeParseException e) {
								e.printStackTrace();
								throw new DateTimeParseException("erro de parse", customerCnpj, invoiceNumber);
							}
						}
					}

					RevenueSpreadsheetDto revenueDto = new RevenueSpreadsheetDto(invoiceNumber, date, operationType,
							customerCnpj, userId, productCode, quantity);
					dtos.add(revenueDto);

				} catch (Exception e) {
					System.err.println("Erro crítico na linha " + row.getRowNum() + ": " + e.getMessage());
					e.printStackTrace();
				}
			}
		}

		List<Revenue> revenues = fromRevenueDto(dtos);
		revenues.forEach(System.out::println);
		revenueRepository.saveAll(revenues);
		productStockService.updateStock(revenues);

	}

	private List<Revenue> fromRevenueDto(List<RevenueSpreadsheetDto> revenuesDto) {
		List<Revenue> revenues = new ArrayList<>();
		List<Integer> numbers = new ArrayList<>();
		for (RevenueSpreadsheetDto dto : revenuesDto) {

			Revenue revenue;

			Integer invoiceNumber = dto.getInvoiceNumber();
			Product product = productRepository.findByProductCode(dto.getProductCode());
			Integer quantity = dto.getQuantity();

			if (invoiceNumber == null || invoiceNumber <= 0) {
				throw new IllegalArgumentException("Numero da nota invalido");
			}

			if (product == null) {
				throw new EntityNotFoundException("produto não encontrado");
			}

			if (quantity == null || quantity <= 0) {
				throw new IllegalArgumentException("Quantidade invalida");
			}

			if (numbers.contains(invoiceNumber)) {
				var revenuesFiltered = revenues.stream().filter(r -> r.getInvoiceNumber().equals(invoiceNumber))
						.toList();

				if (revenuesFiltered.size() > 1) {
					throw new IllegalStateException(
							"Não será possível cadastrar mais de um faturamento com a mesma nota");
				}

				revenue = revenuesFiltered.get(0);

				if (!dto.getCustomerCnpj().equals(revenue.getCustomer().getCnpj())) {
					throw new IllegalArgumentException("uma mesma nota não pode estar emitida contr um mesmo cnpj");
				}

				if (!revenue.getOperationType().getName().equals(dto.getOperationType())) {
					throw new IllegalArgumentException("uma mesma nota não pode ter duas naturezas diferentes");
				}

				if (!revenue.getDate().equals(dto.getDate())) {
					throw new IllegalArgumentException("uma mesma nota não pode ter duas datas diferentes");
				}

				if (!revenue.getDate().equals(dto.getDate())) {
					throw new IllegalArgumentException("uma mesma nota não pode ter duas datas diferentes");
				}

				RevenueItem item = new RevenueItem(null, revenue, product, quantity, null);
				item.setSubTotal(item.subTotal());

				revenue.addRevenueItem(item);
				continue;
			}

			Customer customer = customerRepository.findByCnpj(dto.getCustomerCnpj());
			User user = customer.getUser();
			OperationType operationType = dto.getOperationType().equals("SALE") ? OperationType.SALE
					: OperationType.RETURN;
			LocalDate date = dto.getDate();

			if (customer.equals(null)) {
				throw new EntityNotFoundException("produto não encontrado");
			}

			if (user.equals(null)) {
				throw new EntityNotFoundException("usuario não encontrado");
			}

			if (date.equals(null)) {
				throw new EntityNotFoundException("data invalida");
			}

			revenue = new Revenue();
			revenue.setInvoiceNumber(invoiceNumber);
			revenue.setUser(user);
			revenue.setOperationType(operationType);
			revenue.setDate(date);
			revenue.setCustomer(customer);

			RevenueItem item = new RevenueItem();
			item.setRevenue(revenue);
			item.setProduct(product);
			item.setQuantity(quantity);
			item.setSubTotal(item.subTotal());

			revenue.addRevenueItem(item);
			revenue.setTotal(revenue.totalValue());
			revenues.add(revenue);
			numbers.add(invoiceNumber);
		}
		return revenues;
	}

	public RevenueDetailsDto getRevenueDetailsDto(Long id) {
		return findById(id).stream().map(r -> {
			var dto = new RevenueDetailsDto();
			dto.setId(r.getId());
			dto.setInvoiceNumber(r.getInvoiceNumber());
			dto.setUnysoftId(r.getUnysoftId());
			dto.setUser(r.getUser().getName());

			var customerUniqueInfos = new CustomerUniqueInfos();
			customerUniqueInfos.setId(r.getCustomer().getId());
			customerUniqueInfos.setFantasyName(r.getCustomer().getFantasyName());
			customerUniqueInfos.setCnpj(r.getCustomer().getCnpj());

			dto.setCustomer(customerUniqueInfos);
			dto.setOperationType(r.getOperationType().getName());
			dto.setDate(r.getDate());

			var items = new ArrayList<RevenueItemDetailsDto>();
			items.addAll(r.getRevenueItems().stream().map(i -> {
				var item = new RevenueItemDetailsDto();
				item.setProductCode(i.getProduct().getProductCode());
				item.setProductName(i.getProduct().getProductName());
				item.setQuantity(i.getQuantity());

				TablePrice tablePrice = i.getRevenue().getCustomer().getTables().stream()
						.filter(t -> t.getProduct().equals(i.getProduct())).findFirst()
						.orElse(new TablePrice(null, r.getCustomer(), i.getProduct(), BigDecimal.ZERO));

				item.setPrice(tablePrice.getPrice());
				item.setSubTotal(item.getSubTotalValue());

				return item;
			}).toList());

			dto.setItems(items);
			dto.setBalance(dto.getTotalBalance());

			return dto;
		}).findFirst().orElseThrow(() -> new EntityNotFoundException("Receita não encontrada"));

	}

	public RevenueDetailsEditDto getRevenueEditDetailsDto(Long id) {
		return findById(id).stream().map(r -> {
			var dto = new RevenueDetailsEditDto();
			dto.setId(r.getId());
			dto.setInvoiceNumber(r.getInvoiceNumber());
			dto.setUnysoftId(r.getUnysoftId());
			dto.setOperationType(r.getOperationType());
			dto.setCustomer(r.getCustomer().getId());
			dto.setDate(r.getDate());

			var items = r.getRevenueItems().stream().map(i -> {
				var itemDto = new RevenueItemDto();
				itemDto.setProductId(i.getProduct().getId());
				itemDto.setQuantity(i.getQuantity());

				return itemDto;
			}).toList();

			dto.setItems(items);

			var operationTypes = Arrays.asList(OperationType.values());

			var customers = customerRepository.findAll().stream().map(c -> {
				var cui = new CustomerUniqueInfos();
				cui.setId(c.getId());
				cui.setFantasyName(c.getFantasyName());
				cui.setCnpj(c.getCnpj());

				return cui;
			}).toList();

			var products = productRepository.findAll().stream()
					.map(p -> new ProductDTO(p.getId(), p.getProductCode(), p.getProductName())).toList();

			dto.setOperationTypes(operationTypes);
			dto.setCustomers(customers);
			dto.setProducts(products);

			return dto;
		}).findFirst().orElseThrow();
	}

	@Transactional
	public void update(Long id, RevenueUpdateDto dto) {
		Revenue revenue = revenueRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Faturamento com id " + id + " não encontrado"));

		Customer oldCustomer = revenue.getCustomer();
		OperationType oldOpType = revenue.getOperationType();
		List<RevenueItem> oldItems = revenue.getRevenueItems().stream().map(r -> {
			RevenueItem copy = new RevenueItem();
			copy.setProduct(r.getProduct());
			copy.setQuantity(r.getQuantity());
			return copy;
		}).toList();

		Customer newCustomer = customerById(dto.getCustomerId());
		OperationType newOpType = dto.getOperationType();

		List<RevenueItem> newItems = dto.getItems().stream().filter(i -> i.getQuantity() > 0).map(iDto -> {
			RevenueItem ni = new RevenueItem();
			ni.setProduct(productById(iDto.getProductId()));
			ni.setQuantity(iDto.getQuantity());
			ni.setRevenue(revenue);
			ni.setSubTotal(ni.subTotal());
			return ni;
		}).toList();

		if (revenue.getInvoiceNumber() != dto.getInvoiceNumber()) {
			if (invoiceNumberExistsOnOther(dto.getInvoiceNumber(), newCustomer, dto.getOperationType(), revenue)) {
				throw new IllegalArgumentException("N° de nota fiscal já existe");
			}
		}

		revenue.setCustomer(newCustomer);
		revenue.setOperationType(newOpType);
		revenue.setDate(dto.getDate());
		revenue.setInvoiceNumber(dto.getInvoiceNumber());
		revenue.setUnysoftId(dto.getUnysoftId());

		revenue.getRevenueItems().clear();
		revenue.getRevenueItems().addAll(newItems);
		revenue.setTotal(revenue.totalValue());
		revenueRepository.save(revenue);

		productStockService.adjustStockOnUpdate(oldItems, oldOpType, oldCustomer, newItems, newOpType, newCustomer);

		currentAccountService.updateCurrentAccount(List.of(revenue));
	}

	private boolean invoiceNumberExistsOnOther(Integer invoiceNumberToCheck, Customer customer,
			OperationType operationType, Revenue revenueToCheck) {
		Optional<Revenue> revenueFinded = revenueRepository
				.findByInvoiceNumberAndCustomerAndOperationType(invoiceNumberToCheck, customer, operationType);

		if (revenueFinded.isEmpty())
			return false;

		return revenueFinded.get().equals(revenueToCheck) ? false : true;

	}

	@Transactional
	public DeleteRevenueResponse deleteRevenue(Long id) {
		var revenue = findById(id);

		if (revenue.isEmpty()) {
			throw new EntityNotFoundException("Faturamento com id: " + id + " não encontrado");
		}

		var deleteRevenueResponse = revenue.stream().map(r -> {
			var dto = new DeleteRevenueResponse();

			var customer = r.getCustomer();

			var customerId = customer.getId();
			var cnpj = customer.getCnpj();
			var fantasyName = customer.getFantasyName();

			var customerUniqueInfos = new CustomerUniqueInfos(customerId, cnpj, fantasyName);

			var items = new ArrayList<RevenueItemDetailsDto>();
			items.addAll(r.getRevenueItems().stream().map(i -> {
				var item = new RevenueItemDetailsDto();
				item.setProductCode(i.getProduct().getProductCode());
				item.setProductName(i.getProduct().getProductName());
				item.setQuantity(i.getQuantity());

				TablePrice tablePrice = i.getRevenue().getCustomer().getTables().stream()
						.filter(t -> t.getProduct().equals(i.getProduct())).findFirst()
						.orElse(new TablePrice(null, r.getCustomer(), i.getProduct(), BigDecimal.ZERO));

				item.setPrice(tablePrice.getPrice());
				item.setSubTotal(item.getSubTotalValue());

				return item;
			}).toList());

			dto.setCustomer(customerUniqueInfos);
			dto.setItems(items);

			return dto;
		}).findFirst().orElseThrow();

		var removeFromStock = ProductItemConverter.toStockItem(revenue.get());
		var customer = revenue.get().getCustomer();

		productStockService.removeFromStock(revenue.get().getCustomer().getProductStock(), removeFromStock);

		revenueRepository.delete(revenue.get());

		deleteRevenueResponse.setMessage("Faturamento excluido com Sucesso!");

		currentAccountService.updateCurrentAccount(customer);

		return deleteRevenueResponse;
	}

	public Page<Revenue> findByInvoiceNumber(Integer invoiceNumber, Pageable pageable) {
		return revenueRepository.findAllByInvoiceNumber(invoiceNumber, pageable);
	}

	public Page<Revenue> filter(LocalDate start, LocalDate end, List<Long> userIds, List<Long> customerIds, List<Long> groupIds,
			List<OperationType> operationTypes, Pageable pageable) {
		Specification<Revenue> spec = Specification.where(null);

		if (userIds != null && !userIds.isEmpty()) {
			spec = spec.and(RevenueSpecifications.usersIn(userIds));
		}

		if (customerIds != null && !customerIds.isEmpty()) {
			spec = spec.and(RevenueSpecifications.customersIn(customerIds));
		}

		if (groupIds != null && !groupIds.isEmpty()) {
			spec = spec.and(RevenueSpecifications.groupsIn(groupIds));
		}

		if (operationTypes != null && !operationTypes.isEmpty()) {
			spec = spec.and(RevenueSpecifications.operationTypesIn(operationTypes));
		}

		if (start != null && end != null ) {
			spec = spec.and(RevenueSpecifications.byReferenceDateBetween(start, end));
		}
		
		return revenueRepository.findAll(spec, pageable);
		
	}
	
	public List<Revenue> filter(RevenueUserReportInstructions revenueUserReportInstructions) {
		Specification<Revenue> spec = Specification.where(null);

		if (revenueUserReportInstructions.getStartDate() != null && revenueUserReportInstructions.getEndDate() != null ) {
			spec = spec.and(
					RevenueSpecifications
						.byReferenceDateBetween(revenueUserReportInstructions.getStartDate(), revenueUserReportInstructions.getEndDate()));
		}
		
		if (revenueUserReportInstructions.getUsers() != null && !revenueUserReportInstructions.getUsers().isEmpty()) {
			spec = spec.and(RevenueSpecifications.usersIn(revenueUserReportInstructions.getUsers()));
		}

		if (revenueUserReportInstructions.getOperationTypes() != null && !revenueUserReportInstructions.getOperationTypes().isEmpty()) {
			spec = spec.and(RevenueSpecifications.operationTypesIn(revenueUserReportInstructions.getOperationTypes()));
		}

		return revenueRepository.findAll(spec);
		
	}
	
	public List<Revenue> filter(RevenueGroupReportInstructions revenueUserReportInstructions) {
		Specification<Revenue> spec = Specification.where(null);

		if (revenueUserReportInstructions.getStartDate() != null && revenueUserReportInstructions.getEndDate() != null ) {
			spec = spec.and(
					RevenueSpecifications
						.byReferenceDateBetween(revenueUserReportInstructions.getStartDate(), revenueUserReportInstructions.getEndDate()));
		}
		
		if (revenueUserReportInstructions.getGroups() != null && !revenueUserReportInstructions.getGroups().isEmpty()) {
			spec = spec.and(RevenueSpecifications.groupsIn(revenueUserReportInstructions.getGroups()));
		}

		if (revenueUserReportInstructions.getOperationTypes() != null && !revenueUserReportInstructions.getOperationTypes().isEmpty()) {
			spec = spec.and(RevenueSpecifications.operationTypesIn(revenueUserReportInstructions.getOperationTypes()));
		}
		
		return revenueRepository.findAll(spec);
		
	}
	
	public List<Revenue> filter(RevenueCustomerReportInstructions revenueUserReportInstructions) {
		Specification<Revenue> spec = Specification.where(null);

		if (revenueUserReportInstructions.getStartDate() != null && revenueUserReportInstructions.getEndDate() != null ) {
			spec = spec.and(
					RevenueSpecifications
						.byReferenceDateBetween(revenueUserReportInstructions.getStartDate(), revenueUserReportInstructions.getEndDate()));
		}
		
		if (revenueUserReportInstructions.getCustomers() != null && !revenueUserReportInstructions.getCustomers().isEmpty()) {
			spec = spec.and(RevenueSpecifications.customersIn(revenueUserReportInstructions.getCustomers()));
		}

		if (revenueUserReportInstructions.getOperationTypes() != null && !revenueUserReportInstructions.getOperationTypes().isEmpty()) {
			spec = spec.and(RevenueSpecifications.operationTypesIn(revenueUserReportInstructions.getOperationTypes()));
		}

		return revenueRepository.findAll(spec);
		
	}
	
	public List<Revenue> filter(RevenueDateReportInstructions revenueUserReportInstructions) {
		Specification<Revenue> spec = Specification.where(null);

		if (revenueUserReportInstructions.getStartDate() != null && revenueUserReportInstructions.getEndDate() != null ) {
			spec = spec.and(
					RevenueSpecifications
						.byReferenceDateBetween(revenueUserReportInstructions.getStartDate(), revenueUserReportInstructions.getEndDate()));
		}
		
		if (revenueUserReportInstructions.getOperationTypes() != null && !revenueUserReportInstructions.getOperationTypes().isEmpty()) {
			spec = spec.and(RevenueSpecifications.operationTypesIn(revenueUserReportInstructions.getOperationTypes()));
		}

		return revenueRepository.findAll(spec);
		
	}

	public List<RevenueItemReportDTO> flattenRevenuesToReportDTOs(List<Revenue> revenues) {
    return revenues.stream()
        .flatMap(revenue -> revenue.getRevenueItems().stream()
            .map(item -> new RevenueItemReportDTO(
                revenue.getDate().getMonthValue() + "/" + revenue.getDate().getYear(),  
                revenue.getInvoiceNumber(),
				revenue.getOperationType().getName(),
                revenue.getCustomer().getFantasyName(),
                revenue.getCustomer().getCnpj(),  
                revenue.getUser().getName(),
                revenue.getUser().getCpf(),  
                item.getProduct().getProductCode(),
                item.getProduct().getProductName(),
                item.getQuantity(),
                item.getSubTotal() 
            ))
        )
        .collect(Collectors.toList());
}
}
