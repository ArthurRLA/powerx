package br.ind.powerx.gestaoOperacional.util.interfaces;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.enums.ReportType;

public interface IncentiveReportPDFGenerator {

	byte[] generatePDF(List<Incentive> incentives, LocalDate startDate, LocalDate endDate, ReportType reportType) throws IOException;
}
