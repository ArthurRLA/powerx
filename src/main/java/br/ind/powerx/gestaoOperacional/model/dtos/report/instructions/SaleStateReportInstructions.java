package br.ind.powerx.gestaoOperacional.model.dtos.report.instructions;

import java.time.LocalDate;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.enums.ReportFileFormat;
import br.ind.powerx.gestaoOperacional.model.enums.State;
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
public class SaleStateReportInstructions{
	
	private LocalDate startDate;
	private LocalDate endDate;
	private List<State> states;
	private boolean details;
	private ReportFileFormat fileFormat;
	
}
