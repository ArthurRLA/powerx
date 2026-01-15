package br.ind.powerx.gestaoOperacional.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.ind.powerx.gestaoOperacional.model.ApurationType;
import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Employee;
import br.ind.powerx.gestaoOperacional.model.Function;
import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.repositories.ApurationTypeRepository;
import br.ind.powerx.gestaoOperacional.repositories.CustomerRepository;
import br.ind.powerx.gestaoOperacional.repositories.FunctionRepository;
import br.ind.powerx.gestaoOperacional.repositories.PaymentMethodRepository;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.EmployeeService;

@Controller
@RequestMapping("/employees")
public class EmployeeController {

	@Autowired
	private EmployeeService employeeService;

	@Autowired
	private FunctionRepository functionRepository;

	@Autowired
	private CustomerRepository customerRepository;

	@Autowired
	private ApurationTypeRepository apurationTypeRepository;

	@Autowired
	private PaymentMethodRepository paymentMethodRepository;

	@Autowired
	private AuthenticationService authenticationService;

	@GetMapping
	public String findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
			Model model) {
		User user = authenticationService.getUserAuthenticated();
		List<Function> functions = functionRepository.findAllOrderByNameAsc();
		List<Customer> customers = customerRepository.findAllByActiveTrueOrderByFantasyNameAsc();
		List<ApurationType> apurationTypes = apurationTypeRepository.findAllOrderByNameAsc();
		List<PaymentMethod> paymentMethods = paymentMethodRepository.findAllOrderByNameAsc();
		Page<Employee> employeesPage = employeeService.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));

		model.addAttribute("employees", employeesPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", employeesPage.getTotalPages());
		model.addAttribute("user", user);
		model.addAttribute("functions", functions);
		model.addAttribute("customers", customers);
		model.addAttribute("apurationTypes", apurationTypes);
		model.addAttribute("paymentMethods", paymentMethods);

		return "employees";
	}

	@PostMapping("/save")
	public String save(@ModelAttribute Employee employee, @RequestParam List<Long> functions,
			@RequestParam(value="customers", required = false) List<Long> customers, @RequestParam Long paymentMethod,
			@RequestParam List<Long> apurationTypes) {
		System.out.println(customers);
		employee.setActive(true);
		employeeService.save(employee, functions, customers, paymentMethod, apurationTypes);

		return "redirect:/employees";
	}

	@PostMapping("/update/{id}")
	public String update(
			@PathVariable Long id, 
			@ModelAttribute Employee employee, 
			@RequestParam List<Long> functions,
			@RequestParam(required = false) List<Long> customers, @RequestParam Long paymentMethod,
			@RequestParam List<Long> apurationTypes, 
			Model model) {

		employeeService.update(id, employee, functions, customers, paymentMethod, apurationTypes);

		return "redirect:/employees";
	}

	@GetMapping("/filter")
	public String filter(
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(value = "functions", required = false) List<Long> functionIds, 
			@RequestParam(value = "customers", required = false) List<Long> customerIds,
			Model model) {
				System.out.println(active);
		Page<Employee> employees = employeeService.filter(customerIds, functionIds, active, PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));

		model.addAttribute("employees", employees.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", employees.getTotalPages());
		
		model.addAttribute("active", active);
		model.addAttribute("functionIds", functionIds);
		model.addAttribute("customerIds", customerIds);
		model.addAttribute("size", size);

		return "fragments/filteredEmployees :: filtered-employees";
	}

	@GetMapping("/clearFilters")
	public String clearFilters(
			@RequestParam(defaultValue = "0") int page, 
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(name = "active", required = false) Boolean active,
			@RequestParam(value = "functions", required = false) List<Long> functionIds, 
			@RequestParam(value = "customers", required = false) List<Long> customerIds,
			Model model) {

		Page<Employee> employees = employeeService.findAll(PageRequest.of(page, size));

		model.addAttribute("employees", employees.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", employees.getTotalPages());

		model.addAttribute("active", active);
		model.addAttribute("functionIds", functionIds);
		model.addAttribute("customerIds", customerIds);
		model.addAttribute("size", size);

		return "fragments/filteredEmployees :: filtered-employees";
	}
	
	@PostMapping("/save/spreadsheet")
	public String saveBySpredSheet(@RequestParam(name = "file") MultipartFile file, Model model) {
		try {
			employeeService.saveBySpreadsheet(file);
			return "redirect:/employees";
		}
		catch(Exception e) {
			System.out.println(e);
			e.printStackTrace();
			model.addAttribute("error", e);
			return "error";
		}
		
	}
	

}


























