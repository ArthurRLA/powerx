package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;

public record UserUpdateDTO(Long id, String unysoftCode, String name, String cpf, String address, LocalDate birthday,
		String email, String phone, String role, String position, String state, boolean active) {

}
