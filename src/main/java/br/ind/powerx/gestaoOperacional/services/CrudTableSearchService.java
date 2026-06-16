package br.ind.powerx.gestaoOperacional.services;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.TablePrice;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchCustomerRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchEmployeeRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchFlagRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchGroupRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchIndustryRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchPaymentRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchProductRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchTablePriceRow;
import br.ind.powerx.gestaoOperacional.model.dtos.crud.CrudSearchUserRow;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FlagRepository;
import br.ind.powerx.gestaoOperacional.repositories.GroupRepository;
import br.ind.powerx.gestaoOperacional.repositories.IndustryRepository;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import br.ind.powerx.gestaoOperacional.repositories.TablePriceRepository;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;

@Service
public class CrudTableSearchService {

	private final UserRepository userRepository;
	private final CustomerRepository customerRepository;
	private final ProductRepository productRepository;
	private final EmployeeRepository employeeRepository;
	private final GroupRepository groupRepository;
	private final PaymentMethodRepository paymentMethodRepository;
	private final IndustryRepository industryRepository;
	private final FlagRepository flagRepository;
	private final TablePriceRepository tablePriceRepository;

	public CrudTableSearchService(UserRepository userRepository, CustomerRepository customerRepository,
			ProductRepository productRepository, EmployeeRepository employeeRepository, GroupRepository groupRepository,
			PaymentMethodRepository paymentMethodRepository, IndustryRepository industryRepository,
			FlagRepository flagRepository, TablePriceRepository tablePriceRepository) {
		this.userRepository = userRepository;
		this.customerRepository = customerRepository;
		this.productRepository = productRepository;
		this.employeeRepository = employeeRepository;
		this.groupRepository = groupRepository;
		this.paymentMethodRepository = paymentMethodRepository;
		this.industryRepository = industryRepository;
		this.flagRepository = flagRepository;
		this.tablePriceRepository = tablePriceRepository;
	}

	public List<CrudSearchUserRow> searchUsers(String q) {
		return userRepository.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
				.map(u -> new CrudSearchUserRow(u.getId(), u.getName())).collect(Collectors.toList());
	}

	public List<CrudSearchCustomerRow> searchCustomers(String q) {
		return customerRepository.searchForCrudTableByFantasyName(q).stream().map(this::toCustomerRow)
				.collect(Collectors.toList());
	}

	private CrudSearchCustomerRow toCustomerRow(Customer c) {
		return new CrudSearchCustomerRow(c.getId(), c.getFantasyName(), c.getUnysoftCode(), c.getCnpj(),
				c.getRegisteredName(), c.getAddress(), c.isActive(),
				c.getUser() != null ? c.getUser().getName() : "-",
				c.getGroup() != null ? c.getGroup().getName() : "-",
				c.getMechanicApuration() != null ? c.getMechanicApuration().getName() : "-",
				c.getIndustry() != null ? c.getIndustry().getName() : "-",
				c.getFlag() != null ? c.getFlag().getName() : "-");
	}

	public List<CrudSearchProductRow> searchProducts(String q) {
		return productRepository.findByProductNameContainingIgnoreCaseOrderByProductCodeAsc(q).stream()
				.map(p -> new CrudSearchProductRow(p.getId(), p.getProductCode(), p.getProductName()))
				.collect(Collectors.toList());
	}

	public List<CrudSearchEmployeeRow> searchEmployees(String q) {
		return employeeRepository.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
				.map(e -> new CrudSearchEmployeeRow(e.getId(), e.getName())).collect(Collectors.toList());
	}

	public List<CrudSearchGroupRow> searchGroups(String q) {
		return groupRepository.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
				.map(g -> new CrudSearchGroupRow(g.getId(), g.getName())).collect(Collectors.toList());
	}

	public List<CrudSearchPaymentRow> searchPayments(String q) {
		return paymentMethodRepository.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
				.map(p -> new CrudSearchPaymentRow(p.getId(), p.getName())).collect(Collectors.toList());
	}

	public List<CrudSearchIndustryRow> searchIndustries(String q) {
		return industryRepository.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
				.map(i -> new CrudSearchIndustryRow(i.getId(), i.getName())).collect(Collectors.toList());
	}

	public List<CrudSearchFlagRow> searchFlags(String q) {
		return flagRepository.findByNameContainingIgnoreCaseOrderByNameAsc(q).stream()
				.map(f -> new CrudSearchFlagRow(f.getId(), f.getName())).collect(Collectors.toList());
	}

	public List<CrudSearchTablePriceRow> searchTablePrices(String q) {
		return tablePriceRepository.searchForCrudTableByProductText(q).stream().map(this::toTablePriceRow)
				.collect(Collectors.toList());
	}

	private CrudSearchTablePriceRow toTablePriceRow(TablePrice tp) {
		var p = Objects.requireNonNull(tp.getProduct());
		var c = Objects.requireNonNull(tp.getCustomer());
		String productLine = p.getProductCode() + "  " + p.getProductName();
		return new CrudSearchTablePriceRow(tp.getId(), c.getFantasyName(), p.getProductCode(), productLine,
				tp.getPrice());
	}
}
