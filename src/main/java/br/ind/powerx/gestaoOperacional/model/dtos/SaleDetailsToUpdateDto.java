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
public class SaleDetailsToUpdateDto {

    private Integer documentNumber;
    private String customerName;
    private String date;
    private List<EmployeeSaleDto> consultantSales;
    private List<EmployeeSaleDto> tinkerSales;
    private List<EmployeeSaleDto> mechanicSales;
    private List<EmployeeDTO> consultants;
    private List<EmployeeDTO> mechanics;
    private List<EmployeeDTO> tinkers;
    private List<ProductDTO> products;
}
