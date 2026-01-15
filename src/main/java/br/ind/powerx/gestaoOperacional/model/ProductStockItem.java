package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_stock_item")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ProductStockItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	@EqualsAndHashCode.Include
	private Product product;
	
	private Integer quantity;
	
	@ManyToOne
	@JoinColumn(name = "product_stock_id")
	private ProductStock productStock;
	
	public BigDecimal getTotalCcValue() {
        return product.getIncentiveValues().stream()
            .filter(iv -> iv.getCustomer().getId().equals(this.productStock.getCustomer().getId()))
            .map(IncentiveValue::getCcValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBalance() {
        return getTotalCcValue()
            .multiply(BigDecimal.valueOf(quantity));
    }
    
    @Override
    public String toString() {
    	return "id produto: " + product.getId() +
    			"quantidade: " + quantity;
    }
}





