package br.ind.powerx.gestaoOperacional.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;

@Controller
@RequestMapping("/contacts")
public class ContactsController {
	
	private final AuthenticationService authenticationService;
	
	public ContactsController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@GetMapping()
	public String getContacts(Model model) {
		User user = authenticationService.getUserAuthenticated();
		
		model.addAttribute("user", user);
		
		return "contacts";
	}
	
}
