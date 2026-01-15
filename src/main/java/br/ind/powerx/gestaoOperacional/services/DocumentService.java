package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerDate;
import br.ind.powerx.gestaoOperacional.model.dtos.DocumentDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.DocumentDetailsProductResume;
import br.ind.powerx.gestaoOperacional.model.dtos.DocumentDetailsSale;
import br.ind.powerx.gestaoOperacional.model.dtos.DocumentDetailsSaleProduct;
import br.ind.powerx.gestaoOperacional.model.dtos.IncentiveDTO;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.repositories.IncentiveRepository;
import br.ind.powerx.gestaoOperacional.repositories.SaleRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.IncentiveSpecifications;

@Service
public class DocumentService {

	private final SaleRepository saleRepository;

	private final IncentiveRepository incentiveRepository;

	@Autowired
	public DocumentService(SaleRepository saleRepository, IncentiveRepository incentiveRepository) {
		this.saleRepository = saleRepository;
		this.incentiveRepository = incentiveRepository;
	}

	public List<Integer> findAllDocumentNumbers() {
		List<Integer> salesDocumentNumbers = saleRepository.findDistinctDocumentNumbers();
		List<Integer> incentivesDocumentNumbers = incentiveRepository.findDistinctDocumentNumbers();

		Set<Integer> allDocumentNumbers = new HashSet<>();
		allDocumentNumbers.addAll(incentivesDocumentNumbers);
		allDocumentNumbers.addAll(salesDocumentNumbers);

		return allDocumentNumbers.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
	}

