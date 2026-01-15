package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record EmployeeBasicInfosDTO(@CPF
		String cpf,
		@NotBlank
		@NotNull
		String name,
		@NotNull
		@NotBlank
		@Email
		String email, 
		@NotNull
		@NotBlank
		String phone,
		@Past
		@NotNull
		LocalDate dateOfBirth) {

}
