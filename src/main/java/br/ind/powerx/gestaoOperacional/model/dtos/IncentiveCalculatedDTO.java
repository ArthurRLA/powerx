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
public class IncentiveCalculatedDTO {

    private Integer saleDocumentNumber;
    private String referenceDate;
    private State state;
    private String customerName;
    private String customerCnpj;
    private String paymentMethod;

    private List<IncentiveDTO> ccIncentives;
    private List<IncentiveDTO> nfsIncentives;

    private BigDecimal totalCc;
    private BigDecimal totalNfs;
}
