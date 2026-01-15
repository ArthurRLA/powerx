package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

public record AddIncentiveValueDTO (Long productId, List<IncentiveValueDTO> incentives){

}
