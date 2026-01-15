package br.ind.powerx.gestaoOperacional.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ind.powerx.gestaoOperacional.model.PasswordResetToken;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.PasswordResetTokenService;
import br.ind.powerx.gestaoOperacional.services.UserService;

@Controller
@RequestMapping("/reset-password")
public class ResetPasswordController {

	@Autowired
	private PasswordResetTokenService tokenService;
	
	@Autowired
	private UserService userService;
	
	@GetMapping
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
    	System.out.println("Token: " + token);
        Optional<PasswordResetToken> resetTokenOpt = tokenService.findPasswordResetTokenByToken(token);
        
        System.out.println("PasswordResetToken: " + resetTokenOpt);
        
        if (resetTokenOpt.isPresent() && !resetTokenOpt.get().isExpired()) {
            model.addAttribute("token", token);
            return "reset-password";
        } else {
            model.addAttribute("error", "Token inválido ou expirado.");
            return "reset-password";
        }
    }

    @PostMapping
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "As senhas não coincidem.");
            model.addAttribute("token", token);
            return "reset-password";
        }

        Optional<PasswordResetToken> resetTokenOpt = tokenService.findPasswordResetTokenByToken(token);
        if (resetTokenOpt.isPresent() && !resetTokenOpt.get().isExpired()) {
            User user = resetTokenOpt.get().getUser();
            String encryptedPassword = new BCryptPasswordEncoder().encode(password);
            user.setPasswordHash(encryptedPassword);
            userService.update(user.getId(), user);
            tokenService.deletePasswordResetToken(resetTokenOpt.get());
            model.addAttribute("message", "Sua senha foi redefinida com sucesso.");
            return "login";
        } else {
            model.addAttribute("error", "Token inválido ou expirado.");
            return "reset-password";
        }
    }
}
