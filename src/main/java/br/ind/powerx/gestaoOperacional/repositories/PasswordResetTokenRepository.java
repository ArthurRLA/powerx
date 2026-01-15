package br.ind.powerx.gestaoOperacional.repositories;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.ind.powerx.gestaoOperacional.model.PasswordResetToken;
import br.ind.powerx.gestaoOperacional.model.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserId(Long id);
    void deleteByUserId(Long id);
	void deleteByUser(User user);
}
