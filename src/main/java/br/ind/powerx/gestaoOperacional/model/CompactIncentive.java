package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompactIncentive {
	
	private String saleDocumentNumber;
    private String monthYear;
    private String region;
    private String type;
    private String modality;
    private String cpf;
    private String employeeName;
    private BigDecimal total;
    private String function;
    private String store;
    private String cnpj;
}

