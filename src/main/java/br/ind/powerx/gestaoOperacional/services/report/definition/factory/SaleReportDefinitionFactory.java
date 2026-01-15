package br.ind.powerx.gestaoOperacional.services.report.definition.factory;
import org.springframework.stereotype.Component;
import br.ind.powerx.gestaoOperacional.model.Sale;
import br.ind.powerx.gestaoOperacional.model.enums.ReportType;
import br.ind.powerx.gestaoOperacional.services.report.definition.ReportDefinition;
@Component
public class SaleReportDefinitionFactory {
    public ReportDefinition<Sale> salesByStateReport(String d, ReportType t) { return null; }
    public ReportDefinition<Sale> salesByFlagReport(String d, ReportType t) { return null; }
    public ReportDefinition<Sale> salesByGroupReport(String d, ReportType t) { return null; }
    public ReportDefinition<Sale> salesByCustomerReport(String d, ReportType t) { return null; }
    public ReportDefinition<Sale> salesByDateReport(String d, ReportType t) { return null; }
}
