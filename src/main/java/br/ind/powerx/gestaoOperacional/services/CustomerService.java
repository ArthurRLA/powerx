package br.ind.powerx.gestaoOperacional.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.CurrentAccount;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Flag;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.IncentiveValue;
import br.ind.powerx.gestaoOperacional.model.Industry;
import br.ind.powerx.gestaoOperacional.model.MechanicApuration;
import br.ind.powerx.gestaoOperacional.model.ProductStock;
import br.ind.powerx.gestaoOperacional.model.ProductStockItem;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerBasicInfosDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerCurrentAccountInfosDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerCurrentAccountProductsItemsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerEditDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerEmployeeBasicInfosDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerGroupProductsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerProductStockItemsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerSetCurrentAccountDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerTablePriceInfosDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerUniqueInfos;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerUpdateDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.DataNewSales;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.FlagDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.GroupDto;
import br.ind.powerx.gestaoOperacional.model.dtos.IndustryDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.MechanicApurationDto;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductStockDto;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductStockItemDto;
import br.ind.powerx.gestaoOperacional.model.dtos.ProductTableDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UserCustomersDto;
import br.ind.powerx.gestaoOperacional.model.dtos.UserDto;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FlagRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.GroupRepository;
import br.ind.powerx.gestaoOperacional.repositories.IndustryRepository;
import br.ind.powerx.gestaoOperacional.repositories.MechanicApurationRepository;
import br.ind.powerx.gestaoOperacional.repositories.ProductRepository;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.CustomerSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CustomerService {

	private final CustomerRepository customerRepository;
	private final EmployeeRepository employeeRepository;
	private final GroupRepository groupRepository;
	private final IndustryRepository industryRepository;
	private final FlagRepository flagRepository;
	private final UserRepository userRepository;
	private final MechanicApurationRepository mechanicApurationRepository;
	private final ProductRepository productRepository;
	private final FunctionRepository functionRepository;

	@Autowired
	public CustomerService(CustomerRepository customerRepository, EmployeeRepository employeeRepository,
			GroupRepository groupRepository, IndustryRepository industryRepository, FlagRepository flagRepository,
			UserRepository userRepository, MechanicApurationRepository mechanicApurationRepository,
			ProductRepository productRepository, FunctionRepository functionRepository) {
		this.customerRepository = customerRepository;
		this.employeeRepository = employeeRepository;
		this.groupRepository = groupRepository;
		this.industryRepository = industryRepository;
		this.flagRepository = flagRepository;
		this.userRepository = userRepository;
		this.mechanicApurationRepository = mechanicApurationRepository;
		this.productRepository = productRepository;
		this.functionRepository = functionRepository;
	}

	@Transactional
	public void save(Customer customer, Long userId, Long groupId, Long industryId, Long flagId,
			Long mechanicApurationId, List<Long> employees) {

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
		customer.setUser(user);

		Group group = groupRepository.findById(groupId)
				.orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado"));
		customer.setGroup(group);

		Industry industry = industryRepository.findById(industryId)
				.orElseThrow(() -> new EntityNotFoundException("Seguimento não encontrado"));
		customer.setIndustry(industry);

		Flag flag = flagRepository.findById(flagId)
				.orElseThrow(() -> new EntityNotFoundException("Marca/Bandeira não encontrado"));
		customer.setFlag(flag);

		MechanicApuration mechanicApuration = mechanicApurationRepository.findById(mechanicApurationId)
				.orElseThrow(() -> new EntityNotFoundException("Apuração de mecânico não encontrada"));
		customer.setMechanicApuration(mechanicApuration);

		customerRepository.save(customer);

	}

	public void save(Customer customer) {
		customerRepository.save(customer);
	}

	@Transactional
	public void update(CustomerUpdateDTO customerToUpdate, Long userId, Long groupId, Long industryId, Long flagId,
			Long mechanicApurationId, List<Long> employeeIds) {
		Customer existingCustomer = customerRepository.findById(customerToUpdate.id())
				.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

		existingCustomer.setUnysoftCode(customerToUpdate.unysoftCode());
		existingCustomer.setCnpj(customerToUpdate.cnpj());
		existingCustomer.setRegisteredName(customerToUpdate.registeredName());
		existingCustomer.setFantasyName(customerToUpdate.fantasyName());
		existingCustomer.setAddress(customerToUpdate.address());
		existingCustomer.setActive(customerToUpdate.active());

		existingCustomer.setGroup(groupRepository.findById(groupId)
				.orElseThrow(() -> new EntityNotFoundException("Grupo não encontrado")));
		existingCustomer.setIndustry(industryRepository.findById(industryId)
				.orElseThrow(() -> new EntityNotFoundException("Seguimento não encontrado")));
		existingCustomer.setFlag(flagRepository.findById(flagId)
				.orElseThrow(() -> new EntityNotFoundException("Marca/Bandeira não encontrado")));
		existingCustomer.setUser(userRepository.findById(userId)
				.orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado")));
		existingCustomer.setMechanicApuration(mechanicApurationRepository.findById(mechanicApurationId)
				.orElseThrow(() -> new EntityNotFoundException("Apuração de mecânico não encontrada")));

		List<Employee> newEmployees = new ArrayList<>();

		if(employeeIds != null && !employeeIds.isEmpty()){
			newEmployees = employeeRepository.findAllById(employeeIds);
		}
		existingCustomer.getEmployees().clear();
		newEmployees.forEach(existingCustomer::addEmployee);

	}

	public Page<Customer> filterCustomers(List<Long> users, List<Long> groups, List<Long> industries, List<Long> flags,
			boolean active, Pageable pageable) {

		Specification<Customer> spec = Specification.where(null);

		if (users != null && !users.isEmpty()) {
			spec = spec.and(CustomerSpecifications.userIdIn(users));
		}

		if (groups != null && !groups.isEmpty()) {
			spec = spec.and(CustomerSpecifications.groupIdIn(groups));
		}

		if (industries != null && !industries.isEmpty()) {
			spec = spec.and(CustomerSpecifications.industryIdIn(industries));
		}

		if (flags != null && !flags.isEmpty()) {
			spec = spec.and(CustomerSpecifications.flagIdIn(flags));
		}

		spec = spec.and(CustomerSpecifications.isActive(active));
		

		return customerRepository.findAll(spec, pageable);
	}

	public List<Customer> findAllByActiveTrue() {
		return customerRepository.findAllByActiveTrueOrderByFantasyNameAsc();
	}

	public List<Customer> findAllByUserIdNull() {
		return customerRepository.findAllByUserIdNullOrderByFantasyNameAsc();
	}

	public List<Customer> findAllById(List<Long> customers) {
		return customerRepository.findAllById(customers);
	}

	public List<Customer> findAllByGroupIdNull() {
		return customerRepository.findAllByGroupIdNullOrderByFantasyNameAsc();
	}

	public Optional<Customer> findById(Long cutomerId) {
		return customerRepository.findById(cutomerId);
	}

	public Page<Customer> findAll(Pageable pageable) {
		return customerRepository.findAll(pageable);
	}

	public List<Customer> findAll() {
		return customerRepository.findAll();
	}

	@Transactional
	public void setStock(Long customerId, ProductStockDto productStockDto) {
		Customer customer = byId(customerId, customerRepository);
		ProductStock stock = customer.getProductStock();

		if (stock == null) {
			stock = new ProductStock();
			stock.setCustomer(customer);
			customer.setProductStock(stock);
		} else {
			stock.getProductStockItems().clear();
		}

		List<ProductStockItem> newItems = stockItemsFromDto(productStockDto.getProductStockItems(), stock);
		newItems.forEach(stock::addProductStockItem);

		System.out.println("Estoque após atualização de itens: " + stock);

		CurrentAccount currentAccount = customer.getCurrentAccount();

		if (currentAccount == null) {
			currentAccount = new CurrentAccount();
			currentAccount.setCustomer(customer);
		}
		currentAccount.setBalance(stock.getTotalBalance());
		customer.setCurrentAccount(currentAccount);

		customerRepository.save(customer);

	}

	private <T, ID> T byId(ID id, JpaRepository<T, Long> repository) {
		return repository.findById((Long) id).orElseThrow(() -> new EntityNotFoundException("entidade não encontrada"));
	}

	private List<ProductStockItem> stockItemsFromDto(List<ProductStockItemDto> itemsDto, ProductStock stock) {
		return itemsDto.stream().filter(item -> item.getQuantity() != 0).map(item -> new ProductStockItem(null,
				byId(item.getProduct(), productRepository), item.getQuantity(), stock)).toList();

	}

	public List<Customer> findAllByGroupIdNullOrderByFantasyNameAsc() {
		return customerRepository.findAllByGroupIdNullOrderByFantasyNameAsc();
	}

	public List<Customer> findAllByActiveTrueOrderByFantasyNameAsc() {
		return customerRepository.findAllByActiveTrueOrderByFantasyNameAsc();
	}

	public List<Customer> findAllByUserIdNullOrderByNameAsc() {
		return customerRepository.findAllByUserIdNullOrderByFantasyNameAsc();
	}

	public List<Customer> findAllOrderByFantasyNameAsc() {
		return customerRepository.findAllOrderByFantasyNameAsc();
	}

	public CustomerBasicInfosDto customersBasicInfosById(Long id) {
		return findById(id).stream().map(c -> {
			var dto = new CustomerBasicInfosDto();
			dto.setActive(c.isActive());
			dto.setGroupName(c.getGroup().getName());
			dto.setUnysoftCode(c.getUnysoftCode());
			dto.setCnpj(c.getCnpj());
			dto.setRegisteredName(c.getRegisteredName());
			dto.setFantasyName(c.getFantasyName());
			dto.setCurrentAccountBalance(
					c.getCurrentAccount() != null ? c.getCurrentAccount().getBalance().toString() : "-");
			dto.setAddress(c.getAddress());
			dto.setUserName(c.getUser().getName());
			dto.setIndustryName(c.getIndustry().getName());
			dto.setFlagName(c.getFlag().getName());
			dto.setMechanicApurationName(c.getMechanicApuration().getName());

			return dto;
		}).findFirst().orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
	}

	public List<CustomerEmployeeBasicInfosDto> getCustomerEmployeesBasicInfos(Long id) {
		return findById(id).stream().flatMap(c -> c.getEmployees().stream().map(e -> {
			var dto = new CustomerEmployeeBasicInfosDto();
			dto.setCpf(e.getCpf());
			dto.setName(e.getName());

			dto.setFunctions(e.getFunctions().stream().map(Function::getName).collect(Collectors.toSet()));

			dto.setApurationTypes(
					e.getApurationTypes().stream().map(ApurationType::getName).collect(Collectors.toSet()));

			return dto;
		})).toList();
	}

	public List<CustomerTablePriceInfosDto> getCustomerTablePriceInfos(Long id) {
		var list = findById(id).stream()
				.flatMap(c -> c.getTables() == null ? Stream.empty() : c.getTables().stream().map(tb -> {
					var dto = new CustomerTablePriceInfosDto();
					dto.setProductCode(tb.getProduct().getProductCode());
					dto.setProductName(tb.getProduct().getProductName());
					dto.setPrice(tb.getPrice().toString());
					return dto;
				})).toList();

		var toSort = new ArrayList<>(list);

		toSort.sort(Comparator.comparing(CustomerTablePriceInfosDto::getProductCode));

		return toSort;
	}

	public List<CustomerProductStockItemsDto> getCustomerProductStockItems(Long id) {
		return findById(id).stream().flatMap(c -> c.getProductStock() == null ? Stream.empty()
				: c.getProductStock().getProductStockItems().stream().map(psi -> {
					var dto = new CustomerProductStockItemsDto();
					dto.setId(psi.getProduct().getId());
					dto.setProductCode(psi.getProduct().getProductCode());
					dto.setProductName(psi.getProduct().getProductName());
					dto.setQuantity(psi.getQuantity().toString());
					return dto;
				})).toList();
	}

	public CustomerCurrentAccountInfosDto getCustomerCurrentAccountInfosDto(Long id) {
		return findById(id).stream().map(c -> {
			var dto = new CustomerCurrentAccountInfosDto();
			dto.setFantasyName(c.getFantasyName());
			dto.setCnpj(c.getCnpj());

			if (c.getProductStock() != null && c.getProductStock().getProductStockItems() != null) {
				dto.setBalance(c.getProductStock().getTotalBalance().toString());
				dto.setCustomerProductStockItems(c.getProductStock().getProductStockItems().stream().map(psi -> {
					var cpsi = new CustomerCurrentAccountProductsItemsDto();
					cpsi.setId(psi.getProduct().getId());
					cpsi.setProductCode(psi.getProduct().getProductCode());
					cpsi.setProductName(psi.getProduct().getProductName());
					cpsi.setQuantity(psi.getQuantity().toString());
					cpsi.setBalance(psi.getBalance().toString());
					cpsi.setCcValue(psi.getTotalCcValue().toString());

					return cpsi;
				}).toList());
			}

			return dto;
		}).findFirst().orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
	}

	public CustomerSetCurrentAccountDto getCustomerSetCurrentAccountDto(Long id) {
		return findById(id).stream().map(c -> {
			var dto = new CustomerSetCurrentAccountDto();
			dto.setFantasyName(c.getFantasyName());
			dto.setCnpj(c.getCnpj());

			dto.setCustomerProductStockItems(c.getGroup().getProducts().stream()
					.map(p -> new CustomerGroupProductsDto(p.getId(), p.getProductCode(), p.getProductName()))
					.toList());

			return dto;
		}).findFirst().orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
	}

	public CustomerEditDto getCustomerEditData(Long id) {
		return findById(id).stream().map(c -> {
			var dto = new CustomerEditDto();
			dto.setId(c.getId());
			dto.setUnysoftCode(c.getUnysoftCode());
			dto.setCnpj(c.getCnpj());
			dto.setRegisteredName(c.getRegisteredName());
			dto.setFantasyName(c.getFantasyName());
			dto.setAddress(c.getAddress());
			dto.setActive(c.isActive());
			dto.setGroupId(c.getGroup().getId());
			dto.setMechanicApurationId(c.getMechanicApuration().getId());
			dto.setIndustryId(c.getIndustry().getId());
			dto.setFlagId(c.getFlag().getId());
			dto.setUserId(c.getUser().getId());
			dto.setEmployeeIds(c.getEmployees().stream().map(Employee::getId).toList());

			var groups = groupRepository.findAllOrderByNameAsc();
			var users = userRepository.findAllOrderByNameAsc();
			var mechanicApurations = mechanicApurationRepository.findAllOrderByNameAsc();
			var industries = industryRepository.findAllOrderByNameAsc();
			var flags = flagRepository.findAllOrderByNameAsc();
			var employees = employeeRepository.findAllByActiveTrueOrderByNameAsc();

			dto.setGroups(groups.stream().map(g -> new GroupDto(g.getId(), g.getName())).toList());

			dto.setUsers(users.stream().map(u -> new UserDto(u.getId(), u.getName())).toList());

			dto.setMechanicApurations(
					mechanicApurations.stream().map(m -> new MechanicApurationDto(m.getId(), m.getName())).toList());

			dto.setIndustries(industries.stream().map(i -> new IndustryDTO(i.getId(), i.getName())).toList());

			dto.setFlags(flags.stream().map(f -> new FlagDTO(f.getId(), f.getName())).toList());

			dto.setEmployees(employees.stream().map(f -> new EmployeeDTO(f.getId(), f.getName(), null)).toList());

			return dto;

		}).findFirst().orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

	}

	public List<CustomerDto> getCustomerSelect() {
		return customerRepository.findAllOrderByFantasyNameAsc().stream()
				.map(c -> {
					var dto = new CustomerDto();
					dto.setId(c.getId());
					dto.setName(c.getFantasyName());

					return dto;
				})
				.toList();
	}

	public UserCustomersDto customersUniqueInfosByUserId(Long id) {
		return userRepository.findById(id).stream()
				.findFirst()
				.map(u -> {
					var userCustomer = new UserCustomersDto();

					var customers = u.getCustomers();

					var cuis = customers.stream()
							.map(c -> {
								var cui = new CustomerUniqueInfos();
								cui.setId(c.getId());
								cui.setCnpj(c.getCnpj());
								cui.setFantasyName(c.getFantasyName());
								return cui;
							})
							.toList();

					userCustomer.setCustomers(cuis);

					return userCustomer;
				})
				.orElseThrow(() -> new EntityNotFoundException("Usuario com Id: " + id + " não encontrado"));
	}

	public DataNewSales getDataNewSales(Long id, String functionName) {
		Function function = functionRepository.findByName(functionName)
				.orElseThrow(() -> new EntityNotFoundException("Função: " + functionName + " não encontrada"));

		System.out.println("Função String: " + functionName + "/nFunction Obj: " + function.getName());
		return customerRepository.findById(id).stream()
				.findFirst()
				.map(c -> {
					System.out.println(c.getFantasyName());
					var dto = new DataNewSales();

					List<EmployeeDTO> employees = new ArrayList<>();

					boolean isLinear = "Linear".equalsIgnoreCase(
							Optional.ofNullable(c.getMechanicApuration())
									.map(MechanicApuration::getName)
									.orElse(""));

					if (!(isLinear && functionName.equalsIgnoreCase("Mecânico"))) {
						employees = c.getEmployees().stream()
								.filter(e -> e.getFunctions().stream()
										.anyMatch(f -> f.getName().equalsIgnoreCase(functionName)))
								.map(e -> new EmployeeDTO(e.getId(), e.getName(), null))
								.toList();
					}

					List<ProductTableDto> products = c.getGroup().getProducts().stream()
							.map(p -> {
								var pDto = new ProductTableDto();
								pDto.setId(p.getId());
								pDto.setProductCode(p.getProductCode());
								pDto.setProductName(p.getProductName());

								BigDecimal incentiveValue = p.getIncentiveValues().stream()
										.filter(i -> i.getCustomer().equals(c) && i.getFunction().equals(function))
										.findFirst()
										.map(IncentiveValue::getCcValue)
										.orElse(BigDecimal.ZERO);

								pDto.setIncentiveValue(incentiveValue);

								return pDto;
							})
							.toList();

					dto.setEmployees(employees);
					dto.setProducts(products);

					boolean mechanicCanBeGreater = c.getMechanicApuration().getName().equals("Somente mecânicos");
					dto.setMechanicCanBeGreater(mechanicCanBeGreater);

					return dto;

				})
				.orElseThrow(() -> new EntityNotFoundException("Cliente com Id: " + id + " não encontrado"));

	}

}
