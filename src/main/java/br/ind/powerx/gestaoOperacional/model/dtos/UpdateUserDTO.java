package br.ind.powerx.gestaoOperacional.model.dtos;

import br.ind.powerx.gestaoOperacional.model.enums.Position;
import br.ind.powerx.gestaoOperacional.model.enums.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserDTO(
		@NotNull 
		Long id,
		@NotBlank
		String name,
		@NotBlank
		String email,
		@NotBlank
		Position position,
		@NotBlank
		State state,
		@NotBlank
		String role) {

}
