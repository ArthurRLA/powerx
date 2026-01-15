package br.ind.powerx.gestaoOperacional.controllers;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import br.ind.powerx.gestaoOperacional.model.dtos.LoginDTO;
import br.ind.powerx.gestaoOperacional.model.dtos.RegisterDTO;
import br.ind.powerx.gestaoOperacional.services.UserService;

@Controller
@RequestMapping("/auth")
public class AuthenticationController {
	
	@Autowired
	private UserService userService;
	
	@PostMapping("/register")
	public String register(@ModelAttribute RegisterDTO registerDTO, Model model) {
		try {
			userService.save(registerDTO);
		}catch(BadRequestException e) {
			model.addAttribute("error", "Usuario já cadastrado");
			return "badRequest";
		}
		
		return "redirect:/auth/login";
	}
	
	@GetMapping("/login")
	public String loginPage(Model model) {
		
		model.addAttribute("loginDTO", new LoginDTO(null, null));
		
		return "login";
	}
	
	
}













