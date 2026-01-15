package br.ind.powerx.gestaoOperacional.controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

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

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.CustomerDate;
import br.ind.powerx.gestaoOperacional.model.dtos.UserUpdateDTO;
import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.DocumentService;
import br.ind.powerx.gestaoOperacional.services.UserService;

@Controller
@RequestMapping("/users")
public class UserController {

	private final UserService userService;

	private final CustomerService customerService;

	private final AuthenticationService authenticationService;

	private final DocumentService documentService;

	@Autowired
	public UserController(UserService userService, CustomerService customerService,
			AuthenticationService authenticationService, DocumentService documentService) {
		this.userService = userService;
		this.customerService = customerService;
		this.authenticationService = authenticationService;
		this.documentService = documentService;
	}

	@GetMapping
	public String getUsers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<User> usersPage = userService.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));

		usersPage.getContent().forEach(u -> u.getCustomers().sort(
				Comparator.comparing(Customer::getFantasyName, String.CASE_INSENSITIVE_ORDER)));

		List<Customer> availableCustomers = customerService.findAllByUserIdNullOrderByNameAsc();
		List<Position> positionList = new ArrayList<>();
		List<State> stateList = new ArrayList<>();

		Position[] positions = Position.values();
		State[] states = State.values();

		for (Position p : positions) {
			positionList.add(p);
		}

		for (State s : states) {
			stateList.add(s);
		}

		model.addAttribute("users", usersPage.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", usersPage.getTotalPages());
		model.addAttribute("user", user);
		model.addAttribute("positions", positionList);
		model.addAttribute("states", stateList);
		model.addAttribute("availableCustomers", availableCustomers);
		return "users";
	}

	@PostMapping("/update/{id}")
	public String updateUser(@PathVariable Long id,
			@ModelAttribute UserUpdateDTO user,
			@RequestParam(required = false) List<Long> customers,
			Model model) {
		userService.update(user, customers);

		return "redirect:/users";
	}

	@PostMapping("/save")
	public String saveUser(@ModelAttribute User user,
			Model model) {
		userService.save(user);

		return "redirect:/users";
	}

	@GetMapping("/filter")
	public String filterUsers(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(name = "positions", required = false) List<Position> positions,
			@RequestParam(name = "states", required = false) List<State> states,
			@RequestParam(name = "active", required = false) boolean active,
			Model model) {

		System.out.println(active);
		Page<User> filteredUsers = userService.filterUsers(positions, states, active, PageRequest.of(page, size));

		model.addAttribute("users", filteredUsers.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", filteredUsers.getTotalPages());

		model.addAttribute("active", active);
		model.addAttribute("positions", positions);
		model.addAttribute("states", states);
		model.addAttribute("size", size);

		return "fragments/filteredUsers :: filtered-users";
	}

	@GetMapping("/clearFilters")
	public String clearFilters(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(name = "positions", required = false) List<Position> positions,
			@RequestParam(name = "states", required = false) List<State> states,
			@RequestParam(name = "active", required = false) boolean active,
			Model model) {
		Page<User> filteredUsers = userService.findAll(PageRequest.of(page, size));

		model.addAttribute("users", filteredUsers.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", filteredUsers.getTotalPages());

		model.addAttribute("active", active);
		model.addAttribute("positions", positions);
		model.addAttribute("states", states);
		model.addAttribute("size", size);

		return "fragments/filteredUsers :: filtered-users";
	}

	@GetMapping("/incentives/{id}")
	public String getUserincentives(@PathVariable Long id, Model model) {
		User user = userService.findById(id).get();
		List<Integer> documentNumbersByUser = documentService.findAllDocumentNumbersByUser(user);

		Map<Integer, CustomerDate> documentCustomer = documentService.getCustomersByDocument(documentNumbersByUser);

		model.addAttribute("documentNumbers", documentCustomer);

		return "fragments/incentivesByUser :: incentivesByUser";
	}

}
