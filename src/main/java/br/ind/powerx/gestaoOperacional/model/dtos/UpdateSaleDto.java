package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;

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
public class UpdateSaleDto {

    private Integer documentNumber;
    private List<UpdateSaleItemDTO> consultantSales;
    private List<UpdateSaleItemDTO> tinkerSales;
    private List<UpdateSaleItemDTO> mechanicSales;
}
