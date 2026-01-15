package br.ind.powerx.gestaoOperacional.services.report.definition.factory;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import br.ind.powerx.gestaoOperacional.model.Incentive;
import br.ind.powerx.gestaoOperacional.model.enums.ReportType;
import br.ind.powerx.gestaoOperacional.services.report.definition.GroupingLevel;
import br.ind.powerx.gestaoOperacional.services.report.definition.ReportDefinition;

@Component
public class IncentiveReportDefinitionFactory {

    public ReportDefinition<Incentive> incentivesByCustomerReport(String dateRange, ReportType type) {
        // Agrupamento em dois níveis (Apuração > Cliente) para limpar a tabela interna
        List<GroupingLevel<Incentive, ?>> groups = Arrays.asList(
            new GroupingLevel<>(
                "Apuração", 
                i -> i.getApurationType().getName(), 
                Incentive::getApurationType, 
                this::calculateSubtotal
            ),
            new GroupingLevel<>(
                "Cliente", 
                i -> i.getCustomer().getFantasyName(), 
                Incentive::getCustomer, 
                this::calculateSubtotal
            )
        );
        return createIncentiveDefinition("Relatório de Incentivos por Cliente", dateRange, type, groups);
    }

    public ReportDefinition<Incentive> incentivesByUserReport(String dateRange, ReportType type) {
        List<GroupingLevel<Incentive, ?>> groups = Collections.singletonList(
            new GroupingLevel<>(
                "Usuário Responsável", 
                i -> i.getUser().getName(), 
                Incentive::getUser, 
                this::calculateSubtotal
            )
        );
        return createIncentiveDefinition("Relatório de Incentivos por Usuário", dateRange, type, groups);
    }

    public ReportDefinition<Incentive> incentivesByGroupReport(String dateRange, ReportType type) {
        List<GroupingLevel<Incentive, ?>> groups = Collections.singletonList(
            new GroupingLevel<>(
                "Região/Estado", 
                i -> i.getState().toString(), 
                Incentive::getState, 
                this::calculateSubtotal
            )
        );
        return createIncentiveDefinition("Relatório de Incentivos por Região", dateRange, type, groups);
    }

    public ReportDefinition<Incentive> incentivesByDateReport(String dateRange, ReportType type) {
        List<GroupingLevel<Incentive, ?>> groups = Collections.singletonList(
            new GroupingLevel<>(
                "Data", 
                i -> i.getReferenceDate().toString(), 
                Incentive::getReferenceDate, 
                this::calculateSubtotal
            )
        );
        return createIncentiveDefinition("Relatório de Incentivos por Período", dateRange, type, groups);
    }

    private BigDecimal calculateSubtotal(List<Incentive> incentives) {
        return incentives.stream()
                .map(Incentive::getIncentiveValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Centraliza a criação da definição com o layout de 5 colunas otimizado.
     */
    private ReportDefinition<Incentive> createIncentiveDefinition(String title, String dateRange, ReportType type, List<GroupingLevel<Incentive, ?>> groups) {
        
        // 1. Definição das colunas (Removido o que já está no agrupamento para ganhar espaço)
        List<String> headers = Arrays.asList("ID", "CPF", "Nome", "Função", "Valor");
        
        // 2. Distribuição de larguras (Garante 40% para o Nome, evitando o amontoamento)
        List<Float> widths = Arrays.asList(8f, 17f, 40f, 25f, 10f);

        String finalTitle = title + (type == ReportType.WITH_DETAILS ? " Com detalhamento" : "");

        return new ReportDefinition.Builder<Incentive>()
                .title(finalTitle)
                .date(dateRange)
                .groupingLevels(groups)
                .detailmentHeader(headers)
                .columnWidths(widths)
                .detailsRenderer(list -> list.stream().map(incentive -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    
                    row.put("ID", incentive.getId().toString());
                    row.put("CPF", incentive.getCpf());
                    row.put("Nome", incentive.getEmployee().getName());
                    row.put("Função", incentive.getEmployeeFunction().getName());
                    row.put("Valor", incentive.getIncentiveValue().toString());

                    return row;
                }).collect(Collectors.toList()))
                .build();
    }
}