	public DocumentDetailsDto getDocumentDetails(Integer documentNumber) {
		List<Sale> sales = saleRepository.findByDocumentNumber(documentNumber);
		List<Incentive> incentives = incentiveRepository.findBySaleDocumentNumber(documentNumber);

		if (incentives.isEmpty()) {
			throw new IllegalArgumentException("Nenhum incentivo encontrado para o documento " + documentNumber);
		}

		DocumentDetailsDto documentDetailsDto = new DocumentDetailsDto();

		LocalDate localDate = incentives.get(0).getReferenceDate();
		Customer customer = incentives.get(0).getCustomer();
		State state = incentives.get(0).getState();
		PaymentMethod paymentMethod = incentives.get(0).getPaymentMethod();

		String date = MonthName.from(localDate);
		String customerName = customer.getFantasyName();
		String customerCnpj = customer.getCnpj();
		String paymentMethodName = paymentMethod.getName();

		List<Sale> consultantSales = sales.stream()
				.filter(s -> s.getFunction().equals("Consultor Técnico"))
				.toList();

		List<Sale> tinkerSales = sales.stream()
				.filter(s -> s.getFunction().equals("Consultor de Funilaria"))
				.toList();

		List<Sale> mechanicSales = sales.stream()
				.filter(s -> s.getFunction().equals("Mecânico"))
				.toList();

		Integer salesQuantity = consultantSales.stream()
				.map(Sale::getQuantity)
				.reduce(0, Integer::sum);

		Integer tinkerQuantity = tinkerSales.stream()
				.map(Sale::getQuantity)
				.reduce(0, Integer::sum);

		Integer applicationQuantity = mechanicSales.stream()
				.map(Sale::getQuantity)
				.reduce(0, Integer::sum);

		Map<Employee, Map<Product, Integer>> consultantSalesGroupedByEmployee = consultantSales.stream()
				.collect(Collectors.groupingBy(Sale::getEmployee,
						Collectors.groupingBy(Sale::getProduct,
								Collectors.mapping(Sale::getQuantity, Collectors.reducing(0, Integer::sum)))));

		Map<Employee, Map<Product, Integer>> tinkerSalesGroupedByEmployee = tinkerSales.stream()
				.collect(Collectors.groupingBy(Sale::getEmployee,
						Collectors.groupingBy(Sale::getProduct,
								Collectors.mapping(Sale::getQuantity, Collectors.reducing(0, Integer::sum)))));

		Map<Employee, Map<Product, Integer>> mechanicSalesGroupedByEmployee = mechanicSales.stream()
				.collect(Collectors.groupingBy(Sale::getEmployee,
						Collectors.groupingBy(Sale::getProduct,
								Collectors.mapping(Sale::getQuantity, Collectors.reducing(0, Integer::sum)))));

		List<DocumentDetailsSale> consultantSalesDto = buildSalesDto(consultantSalesGroupedByEmployee,
				"Consultor Técnico");

		List<DocumentDetailsSale> tinkerSalesDto = buildSalesDto(tinkerSalesGroupedByEmployee,
				"Consultor de Funilaria");

		List<DocumentDetailsSale> mechanicSalesDto = buildSalesDto(mechanicSalesGroupedByEmployee, "Mecânico");

		Map<Product, Map<String, Integer>> productsGroupedByFunction = sales.stream()
				.collect(Collectors.groupingBy(Sale::getProduct,
						Collectors.groupingBy(Sale::getFunction,
								Collectors.mapping(
										Sale::getQuantity,
										Collectors.reducing(0, Integer::sum)))));

		List<DocumentDetailsProductResume> productsResume = productsGroupedByFunction.entrySet().stream()
				.map(entry -> {
					Product product = entry.getKey();
					Map<String, Integer> functionQtd = entry.getValue();

					Integer consultantTotal = 0;
					Integer mechanicTotal = 0;

					for (Map.Entry<String, Integer> qtd : functionQtd.entrySet()) {
						String funcName = qtd.getKey();
						Integer quantity = qtd.getValue();

						if (funcName.equals("Consultor Técnico") ||funcName.equals("Consultor de Funilaria") ) {
							consultantTotal += quantity;
						} else if (funcName.equals("Mecânico")) {
							mechanicTotal += quantity;
						}
					}

					DocumentDetailsProductResume currentDto = new DocumentDetailsProductResume(
							product.getProductCode(),
							product.getProductName(),
							consultantTotal,
							mechanicTotal);

					return currentDto;

				})
				.toList();

		List<IncentiveDTO> ccIncentives = incentives.stream()
				.filter(i -> i.getApurationType().getName().equals("Conta Corrente"))
				.map(incentive -> new IncentiveDTO(
						incentive.getCpf(),
						incentive.getEmployee().getName(),
						incentive.getIncentiveValue(),
						incentive.getEmployeeFunction().getName()))
				.toList();

		List<IncentiveDTO> nfsIncentives = incentives.stream()
				.filter(i -> i.getApurationType().getName().equals("NF Serviço"))
				.map(incentive -> new IncentiveDTO(
						incentive.getCpf(),
						incentive.getEmployee().getName(),
						incentive.getIncentiveValue(),
						incentive.getEmployeeFunction().getName()))
				.toList();

		BigDecimal totalCc = calculeTotalValue(ccIncentives);
		BigDecimal totalNfs = calculeTotalValue(nfsIncentives);

		documentDetailsDto.setDocumentNumber(documentNumber);
		documentDetailsDto.setDate(date);
		documentDetailsDto.setCustomerName(customerName);
		documentDetailsDto.setCustomerCnpj(customerCnpj);
		documentDetailsDto.setState(state);
		documentDetailsDto.setPaymentMethod(paymentMethodName);
		documentDetailsDto.setSalesTotal(salesQuantity);
		documentDetailsDto.setTinkerTotal(tinkerQuantity);
		documentDetailsDto.setAplicationsTotal(applicationQuantity);
		documentDetailsDto.setConsultantSales(consultantSalesDto);
		documentDetailsDto.setTinkerSales(tinkerSalesDto);
		documentDetailsDto.setMechanicSales(mechanicSalesDto);
		documentDetailsDto.setProductsResume(productsResume);
		documentDetailsDto.setCcIncentives(ccIncentives);
		documentDetailsDto.setNfsIncentives(nfsIncentives);
		documentDetailsDto.setTotalCc(totalCc);
		documentDetailsDto.setTotalNfs(totalNfs);

		return documentDetailsDto;
	}

