package br.ind.powerx.gestaoOperacional.services.order.definition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import lombok.Getter;

@Getter
public class XLSXReportDefinition<T>{

    private List<XLSXColumn<T>> columnDefinitions;

    private XLSXReportDefinition(List<XLSXColumn<T>> columnDefinitions) {
        this.columnDefinitions = columnDefinitions;
    }

    public static class Builder<T> {
        private List<XLSXColumn<T>> columnDefinitions = new ArrayList<>();

        public Builder<T> addColumn(String header, Function<T, ?> valueExtractor) {
            this.columnDefinitions.add(new XLSXColumn<>(header, valueExtractor));
            return this;
        }

        public XLSXReportDefinition<T> build() {
            return new XLSXReportDefinition<T>(columnDefinitions);
        }
    }
}
