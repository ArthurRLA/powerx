package br.ind.powerx.gestaoOperacional.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassageEmployeeDTO {
    private Long employeeId;
    private String employeeName;
    private String employeeCpf;
    private Integer quantity;
}
