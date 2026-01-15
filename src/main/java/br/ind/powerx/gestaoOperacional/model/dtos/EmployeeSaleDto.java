package br.ind.powerx.gestaoOperacional.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeSaleDto {

    private Long employeeId;
    private String employeeName;
    private Long productId;
    private String productName;
    private Integer quantity;
}
