package br.ind.powerx.gestaoOperacional.model.dtos;

import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleReportDTO {

	private String employeeName;
    private List<ProductReportDTO> products;
    private Integer total;
    
    public SaleReportDTO(String employeeName,  List<ProductReportDTO> products) {
    	this.employeeName = employeeName;
    	this.products = products;
    	this.total = getTotal(products);
    }

    public Integer getTotal(List<ProductReportDTO> products) {
    	return products.stream()
    		.map(ProductReportDTO::getQuantity)
    		.filter(Objects::nonNull)
    		.reduce(0, Integer::sum);
    }
}
