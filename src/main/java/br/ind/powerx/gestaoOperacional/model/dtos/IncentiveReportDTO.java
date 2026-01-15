package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncentiveReportDTO {

	private String state;
	private String apurationType;
	private String employeeName;
	private BigDecimal total;
	private String function;
	
}
