package br.ind.powerx.gestaoOperacional.model.dtos;

import br.ind.powerx.gestaoOperacional.model.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OperationTypeDto {

	private OperationType id;
	private String name;
}
