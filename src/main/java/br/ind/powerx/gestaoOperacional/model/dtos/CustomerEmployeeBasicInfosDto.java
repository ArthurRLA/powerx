package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CustomerEmployeeBasicInfosDto {

	private String cpf;
	private String name;
	private Set<String> functions = new HashSet<>();
	private Set<String> apurationTypes = new HashSet<>();
}