	private BigDecimal calculeTotalValue(List<IncentiveDTO> incentives) {
		return incentives.stream()
				.map(IncentiveDTO::getIncentiveValue)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	private List<DocumentDetailsSale> buildSalesDto(Map<Employee, Map<Product, Integer>> salesGroupedByEmployee,
			String function) {
		return salesGroupedByEmployee.entrySet().stream()
				.map(entry -> {
					Employee emp = entry.getKey();
					Map<Product, Integer> quantities = entry.getValue();

					DocumentDetailsSale dto = new DocumentDetailsSale();
					dto.setEmployeeName(emp.getName());
					dto.setFunction(function);

					int total = 0;
					List<DocumentDetailsSaleProduct> productsDto = new ArrayList<>();

					for (Map.Entry<Product, Integer> qtdEntry : quantities.entrySet()) {
						Product product = qtdEntry.getKey();
						Integer qtd = qtdEntry.getValue();

						DocumentDetailsSaleProduct sp = new DocumentDetailsSaleProduct();
						sp.setProductCode(product.getProductCode());
						sp.setProductName(product.getProductName());
						sp.setQuantity(qtd);

						productsDto.add(sp);
						total += qtd;
					}

					dto.setTotalQuantity(total);
					dto.setProducts(productsDto);

					return dto;
				})
				.toList();
	}

	public List<Integer> findAllDocumentNumbersByUser(User user) {
		List<Integer> incentivesDocumentNumbers = incentiveRepository.findDistinctDocumentNumbersByUserId(user.getId());

		return incentivesDocumentNumbers.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
	}

	public Map<Integer, CustomerDate> getCustomersByDocument(Collection<Integer> documentNumbers) {
		Map<Integer, CustomerDate> documentCustomer = new LinkedHashMap<>();
		for (Integer documentNumber : documentNumbers) {
			var incentives = incentiveRepository.findBySaleDocumentNumber(documentNumber);
			Customer customer = incentives.get(0).getCustomer();
			String customerName = customer.getFantasyName();
			LocalDate date = incentives.get(0).getReferenceDate();
			CustomerDate customerDate = new CustomerDate(customerName, date);
			documentCustomer.put(documentNumber, customerDate);
		}
		return documentCustomer;
	}

	public Page<Entry<Integer, CustomerDate>> getPageDoucmentFiltered(int page, int size, LocalDate start,
			LocalDate end, List<Long> userIds, List<Long> customerIds) {

		Specification<Incentive> spec = Specification.where(null);

		if (start != null && end != null) {
			spec = spec.and(IncentiveSpecifications.byReferenceDateBetween(start, end));
		}

		if (userIds != null && !userIds.isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasUserIn(userIds));
		}

		if (customerIds != null && !customerIds.isEmpty()) {
			spec = spec.and(IncentiveSpecifications.hasCustomerIn(customerIds));
		}

		List<Incentive> incentivesFiltered = incentiveRepository.findAll(spec);
		Set<Integer> documentNumbersSet = new LinkedHashSet<>();
		incentivesFiltered.forEach(i -> documentNumbersSet.add(i.getSaleDocumentNumber()));

		List<Integer> sortedDocumentNumbers = new ArrayList<>(documentNumbersSet);
		sortedDocumentNumbers.sort(Comparator.reverseOrder());

		Map<Integer, CustomerDate> fullMap = getCustomersByDocument(sortedDocumentNumbers);

		List<Map.Entry<Integer, CustomerDate>> entries = new ArrayList<>(fullMap.entrySet());

		sortedDocumentNumbers.forEach(System.out::println);

		Page<Map.Entry<Integer, CustomerDate>> pageEntries = new PageImpl<>(
				entries.subList(page * size, Math.min((page + 1) * size, entries.size())),
				PageRequest.of(page, size),
				entries.size());

		return pageEntries;
	}

}
