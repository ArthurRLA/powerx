package br.ind.powerx.gestaoOperacional.model.dtos;

public record CustomerUpdateDTO(Long id, String unysoftCode, String cnpj, String registeredName, String fantasyName, String address, boolean active) {

}
