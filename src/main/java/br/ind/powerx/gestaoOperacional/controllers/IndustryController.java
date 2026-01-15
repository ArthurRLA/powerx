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

import br.ind.powerx.gestaoOperacional.model.Industry;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.IndustryDTO;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.IndustryService;

@Controller
@RequestMapping("/industry")
public class IndustryController {

	@Autowired
	private IndustryService industryService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@GetMapping
	public String findAll(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "50") int size, 
            Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<Industry> industries = industryService.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));
		
		model.addAttribute("industries", industries.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", industries.getTotalPages());
	    model.addAttribute("user", user);
		
		return "industry";
	}
	
	@PostMapping("/save")
	public String saveIndustry(@ModelAttribute IndustryDTO industry) {
		industryService.save(industry);
		return "redirect:/industry";
	}
	
	@PostMapping("/update/{id}")
	public String updateIndustry(@PathVariable Long id,@ModelAttribute Industry industry) {
		industryService.update(id, industry);
		return "redirect:/industry";
	}
}














