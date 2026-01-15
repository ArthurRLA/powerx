package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TablePriceEditDto {

	private Long id;
	private Long customer;
	private Long product;
	private BigDecimal price;
	
	private List<CustomerDto> customers;
	private List<ProductBasicDto> products;
}
