package br.ind.powerx.gestaoOperacional.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.OrderByCustomerDto;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.OrderByDocumentNumberDto;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.OrderByGroupDto;
import br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.OrderByStateDto;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.order.service.OrderService;

@Controller
@RequestMapping("/orders")
public class OrderController {

	private final AuthenticationService authenticationService;
	private final OrderService orderService;

	@Autowired
	public OrderController(AuthenticationService authenticationService, OrderService orderService) {
		this.authenticationService = authenticationService;
		this.orderService = orderService;
		
	}

	@GetMapping
	public String getOrdersPage(Model model) {
		User user = authenticationService.getUserAuthenticated();
		model.addAttribute("user", user);
		return "orders";
	}

	@GetMapping("/document-number")
	public String getOrdersByDocumentNumberPage(Model model) {
		User user = authenticationService.getUserAuthenticated();
		model.addAttribute("user", user);
		return "orders-document-number";
	}

	@GetMapping("/customer")
	public String getOrdersByCustomersPage(Model model) {
		User user = authenticationService.getUserAuthenticated();
		model.addAttribute("user", user);
		return "orders-customer";
	}

	@GetMapping("/state")
	public String getOrdersByStatePage(Model model) {
		User user = authenticationService.getUserAuthenticated();
		model.addAttribute("user", user);
		return "orders-state";
	}

	@GetMapping("/group")
	public String getOrdersByGroupPage(Model model) {
		User user = authenticationService.getUserAuthenticated();
		model.addAttribute("user", user);
		return "orders-group";
	}

	@PostMapping("/document-number")
	public ResponseEntity<?> getOrdersByDocumentNumberOrder(@RequestBody OrderByDocumentNumberDto dto, Model model) {
		
		try {

			return orderService.generateFile(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Não foi possível baixar o arquivo, erro: " + e.getMessage());
		}
	}

	@PostMapping("/customer")
	public ResponseEntity<?> getOrdersByCustomersOrder(@RequestBody OrderByCustomerDto dto, Model model) {

		try {

			return orderService.generateFile(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Não foi possível baixar o arquivo, erro: " + e.getMessage());
		}
	}

	@PostMapping("/state")
	public ResponseEntity<?> getOrdersByStateOrder(@RequestBody OrderByStateDto dto, Model model) {

		try {

			return orderService.generateFile(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Não foi possível baixar o arquivo, erro: " + e.getMessage());
		}
	}

	@PostMapping("/group")
	public ResponseEntity<?> getOrdersByGroupOrder(@RequestBody OrderByGroupDto dto, Model model) {

		try {

			return orderService.generateFile(dto);
			
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.internalServerError().body("Não foi possível baixar o arquivo, erro: " + e.getMessage());
		}
	}

}
