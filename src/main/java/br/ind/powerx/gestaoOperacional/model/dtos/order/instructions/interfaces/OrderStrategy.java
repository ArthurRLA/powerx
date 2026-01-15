package br.ind.powerx.gestaoOperacional.model.dtos.order.instructions.interfaces;

import br.ind.powerx.gestaoOperacional.services.order.definition.XLSXReportDefinition;

public interface OrderStrategy<T> {

	XLSXReportDefinition<T> definition();
	String format();
}
