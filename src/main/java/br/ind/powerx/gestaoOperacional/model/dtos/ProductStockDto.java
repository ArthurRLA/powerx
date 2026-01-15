package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductStockDto {

	private Long customer;
	private List<ProductStockItemDto> productStockItems;
	
	@Override
	public String toString() {
		return "id cliente: " + customer +
				"\nitems:\n " + productStockItems;
	}
}
