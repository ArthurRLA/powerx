package br.ind.powerx.gestaoOperacional.services;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ind.powerx.gestaoOperacional.model.PasswordResetToken;
import br.ind.powerx.gestaoOperacional.model.User;
import br.ind.powerx.gestaoOperacional.repositories.PasswordResetTokenRepository;
import br.ind.powerx.gestaoOperacional.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class PasswordResetTokenService {

	
	private final PasswordResetTokenRepository tokenRepository;
	
	private final UserRepository userRepository;
	
	@Autowired
	public PasswordResetTokenService(PasswordResetTokenRepository tokenRepository, UserRepository userRepository) {
		this.tokenRepository = tokenRepository;
		this.userRepository = userRepository;
	}
	
	public void savePasswordResetToken(PasswordResetToken token) {
        tokenRepository.save(token);
    }

    public Optional<PasswordResetToken> password(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        if (resetToken.isPresent() && resetToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            return resetToken;
        }
        return Optional.empty();
    }

    public void deletePasswordResetToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
    
    @Transactional
    public void deletePasswordResetTokenByUserId(Long id) {
    	User user = userRepository.findById(id)
    			.orElseThrow(() -> new EntityNotFoundException("Usuario não encontrado"));
        tokenRepository.deleteByUser(user);
    }

	public Optional<PasswordResetToken> findPasswordResetTokenByUser(Long id) {
		Optional<PasswordResetToken> resetToken = tokenRepository.findByUserId(id);
		
		if (!resetToken.isPresent()) {
            return Optional.empty();
        }
		
		if (!resetToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
			deletePasswordResetTokenByUserId(id);
			return Optional.empty();
        }
		
        if (resetToken.isPresent() && resetToken.get().getExpiryDate().isAfter(LocalDateTime.now())) {
            return resetToken;
        }
        
        return Optional.empty();
	}

	public Optional<PasswordResetToken> findPasswordResetTokenByToken(String token) {
		return tokenRepository.findByToken(token);
	}
}





