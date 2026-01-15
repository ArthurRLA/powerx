package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.enums.OperationType;
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
public class RevenueDetailsEditDto {

	private Long id;
	private Integer invoiceNumber;
	private Integer unysoftId;
	private OperationType operationType;
	private Long customer; 
	private LocalDate date;
	private List<RevenueItemDto> items;
	
	private List<OperationType> operationTypes;
	private List<CustomerUniqueInfos> customers;
	private List<ProductDTO> products;
	
}
