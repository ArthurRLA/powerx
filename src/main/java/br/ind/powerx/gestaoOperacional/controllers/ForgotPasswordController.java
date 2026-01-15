package br.ind.powerx.gestaoOperacional.controllers;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.ind.powerx.gestaoOperacional.model.PasswordResetToken;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.services.EmailService;
import br.ind.powerx.gestaoOperacional.services.PasswordResetTokenService;
import br.ind.powerx.gestaoOperacional.services.UserService;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    @Autowired
    private PasswordResetTokenService tokenService;
    
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping
    public String processForgotPasswordForm(@RequestParam("email") String email, Model model, HttpServletRequest request) {
        User userOpt = userService.findByEmail(email);
        if (userOpt != null) {
            Optional<PasswordResetToken> existingTokenOpt = tokenService.findPasswordResetTokenByUser(userOpt.getId());
            
            if (existingTokenOpt.isPresent()) {
                tokenService.deletePasswordResetTokenByUserId(userOpt.getId());
            }

            String token = UUID.randomUUID().toString();
            PasswordResetToken resetToken = new PasswordResetToken(token, userOpt);
            
            tokenService.savePasswordResetToken(resetToken);

            String resetLink = getAppUrl(request) + "/reset-password?token=" + token;

            emailService.sendPasswordResetEmail(userOpt.getEmail(), resetLink);
        }
        model.addAttribute("message", "Se um usuário com esse e-mail existir, um link de redefinição foi enviado.");
        return "forgot-password";
    }


    

    private String getAppUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}

