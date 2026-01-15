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
public class DocumentDetailsSale {

    private String employeeName;
    private String function;
    private Integer totalQuantity;

    private List<DocumentDetailsSaleProduct> products;
}
