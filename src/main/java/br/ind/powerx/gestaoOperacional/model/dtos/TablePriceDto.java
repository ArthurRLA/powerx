package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TablePriceDto {

	private Long id;
	private String customer;
	private String product;
	private BigDecimal price;
}
