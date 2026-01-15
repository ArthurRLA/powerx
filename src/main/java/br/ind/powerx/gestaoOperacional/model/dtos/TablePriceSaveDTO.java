package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;

public record TablePriceSaveDTO(Long customer, Long product, BigDecimal price) {

}
