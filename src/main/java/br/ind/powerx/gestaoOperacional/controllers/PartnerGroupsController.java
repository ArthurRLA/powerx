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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ind.powerx.gestaoOperacional.model.Customer;
import br.ind.powerx.gestaoOperacional.model.Group;
import br.ind.powerx.gestaoOperacional.model.Product;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.CustomerService;
import br.ind.powerx.gestaoOperacional.services.PartnerGroupService;
import br.ind.powerx.gestaoOperacional.services.ProductService;

@Controller
@RequestMapping("/partner-groups")
public class PartnerGroupsController {
	
	@Autowired
	private PartnerGroupService groupService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private AuthenticationService authenticationService;

	@GetMapping
	public String getPartnerGroups(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "50") int size,
            Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<Group> groups = groupService.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));
		List<Product> products = productService.findAllOrderByProductCodeAsc();
		List<Customer> availableCustomers = customerService.findAllByGroupIdNullOrderByFantasyNameAsc();
		
		model.addAttribute("groups", groups.getContent());
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", groups.getTotalPages());
		model.addAttribute("user", user);
		model.addAttribute("products", products);
		model.addAttribute("availableCustomers", availableCustomers);
		
		return "partner-groups";
	}
	
	@PostMapping("/save")
	public String save(@ModelAttribute Group group, Model model) {
		groupService.save(group);
		
		return "redirect:/partner-groups";
	}
	
	@PostMapping("/update/{id}")
	public String update(@ModelAttribute Group group, Model model) {
		groupService.update(group);
		
		return "redirect:/partner-groups";
	}
}
