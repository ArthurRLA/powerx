package br.ind.powerx.gestaoOperacional.model.dtos.crud;

public record CrudSearchCustomerRow(Long id, String fantasyName, String unysoftCode, String cnpj,
		String registeredName, String address, boolean active, String userName, String groupName,
		String mechanicApurationName, String industryName, String flagName) {
}
