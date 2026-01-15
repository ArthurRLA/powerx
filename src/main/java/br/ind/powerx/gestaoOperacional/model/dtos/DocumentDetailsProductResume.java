package br.ind.powerx.gestaoOperacional.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentDetailsProductResume {

    private String productCode;
    private String productName;
    private Integer consultantQuantity;
    private Integer mechanicQuantity;
}
