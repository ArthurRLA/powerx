package br.ind.powerx.gestaoOperacional.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.RevenueSaveDto;
import br.ind.powerx.gestaoOperacional.model.enums.OperationType;
import br.ind.powerx.gestaoOperacional.repositories.GroupRepository;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.ProductService;
import br.ind.powerx.gestaoOperacional.services.RevenueService;
import br.ind.powerx.gestaoOperacional.services.UserService;

@Controller
@RequestMapping("/revenues")
public class RevenueController {

	private final RevenueService revenueService;
	private final CustomerService customerService;
	private final UserService userService;
	private final GroupRepository groupRepository;
	private final ProductService productService;
	private final AuthenticationService authenticationService;

	@Autowired
	public RevenueController(RevenueService revenueService, CustomerService customerService,
			ProductService productService, AuthenticationService authenticationService, UserService userService, 
			GroupRepository groupRepository) {
		this.revenueService = revenueService;
		this.customerService = customerService;
		this.productService = productService;
		this.authenticationService = authenticationService;
		this.userService = userService;
		this.groupRepository = groupRepository;
	}

	@GetMapping
	public String getRevenues(Model model, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "25") int size) {
		User user = authenticationService.getUserAuthenticated();
		Page<Revenue> revenues = revenueService.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.desc("date"))));
		List<Customer> customers = customerService.findAllByActiveTrueOrderByFantasyNameAsc();
		List<Product> products = productService.findAllOrderByProductCodeAsc();
		List<Group> groups = groupRepository.findAllOrderByNameAsc();
		List<User> users = userService.findAllByActiveTrueOrderByNameAsc();
		List<OperationType> operationTypes = Arrays.asList(OperationType.values());

		model.addAttribute("revenues", revenues.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", revenues.getTotalPages());
		model.addAttribute("customers", customers);
		model.addAttribute("groups", groups);
		model.addAttribute("users", users);
		model.addAttribute("operationTypes", operationTypes);
		model.addAttribute("products", products);
		model.addAttribute("user", user);

		return "revenue";
	}

	@PostMapping("/save")
	public ResponseEntity<?> saveRevenue(@RequestBody RevenueSaveDto revenueDto) {
		System.out.println(revenueDto);
		try {
			revenueService.save(revenueDto);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(500).body("Erro ao salvar receita: " + e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

	@PostMapping("/save/spreadsheet")
	public String saveBySpreadsheet(@RequestParam(name = "file") MultipartFile file) {
		try {
			revenueService.saveBySpreadsheet(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/revenues";
	}

	@GetMapping("/by-invoice-number/{invoiceNumber}")
	public String findByInvoiceNumber(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size, @PathVariable Integer invoiceNumber, Model model) {
		try {
			Page<Revenue> revenues = revenueService.findByInvoiceNumber(invoiceNumber,
					PageRequest.of(page, size, Sort.by(Sort.Order.desc("invoiceNumber"))));

			model.addAttribute("revenues", revenues.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", revenues.getTotalPages());
			model.addAttribute("invoiceNumber", invoiceNumber);

			return "fragments/searchRevenues :: searched-revenues";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/revenues";
		}
	}

	@GetMapping("/clearFilters")
	public String clearFilters(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
			@RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
			@RequestParam(value = "userIds", required = false) List<Long> userIds,
			@RequestParam(value = "customerIds", required = false) List<Long> customerIds,
			@RequestParam(value = "groupIds", required = false) List<Long> groupIds,
			@RequestParam(value = "OperationTypes", required = false) List<OperationType> operationTypes, Model model) {
		try {
			Page<Revenue> revenues = revenueService
					.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("invoiceNumber"))));

			model.addAttribute("revenues", revenues.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", revenues.getTotalPages());
			
			model.addAttribute("start", start);
			model.addAttribute("end", end);
			model.addAttribute("userIds", userIds);
			model.addAttribute("customerIds", customerIds);
			model.addAttribute("groupIds", groupIds);
			model.addAttribute("operationTypes", operationTypes);
			model.addAttribute("size", size);

			return "fragments/filteredRevenues :: filtered-revenues";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/revenues";
		}
	}

	@GetMapping("/filter")
	public String filter(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
			@RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
			@RequestParam(value = "userIds", required = false) List<Long> userIds,
			@RequestParam(value = "customerIds", required = false) List<Long> customerIds,
			@RequestParam(value = "groupIds", required = false) List<Long> groupIds,
			@RequestParam(value = "OperationTypes", required = false) List<OperationType> operationTypes, Model model) {
		try {
			Page<Revenue> revenues = revenueService.filter(start, end, userIds, customerIds, groupIds, operationTypes,
					PageRequest.of(page, size, Sort.by(Sort.Order.asc("invoiceNumber"))));

			model.addAttribute("revenues", revenues.getContent());
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages", revenues.getTotalPages());

			model.addAttribute("start", start);
			model.addAttribute("end", end);
			model.addAttribute("userIds", userIds);
			model.addAttribute("customerIds", customerIds);
			model.addAttribute("groupIds", groupIds);
			model.addAttribute("operationTypes", operationTypes);
			model.addAttribute("size", size);

			return "fragments/filteredRevenues :: filtered-revenues";
		} catch (Exception e) {
			e.printStackTrace();
			return "redirect:/revenues";
		}
	}

}
