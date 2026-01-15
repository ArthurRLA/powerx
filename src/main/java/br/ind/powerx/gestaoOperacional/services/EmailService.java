package br.ind.powerx.gestaoOperacional.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
 
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Redefinição de Senha");
        message
	        .setText("Para redefinir sua senha, clique no link a seguir:\n"
		        + resetLink
		        + "\n"
		        + "Este link é válido por 24 horas.");
        mailSender.send(message);
    }
}
