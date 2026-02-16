package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassageDetailsDTO {
    private Integer passageNumber;
    private LocalDate referenceDate;
    private String customerName;
    private Long customerId;
    private Integer totalQuantity;
    private List<PassageEmployeeDTO> employees;
}
