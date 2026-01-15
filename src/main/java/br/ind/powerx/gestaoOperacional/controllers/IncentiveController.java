package br.ind.powerx.gestaoOperacional.controllers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerDate;
import br.ind.powerx.gestaoOperacional.model.dtos.DocumentDetailsDto;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.DocumentService;
import br.ind.powerx.gestaoOperacional.services.UserService;

@Controller
@RequestMapping("/incentives")
public class IncentiveController {

	private final DocumentService documentService;

	private final CustomerService customerService;

	private final AuthenticationService authenticationService;

	private final UserService userService;

	@Autowired
	public IncentiveController(DocumentService documentService, CustomerService customerService,
			AuthenticationService authenticationService, UserService userService) {
		this.documentService = documentService;
		this.customerService = customerService;
		this.authenticationService = authenticationService;
		this.userService = userService;
	}

	@GetMapping
	public String findAll(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "50") int size,
			Model model) {

		User user = authenticationService.getUserAuthenticated();
		boolean isAdmin = user.getRole().equals("ROLE_ADMIN") ? true : false;

		List<Customer> customers = isAdmin
				? customerService.findAllByActiveTrueOrderByFantasyNameAsc()
				: user.getCustomers();

		List<User> users = isAdmin 
				? userService.findAllByActiveTrueOrderByNameAsc()
				: List.of(user); 
				

		List<Integer> allDocNums = isAdmin 
				? documentService.findAllDocumentNumbers()
				: documentService.findAllDocumentNumbersByUser(user);

		Map<Integer, CustomerDate> fullMap = documentService.getCustomersByDocument(allDocNums);

		List<Map.Entry<Integer, CustomerDate>> entries = new ArrayList<>(fullMap.entrySet());

		Page<Map.Entry<Integer, CustomerDate>> pageEntries = new PageImpl<>(
				entries.subList(page * size, Math.min((page + 1) * size, entries.size())), PageRequest.of(page, size),
				entries.size());

		System.out.println(pageEntries.getContent());		

		Map<Integer, CustomerDate> pagedMap = pageEntries.getContent().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, LinkedHashMap::new));

		model.addAttribute("documentNumbers", pagedMap);
		model.addAttribute("currentPage", pageEntries.getNumber());
		model.addAttribute("totalPages", pageEntries.getTotalPages());
		model.addAttribute("user", user);
		model.addAttribute("users", users);
		model.addAttribute("customers", customers);

		return "incentive-launch";
	}

	@GetMapping("/{documentNumber}")
	@ResponseBody
	public ResponseEntity<?> getDocumentDetails(@PathVariable Integer documentNumber) {
		try{
			DocumentDetailsDto dto = documentService.getDocumentDetails(documentNumber);
			return ResponseEntity.ok(dto);
		} catch (Exception e){
			e.printStackTrace();
			return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Erro ao buscar usuários: " + e.getMessage());
		}
	}

	@GetMapping("/filter")
	public String filterIncentives(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
			@RequestParam(value = "userIds", required = false) List<Long> userIds,
			@RequestParam(value = "customerIds", required = false) List<Long> customerIds,
			Model model) {
		User user = authenticationService.getUserAuthenticated();
		boolean isAdmin = user.getRole().equalsIgnoreCase("role_admin");
		List<Long> userIdsToFilter = isAdmin
		? userIds
		: List.of(user.getId());

		try {
			Page<Map.Entry<Integer, CustomerDate>> pageEntries = documentService.getPageDoucmentFiltered(page, size,
					start, end, userIdsToFilter, customerIds);
			Map<Integer, CustomerDate> pagedMap = pageEntries.getContent().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, LinkedHashMap::new));

			model.addAttribute("documentNumbers", pagedMap);
			model.addAttribute("currentPage", pageEntries.getNumber());
			model.addAttribute("totalPages", pageEntries.getTotalPages());

			model.addAttribute("start", start);
			model.addAttribute("end", end);
			model.addAttribute("userIds", userIds);
			model.addAttribute("customerIds", customerIds);
			model.addAttribute("size", size);

			return "fragments/filteredIncentives :: filteredIncentives";
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", e.getMessage());
			return "incentive-launch";
		}
	}

	@GetMapping("/clearFilters")
	public String clearFilters(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(value = "start", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
			@RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
			@RequestParam(value = "userIds", required = false) List<Long> userIds,
			@RequestParam(value = "customerIds", required = false) List<Long> customerIds,
			Model model) {
		User user = authenticationService.getUserAuthenticated();
		boolean isAdmin = user.getRole().equalsIgnoreCase("role_admin");

		try {

			List<Integer> allDocNums = isAdmin ? documentService.findAllDocumentNumbers()
					: documentService.findAllDocumentNumbersByUser(user);

			Map<Integer, CustomerDate> fullMap = documentService.getCustomersByDocument(allDocNums);

			List<Map.Entry<Integer, CustomerDate>> entries = new ArrayList<>(fullMap.entrySet());

			Page<Map.Entry<Integer, CustomerDate>> pageEntries = new PageImpl<>(
					entries.subList(page * size, Math.min((page + 1) * size, entries.size())),
					PageRequest.of(page, size),
					entries.size());

			Map<Integer, CustomerDate> pagedMap = pageEntries.getContent().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (u, v) -> u, LinkedHashMap::new));

			model.addAttribute("documentNumbers", pagedMap);
			model.addAttribute("currentPage", pageEntries.getNumber());
			model.addAttribute("totalPages", pageEntries.getTotalPages());

			model.addAttribute("start", start);
			model.addAttribute("end", end);
			model.addAttribute("userIds", userIds);
			model.addAttribute("customerIds", customerIds);
			model.addAttribute("size", size);

			return "fragments/filteredIncentives :: filteredIncentives";
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
			return "incentive-launch";
		}
	}

}
