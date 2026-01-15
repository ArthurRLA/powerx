package br.ind.powerx.gestaoOperacional.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerCurrentAccountProductsItemsDto {
	private Long id;
	private String productCode;
	private String productName;
	private String quantity;
	private String ccValue;
	private String balance;
}
