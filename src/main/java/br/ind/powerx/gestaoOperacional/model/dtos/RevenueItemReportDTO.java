package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RevenueItemReportDTO {
    private String date;  
    private Integer invoiceNumber;
    private String operationType;
    private String customer;
    private String cnpj;  
    private String user;
    private String cpf;  
    private String productCode;   
    private String productName;  
    private Integer quantity;
    private BigDecimal subTotal;
}
