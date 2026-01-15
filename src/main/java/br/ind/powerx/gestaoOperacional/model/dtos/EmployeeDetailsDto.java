package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeDetailsDto {
	
		private Long id;
		private String cpf;
		private String name;
		private String email;
		private String phone;
		private String birthDate;
		private List<String> functions;
		private List<String> customers; 
		private List<String> apurationTypes;
		private String paymentMethod;
		private boolean active;


}
