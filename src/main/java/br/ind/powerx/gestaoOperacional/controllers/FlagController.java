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

import br.ind.powerx.gestaoOperacional.model.Flag;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.model.dtos.FlagDTO;
import br.ind.powerx.gestaoOperacional.services.AuthenticationService;
import br.ind.powerx.gestaoOperacional.services.FlagService;

@Controller
@RequestMapping("/flags")
public class FlagController {

	@Autowired
	private FlagService flagService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@GetMapping
	public String findAll(@RequestParam(defaultValue = "0") int page, 
            @RequestParam(defaultValue = "50") int size, 
            Model model) {
		User user = authenticationService.getUserAuthenticated();
		Page<Flag> flags = flagService.findAll(PageRequest.of(page, size, Sort.by(Sort.Order.asc("name"))));
		
		model.addAttribute("flags", flags.getContent());
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", flags.getTotalPages());
	    model.addAttribute("user", user);
		
		return "flags";
	}
	
	@PostMapping("/save")
	public String saveFlag(@ModelAttribute FlagDTO flagDTO, Model model) {
		flagService.save(flagDTO);
		return "redirect:/flags";
	}
	
	@PostMapping("/update/{id}")
	public String updateFlag(@PathVariable Long id, @ModelAttribute Flag flag) {
		flagService.update(id, flag);
		return "redirect:/flags";
	}
	
	
}














