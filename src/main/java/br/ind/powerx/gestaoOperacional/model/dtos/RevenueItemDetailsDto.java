package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RevenueItemDetailsDto {

	private String productCode;
	private String productName;
	private Integer quantity;
	private BigDecimal price;
	private BigDecimal subTotal;
	
	public BigDecimal getSubTotalValue() {
		return this.price.multiply(new BigDecimal(quantity));
	}
}
