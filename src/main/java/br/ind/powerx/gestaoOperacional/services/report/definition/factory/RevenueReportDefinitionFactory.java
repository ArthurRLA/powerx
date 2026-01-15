package br.ind.powerx.gestaoOperacional.services.report.definition.factory;
import org.springframework.stereotype.Component;
import br.ind.powerx.gestaoOperacional.model.Revenue;
import br.ind.powerx.gestaoOperacional.model.enums.ReportType;
import br.ind.powerx.gestaoOperacional.services.report.definition.ReportDefinition;
@Component
public class RevenueReportDefinitionFactory {
    public ReportDefinition<Revenue> revenuesByUserReport(String d, ReportType t) { return null; }
    public ReportDefinition<Revenue> revenuesByGroupReport(String d, ReportType t) { return null; }
    public ReportDefinition<Revenue> revenuesByCustomerReport(String d, ReportType t) { return null; }
    public ReportDefinition<Revenue> revenuesByDateReport(String d, ReportType t) { return null; }
}
