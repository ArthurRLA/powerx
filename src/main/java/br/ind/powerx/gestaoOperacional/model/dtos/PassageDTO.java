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
public class PassageDTO {
    private Long customerId;
    private LocalDate referenceDate;
    private List<PassageItemDTO> items;
}
