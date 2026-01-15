package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_stock")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductStock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@OneToMany(mappedBy = "productStock", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<ProductStockItem> productStockItems = new ArrayList<>();
	
	@OneToOne(mappedBy = "productStock")
	private CommercialMonitoring commercialMonitoring;
	
	public void addProductStockItem(ProductStockItem item) {
		if(item != null && !productStockItems.contains(item)) {
			productStockItems.add(item);
			if(item.getProductStock() != this) {
				item.setProductStock(this);
			}
			
		}
	}
	
	public void removeProductStockItem(ProductStockItem item) {
		if(productStockItems.remove(item)) {
			if(item.getProductStock() == this) {
				item.setProductStock(null);
			}
		}
	}
	
	public BigDecimal getTotalBalance() {
        return productStockItems.stream()
            .filter(item -> item.getQuantity() != null && item.getQuantity() != 0)
            .map(ProductStockItem::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
	
	@Override
	public String toString() {
		return "id cliente: " + customer.getId() +
				"\nitems: " + productStockItems;
		
	}
}
