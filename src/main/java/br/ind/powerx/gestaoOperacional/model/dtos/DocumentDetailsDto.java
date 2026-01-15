package br.ind.powerx.gestaoOperacional.model.dtos;

import java.math.BigDecimal;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.enums.State;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DocumentDetailsDto {

    private Integer documentNumber;
    private String date;
    private String customerName;
    private String customerCnpj;
    private State state;
    private String paymentMethod;
    private Integer salesTotal;
    private Integer tinkerTotal;
    private Integer aplicationsTotal;

    private List<DocumentDetailsSale> consultantSales;
    private List<DocumentDetailsSale> tinkerSales;
    private List<DocumentDetailsSale> mechanicSales;

    private List<DocumentDetailsProductResume> productsResume;

    private List<IncentiveDTO> ccIncentives;
    private List<IncentiveDTO> nfsIncentives;

    private BigDecimal totalCc;
    private BigDecimal totalNfs;
    
}
