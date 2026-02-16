package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;

import br.ind.powerx.gestaoOperacional.model.enums.IncentiveStatus;
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
public class CustomerDate {

	private String customerName;
	private LocalDate date;
	private IncentiveStatus status;
	
	public CustomerDate(String customerName, LocalDate date) {
		this.customerName = customerName;
		this.date = date;
	}
}
