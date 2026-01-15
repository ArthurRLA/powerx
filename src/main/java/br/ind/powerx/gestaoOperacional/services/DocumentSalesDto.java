package br.ind.powerx.gestaoOperacional.services;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentSalesDto {

    private LocalDate referenceDate;
	private String customerName;
	private String employeeName;
	private String productName;
	private String productCode;
	private Integer quantity;
	private Integer documentNumber;
}
