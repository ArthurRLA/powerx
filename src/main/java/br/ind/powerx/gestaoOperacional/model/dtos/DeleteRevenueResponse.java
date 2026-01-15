package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter

public class DeleteRevenueResponse {

	private CustomerUniqueInfos customer;
	private List<RevenueItemDetailsDto> items;
	private String message;
}
