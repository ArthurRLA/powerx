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
public class EmployeeEditDto {

	private Long id;
	private String cpf;
	private String name;
	private String email;
	private String phone;
	private String birthDate;
	private List<Long> selectedFunctions;
	private List<Long> selectedCustomers;
	private List<Long> selectedApurationTypes;
	private Long selectedPaymentMethod;
	private boolean active;
	
	private List<FunctionDto> functions;
	private List<CustomerDto> customers;
	private List<ApurationTypeDto> apurationTypes;
	private List<PaymentMethodDTO> paymentMethods;
}
