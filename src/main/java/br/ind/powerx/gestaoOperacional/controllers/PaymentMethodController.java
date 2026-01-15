package br.ind.powerx.gestaoOperacional.controllers;

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

import br.ind.powerx.gestaoOperacional.model.PaymentMethod;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.PaymentMethodDTO;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.PaymentMethodService;

@Controller
@RequestMapping("/payments")
public class PaymentMethodController {
	
	@Autowired
	private PaymentMethodService methodService;
	@Autowired
	private AuthenticationService authenticationService;
	
	@GetMapping
	public String findAll(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "50") int size, 
            Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<PaymentMethod> payments = methodService.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));
		
		model.addAttribute("payments", payments.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", payments.getTotalPages());
		model.addAttribute("user", user);
		
		return "payments";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute PaymentMethodDTO method) {
		methodService.save(method);
		
		return "redirect:/payments";
	}
	
	@PostMapping("/update/{id}")
	public String update(@PathVariable Long id, @ModelAttribute PaymentMethod method) {
		methodService.update(id, method);
		
		return "redirect:/payments";
	}
	
	
}











