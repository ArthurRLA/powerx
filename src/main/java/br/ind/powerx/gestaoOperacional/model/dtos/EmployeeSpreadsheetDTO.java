package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

public record EmployeeSpreadsheetDTO(
		@CPF
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
		LocalDate dateOfBirth,
		Long func_id1,
		Long func_id2,
		Long cust_id1,
		Long cust_id2,
		Long cust_id3,
		Long cust_id4,
		Long cust_id5,
		Long cust_id6,
		Long cust_id7,
		Long apur_id1,
		Long apur_id2,
		Long pay_id
		) {

}
