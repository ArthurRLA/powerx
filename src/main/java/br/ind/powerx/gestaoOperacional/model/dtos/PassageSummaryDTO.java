package br.ind.powerx.gestaoOperacional.model.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PassageSummaryDTO {
    private Integer passageNumber;
    private LocalDate referenceDate;
    private String customerName;
    private Integer totalQuantity;
}
