package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CustomerSetCurrentAccountDto {

	private String fantasyName;
	private String cnpj;
	private List<CustomerGroupProductsDto> customerProductStockItems = new ArrayList<>();
}
