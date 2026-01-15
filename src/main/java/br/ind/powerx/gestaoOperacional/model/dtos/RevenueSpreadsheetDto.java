package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;

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
public class RevenueSpreadsheetDto {
	
	private Integer invoiceNumber;
	private LocalDate date;
	private String operationType;
	private String customerCnpj;
	private Long userId;
	private String productCode;
	private Integer quantity;
}
