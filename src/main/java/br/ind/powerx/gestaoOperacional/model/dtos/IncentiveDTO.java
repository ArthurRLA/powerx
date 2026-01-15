package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IncentiveDTO {
    
    private String cpf;
    private String employeeName;
    private BigDecimal incentiveValue;
    private String functionName;
}

