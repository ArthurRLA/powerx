package br.ind.powerx.gestaoOperacional.model.dtos.crud;

import java.math.BigDecimal;

public record CrudSearchTablePriceRow(Long id, String customerFantasyName, String productCode, String productName,
		BigDecimal price) {
}
