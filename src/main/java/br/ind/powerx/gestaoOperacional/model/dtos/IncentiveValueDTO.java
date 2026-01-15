package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;

public record IncentiveValueDTO(Long customerId, Long functionId, Long apurationTypeId, BigDecimal ccValue, BigDecimal nfsValue, BigDecimal overValue) {

}
