package br.ind.powerx.gestaoOperacional.model.dtos;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductReportDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	private String productCode;
    private String productName;
    private Integer quantity;

}
