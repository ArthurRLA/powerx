package br.ind.powerx.gestaoOperacional.model.dtos.report.instructions;

import java.time.LocalDate;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.enums.ReportFileFormat;
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
public class SaleCustomerReportInstructions{
	
	private LocalDate startDate;
	private LocalDate endDate;
	private List<Long> customers;
	private boolean details;
	private ReportFileFormat fileFormat;
	
}
