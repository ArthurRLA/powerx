package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

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
public class RevenueDetailsDto {

	private Long id;
	private Integer invoiceNumber;
	private Integer unysoftId;
	private String user;
	private CustomerUniqueInfos customer;
	private String operationType;
	private LocalDate date;
	private BigDecimal balance;
	private List<RevenueItemDetailsDto> items;
	
	public BigDecimal getTotalBalance() {
		return this.items.stream()
				.map(RevenueItemDetailsDto::getSubTotalValue)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
}
