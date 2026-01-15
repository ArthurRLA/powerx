package br.ind.powerx.gestaoOperacional.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomerBasicInfosDto {

	private boolean active;
	private String groupName;
	private String unysoftCode;
	private String cnpj;
	private String registeredName;
	private String fantasyName;
	private String currentAccountBalance;
	private String address;
	private String userName;
	private String industryName;
	private String flagName;
	private String mechanicApurationName;
}
