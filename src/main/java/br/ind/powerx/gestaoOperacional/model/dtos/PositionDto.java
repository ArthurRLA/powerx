package br.ind.powerx.gestaoOperacional.model.dtos;

import br.ind.powerx.gestaoOperacional.model.enums.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PositionDto {

	private Position id;
	private String name;
}
