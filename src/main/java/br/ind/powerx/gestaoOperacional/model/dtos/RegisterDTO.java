package br.ind.powerx.gestaoOperacional.model.dtos;

import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
		@NotBlank
		String name,
		@Email
		@NotBlank
		String email, 
		@NotBlank
		String phone, 
		@NotBlank
		String password, 
		@NotBlank
		String role,
		Position position,
		State state) {

}
