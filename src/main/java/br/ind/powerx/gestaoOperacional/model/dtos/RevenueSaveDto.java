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
public class RevenueSaveDto {

	private Integer invoiceNumber;
	
	private OperationType operationType;
	
	private LocalDate date;
	
	private Long customerId;
	
	private List<RevenueItemDto> items;
	
}
