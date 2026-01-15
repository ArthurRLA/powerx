package br.ind.powerx.gestaoOperacional.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductStockItemDto {

	private Long product;
	private Integer quantity;
	
	@Override
	public String toString() {
		return "id produto: " + product +
				"\nquantidade: " + quantity;
	}
}
