package br.ind.powerx.gestaoOperacional.model.dtos.report.instructions;

import java.time.LocalDate;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.enums.ReportFileFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IncentiveCustomerReportInstructions{
	
	private LocalDate startDate;
	private LocalDate endDate;
	private List<Long> customers;
	private List<Long> apurationTypes;
	private ReportFileFormat fileFormat;
	
}
