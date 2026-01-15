package br.ind.powerx.gestaoOperacional.services;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
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

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.dtos.ApurationTypeDto;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerDto;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeBasicInfosDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeDetailsDto;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeEditDto;
import br.ind.powerx.gestaoOperacional.model.dtos.EmployeeRelationshipDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.FunctionDto;
import br.ind.powerx.gestaoOperacional.model.dtos.PaymentMethodDTO;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.EmployeeRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import br.ind.powerx.gestaoOperacional.repositories.specifications.EmployeeSpecifications;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class EmployeeService {

	private final EmployeeRepository employeeRepository;

	private final FunctionRepository functionRepository;

	private final CustomerRepository customerRepository;

	private final PaymentMethodRepository paymentMethodRepository;

	private final ApurationTypeRepository apurationTypeRepository;

	private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

	@Autowired
	public EmployeeService(EmployeeRepository employeeRepository, FunctionRepository functionRepository,
			CustomerRepository customerRepository, PaymentMethodRepository paymentMethodRepository,
			ApurationTypeRepository apurationTypeRepository) {
		this.employeeRepository = employeeRepository;
		this.functionRepository = functionRepository;
		this.customerRepository = customerRepository;
		this.paymentMethodRepository = paymentMethodRepository;
		this.apurationTypeRepository = apurationTypeRepository;
	}

	@Transactional
	public void save(Employee emp, List<Long> functionsIds, List<Long> customersIds, Long paymentMethodsId,
			List<Long> apurationTypesIds) {
		List<Function> functions = functionRepository.findAllById(functionsIds);
		List<Customer> customers = customerRepository.findAllById(customersIds);
		List<ApurationType> apurationTypes = apurationTypeRepository.findAllById(apurationTypesIds);

		PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodsId)
				.orElseThrow(() -> new EntityNotFoundException("Método de pagamento não encontrado"));

		functions.stream().forEach(f -> {
			f.addEmployee(emp);
			emp.addFunction(f);
		});

		customers.stream().forEach(c -> {
			c.addEmployee(emp);
			emp.addCustomer(c);
		});

		apurationTypes.stream().forEach(a -> {
			a.addEmployee(emp);
			emp.addApurationType(a);
		});

		paymentMethod.addEmployee(emp);
		emp.setPaymentMethod(paymentMethod);

		employeeRepository.save(emp);
	}

	@Transactional
	public void update(Long id, Employee emp, List<Long> functionsIds, List<Long> customersIds, Long paymentMethodId,
			List<Long> apurationTypesIds) {
		List<Function> functions = functionRepository.findAllById(functionsIds);
		List<Customer> customers = customerRepository.findAllById(customersIds);
		List<ApurationType> apurationTypes = apurationTypeRepository.findAllById(apurationTypesIds);
		PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
				.orElseThrow(() -> new EntityNotFoundException("Método de pagamento não encontrado"));

		Employee existingEmp = employeeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Premiado não encontrado"));

		existingEmp.setCpf(emp.getCpf());
		existingEmp.setEmail(emp.getEmail());
		existingEmp.setName(emp.getName());
		existingEmp.setPhone(emp.getPhone());
		existingEmp.setBirthDate(emp.getBirthDate());
		existingEmp.setActive(emp.isActive());

		List<Function> currentFunctions = new ArrayList<>(existingEmp.getFunctions());
		List<Customer> currentCustomers = new ArrayList<>(existingEmp.getCustomers());
		List<ApurationType> currentApurationTypes = new ArrayList<>(existingEmp.getApurationTypes());

		for (Function f : currentFunctions) {
			f.removeEmployee(existingEmp);
			functionRepository.save(f);
		}

		for (Customer c : currentCustomers) {
			c.removeEmployee(existingEmp);
			customerRepository.save(c);
		}

		for (ApurationType a : currentApurationTypes) {
			a.removeEmployee(existingEmp);
			apurationTypeRepository.save(a);
		}

		paymentMethod.addEmployee(existingEmp);
		paymentMethodRepository.save(paymentMethod);

		existingEmp.getFunctions().clear();
		existingEmp.getCustomers().clear();
		existingEmp.getApurationTypes().clear();

		for (Function f : functions) {
			existingEmp.addFunction(f);
			f.addEmployee(existingEmp);
		}

		for (Customer c : customers) {
			existingEmp.addCustomer(c);
			c.addEmployee(existingEmp);
		}

		for (ApurationType a : apurationTypes) {
			existingEmp.addApurationType(a);
			a.addEmployee(existingEmp);
		}

		existingEmp.setPaymentMethod(paymentMethod);

		employeeRepository.save(existingEmp);
	}

	public List<Employee> findAllByActiveTrue() {
		return employeeRepository.findAllByActiveTrue();
	}

	public List<Employee> findAllById(Collection<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		ids.removeIf(Objects::isNull);
		return employeeRepository.findAllById(ids);
	}

	public void save(Employee e) {
		employeeRepository.save(e);

	}

	public Page<Employee> findAll(Pageable pageable) {
		return employeeRepository.findAll(pageable);
	}

	public Page<Employee> filter(List<Long> customers, List<Long> functions, Boolean active, Pageable pageable) {
		Specification<Employee> spec = Specification.where(null);

		if (customers != null && !customers.isEmpty()) {
			spec = spec.and(EmployeeSpecifications.customersIn(customers));
		}

		if (functions != null && !functions.isEmpty()) {
			spec = spec.and(EmployeeSpecifications.functionsIn(functions));
		}

		if(active != null){
			spec = spec.and(EmployeeSpecifications.isActive(active));
		}

		return employeeRepository.findAll(spec, pageable);
	}

	public Employee findById(Long employeeId) {
		return employeeRepository.findById(employeeId)
				.orElseThrow(() -> new EntityNotFoundException("Premaido não encontrado"));
	}

	public Optional<Employee> optionalById(Long id) {
		return employeeRepository.findById(id);
	}

	public void saveBySpreadsheet(MultipartFile file) throws IOException {
		logger.info("---------INICIO DE SALVANDO PREMIADO POR PLANILHA-------------");
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

		try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
			Sheet sheet = workbook.getSheetAt(0);
			int lastRowWithData = getLastRowWithData(sheet);
			logger.info("Última linha com dados: {}", lastRowWithData);

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

				if (isRowEmpty(row)) {
					logger.info("Linha {} vazia - pulando", row.getRowNum());
					continue;
				}

				try {
					String cpf = getStringCellValue(row.getCell(0));
					logger.info("CPF planilhado - {}", cpf);

					String name = getStringCellValue(row.getCell(1));
					logger.info("Nome planilhado - {}", name);

					String email = getStringCellValue(row.getCell(2));
					logger.info("Email planilhado - {}", email);

					String phone = getStringCellValue(row.getCell(3));
					logger.info("Phone planilhado - {}", phone);

					LocalDate dateOfBirth = null;
					Cell dobCell = row.getCell(4);
					if (dobCell != null) {
						if (dobCell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(dobCell)) {
							dateOfBirth = dobCell.getDateCellValue().toInstant()
									.atZone(ZoneId.systemDefault()).toLocalDate();
						} else if (dobCell.getCellType() == CellType.STRING) {
							try {
								dateOfBirth = LocalDate.parse(dobCell.getStringCellValue().trim(), dateFormatter);
							} catch (DateTimeParseException e) {
								logger.error("Erro na linha {}: Formato de data inválido - {}",
										row.getRowNum(), dobCell.getStringCellValue());
								continue;
							}
						}
					}
					logger.info("Data planilhado - {}", dateOfBirth);

					EmployeeBasicInfosDTO empBasicDto = new EmployeeBasicInfosDTO(cpf, name, email, phone, dateOfBirth);
					Employee empBasic = basicEmpFromDto(empBasicDto);

					if (empBasic != null) {
						employeeRepository.save(empBasic);
						logger.info("Funcionário salvo: {}", empBasic);
					}

					Long func_id1 = parseLongFromCell(row.getCell(5));
					logger.info("ID FUNÇÃO 1 - {}", func_id1);
					Long func_id2 = parseLongFromCell(row.getCell(6));
					logger.info("ID FUNÇÃO 2 - {}", func_id2);
					Long cust_id1 = parseLongFromCell(row.getCell(7));
					logger.info("ID CLIENTE 1 - {}", cust_id1);
					Long cust_id2 = parseLongFromCell(row.getCell(8));
					logger.info("ID CLIENTE 2 - {}", cust_id2);
					Long cust_id3 = parseLongFromCell(row.getCell(9));
					logger.info("ID CLIENTE 3 - {}", cust_id3);
					Long cust_id4 = parseLongFromCell(row.getCell(10));
					logger.info("ID CLIENTE 4 - {}", cust_id4);
					Long cust_id5 = parseLongFromCell(row.getCell(11));
					logger.info("ID CLIENTE 5 - {}", cust_id5);
					Long cust_id6 = parseLongFromCell(row.getCell(12));
					logger.info("ID CLIENTE 6 - {}", cust_id6);
					Long cust_id7 = parseLongFromCell(row.getCell(13));
					logger.info("ID CLIENTE 7 - {}", cust_id7);
					Long apur_id1 = parseLongFromCell(row.getCell(14));
					logger.info("ID APURAÇÃO 1 - {}", apur_id1);
					Long apur_id2 = parseLongFromCell(row.getCell(15));
					logger.info("ID APURAÇÃO 2 - {}", apur_id2);
					Long pay_id = parseLongFromCell(row.getCell(16));
					logger.info("ID METODO  - {}", pay_id);

					if (cpf == null || cpf.isBlank()) {
						logger.error("CPF inválido na linha {}", row.getRowNum());
						continue;
					}

					EmployeeRelationshipDTO empRelDto = new EmployeeRelationshipDTO(
							func_id1, func_id2,
							cust_id1, cust_id2, cust_id3, cust_id4, cust_id5, cust_id6, cust_id7,
							apur_id1, apur_id2,
							pay_id);

					if (empBasic != null) {
						relEmpFromDto(empBasic, empRelDto);
					}

				} catch (Exception e) {
					logger.error("Erro crítico na linha {}: {}", row.getRowNum(), e.getMessage(), e);
				}
			}
		}
		logger.info("---------FIM DE SALVANDO PREMIADO POR PLANILHA-------------");
	}

	private void relEmpFromDto(Employee emp, EmployeeRelationshipDTO empRelDto) {
		logger.info("-------------Inicio dos relacionamentos------------");
		if (emp != null && empRelDto != null) {
			emp.setActive(true);

			if (empRelDto.func_id1() != null) {
				Function function1 = functionRepository.findById(empRelDto.func_id1())
						.orElseThrow(() -> new EntityNotFoundException("Função não encontrada"));
				emp.addFunction(function1);
				logger.info("Função 1 encontrada: {}", function1.getName());
			}
			if (empRelDto.func_id2() != null) {
				Function function2 = functionRepository.findById(empRelDto.func_id2())
						.orElseThrow(() -> new EntityNotFoundException("Função não encontrada"));
				emp.addFunction(function2);
				logger.info("Função 2 encontrada: {}", function2.getName());
			}

			if (empRelDto.cust_id1() != null) {
				Customer customer1 = customerRepository.findById(empRelDto.cust_id1())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer1);
				customerRepository.save(customer1);
				logger.info("Cliente 1 encontrado: {}", customer1.getFantasyName());
			}
			if (empRelDto.cust_id2() != null) {
				Customer customer2 = customerRepository.findById(empRelDto.cust_id2())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer2);
				customerRepository.save(customer2);
				logger.info("Cliente 2 encontrado: {}", customer2.getFantasyName());
			}
			if (empRelDto.cust_id3() != null) {
				Customer customer3 = customerRepository.findById(empRelDto.cust_id3())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer3);
				customerRepository.save(customer3);
				logger.info("Cliente 3 encontrado: {}", customer3.getFantasyName());
			}
			if (empRelDto.cust_id4() != null) {
				Customer customer4 = customerRepository.findById(empRelDto.cust_id4())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer4);
				customerRepository.save(customer4);
				logger.info("Cliente 4 encontrado: {}", customer4.getFantasyName());
			}
			if (empRelDto.cust_id5() != null) {
				Customer customer5 = customerRepository.findById(empRelDto.cust_id5())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer5);
				customerRepository.save(customer5);
				logger.info("Cliente 5 encontrado: {}", customer5.getFantasyName());
			}
			if (empRelDto.cust_id6() != null) {
				Customer customer6 = customerRepository.findById(empRelDto.cust_id6())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer6);
				customerRepository.save(customer6);
				logger.info("Cliente 6 encontrado: {}", customer6.getFantasyName());
			}
			if (empRelDto.cust_id7() != null) {
				Customer customer7 = customerRepository.findById(empRelDto.cust_id7())
						.orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));
				emp.addCustomer(customer7);
				customerRepository.save(customer7);
				logger.info("Cliente 7 encontrado: {}", customer7.getFantasyName());
			}

			if (empRelDto.apur_id1() != null) {
				ApurationType apurationType1 = apurationTypeRepository.findById(empRelDto.apur_id1())
						.orElseThrow(() -> new EntityNotFoundException("Apuração não encontrada"));
				emp.addApurationType(apurationType1);
				logger.info("Apuração 1 encontrada: {}", apurationType1.getName());
			}
			if (empRelDto.apur_id2() != null) {
				ApurationType apurationType2 = apurationTypeRepository.findById(empRelDto.apur_id2())
						.orElseThrow(() -> new EntityNotFoundException("Apuração não encontrada"));
				emp.addApurationType(apurationType2);
				logger.info("Apuração 2 encontrada: {}", apurationType2.getName());
			}

			if (empRelDto.pay_id() != null) {
				PaymentMethod payment = paymentMethodRepository.findById(empRelDto.pay_id())
						.orElseThrow(() -> new EntityNotFoundException("Método não encontrado"));
				emp.setPaymentMethod(payment);
				logger.info("Método de pagamento encontrado: {}", payment.getName());
			}
			logger.info("-------------Fim dos relacionamentos------------");
			employeeRepository.save(emp);
		}
	}

	private Employee basicEmpFromDto(EmployeeBasicInfosDTO empBasicDto) {
		logger.info("-------------Inicio do método basicEmpFromDto------------");
		Employee emp = new Employee();
		Employee empSearched = findByCpf(empBasicDto.cpf());
		if (empSearched == null) {
			emp.setCpf(empBasicDto.cpf());
			logger.info("CPF DTO: {}", emp.getCpf());
			emp.setName(empBasicDto.name());
			logger.info("Name DTO: {}", emp.getName());
			emp.setEmail(empBasicDto.email());
			logger.info("Email DTO: {}", emp.getEmail());
			emp.setPhone(empBasicDto.phone());
			logger.info("Phone DTO: {}", emp.getPhone());
			emp.setBirthDate(empBasicDto.dateOfBirth());
			logger.info("Birth Date DTO: {}", emp.getBirthDate());
			emp.setActive(true);
			logger.info("-------------Fim do método basicEmpFromDto------------");
			return emp;
		} else {
			logger.info("CPF '{}' já encontrado", empBasicDto.cpf());
		}
		logger.info("-------------Fim do método basicEmpFromDto------------");
		return null;
	}

	private String getStringCellValue(Cell cell) {
		if (cell == null)
			return null;
		DataFormatter formatter = new DataFormatter();
		String value = formatter.formatCellValue(cell).trim();
		return value.isEmpty() ? null : value;
	}

	private Long parseLongFromCell(Cell cell) {
		if (cell == null) {
			return null;
		}
		if (cell.getCellType() == CellType.NUMERIC) {
			return (long) cell.getNumericCellValue();
		} else if (cell.getCellType() == CellType.STRING) {
			try {
				return Long.valueOf(cell.getStringCellValue());
			} catch (NumberFormatException e) {
				logger.warn("Erro ao converter para Long na célula: {}", cell.getStringCellValue());
				return null;
			}
		}
		return null;
	}

	public Employee findByCpf(String cpf) {
		return employeeRepository.findByCpf(cpf);
	}

	private int getLastRowWithData(Sheet sheet) {
		int lastRowNum = sheet.getLastRowNum();
		logger.info("lastRowNum - {}", lastRowNum);
		for (int i = lastRowNum; i >= 0; i--) {
			Row row = sheet.getRow(i);
			if (row != null && !isRowEmpty(row)) {
				return i;
			}
		}
		return -1;
	}

	private boolean isRowEmpty(Row row) {
		if (row == null) {
			return true;
		}
		Iterator<Cell> cellIterator = row.cellIterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			if (!isCellEmpty(cell)) {
				return false;
			}
		}
		return true;
	}

	private boolean isCellEmpty(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return true;
		}
		if (cell.getCellType() == CellType.STRING) {
			String value = cell.getStringCellValue().trim();
			return value.isEmpty();
		}
		return false;
	}

	public List<Employee> findAllByActiveTrueOrderByNameAsc() {
		return employeeRepository.findAllByActiveTrueOrderByNameAsc();
	}

	public EmployeeDetailsDto getEmployeeDetails(Long id) {
		return optionalById(id).stream()
				.map(e -> {
					var dto = new EmployeeDetailsDto();
					dto.setActive(e.isActive());
					dto.setId(e.getId());
					dto.setCpf(e.getCpf());
					dto.setName(e.getName());
					dto.setEmail(e.getEmail());
					dto.setPhone(e.getPhone());
					dto.setBirthDate(e.getBirthDate().toString());

					dto.setFunctions(
							e.getFunctions().stream()
									.map(Function::getName)
									.toList());

					dto.setCustomers(
							e.getCustomers().stream()
									.map(Customer::getFantasyName)
									.toList());

					dto.setApurationTypes(
							e.getApurationTypes().stream()
									.map(ApurationType::getName)
									.toList());

					dto.setPaymentMethod(e.getPaymentMethod().getName());

					return dto;
				})
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("Premiado não encontrado"));

	}

	public EmployeeEditDto getEmployeeEdit(Long id) {
		return optionalById(id).stream()
				.map(e -> {
					var dto = new EmployeeEditDto();
					dto.setActive(e.isActive());
					dto.setId(e.getId());
					dto.setCpf(e.getCpf());
					dto.setName(e.getName());
					dto.setEmail(e.getEmail());
					dto.setPhone(e.getPhone());
					dto.setBirthDate(e.getBirthDate().toString());

					dto.setSelectedFunctions(
							e.getFunctions().stream()
									.map(Function::getId)
									.toList());

					dto.setSelectedCustomers(
							e.getCustomers().stream()
									.map(Customer::getId)
									.toList());

					dto.setSelectedApurationTypes(
							e.getApurationTypes().stream()
									.map(ApurationType::getId)
									.toList());

					dto.setSelectedPaymentMethod(e.getPaymentMethod().getId());

					var functions = functionRepository.findAllOrderByNameAsc();
					var customers = customerRepository.findAllByActiveTrueOrderByFantasyNameAsc();
					var apurationTypes = apurationTypeRepository.findAllOrderByNameAsc();
					var paymentMethods = paymentMethodRepository.findAllOrderByNameAsc();

					dto.setFunctions(functions.stream().map(f -> new FunctionDto(f.getId(), f.getName())).toList());
					dto.setCustomers(
							customers.stream().map(c -> new CustomerDto(c.getId(), c.getFantasyName())).toList());
					dto.setApurationTypes(
							apurationTypes.stream().map(a -> new ApurationTypeDto(a.getId(), a.getName())).toList());
					dto.setPaymentMethods(
							paymentMethods.stream().map(p -> new PaymentMethodDTO(p.getId(), p.getName())).toList());

					return dto;
				})
				.findFirst()
				.orElseThrow(() -> new EntityNotFoundException("premiado não encontrado"));
	}

}
