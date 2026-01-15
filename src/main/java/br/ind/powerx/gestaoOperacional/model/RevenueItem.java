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
@Table(name = "revenue_item")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RevenueItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@EqualsAndHashCode.Include
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "revenue_id")
	private Revenue revenue;
	
	@ManyToOne
	@JoinColumn(name = "product_id")
	@EqualsAndHashCode.Include
	private Product product;
	
	private Integer quantity;
	
	private BigDecimal subTotal;
	
	public BigDecimal subTotal() {
		return product.getTables().stream()
				.filter(t -> t.getCustomer() == revenue.getCustomer() && t.getProduct() == product)
				.map(t -> t.getPrice())
				.findFirst()
				.orElse(BigDecimal.ZERO)
				.multiply(new BigDecimal(quantity));
	}
	
	@Override
	public String toString() {
		return "Produto: " + product.getProductCode() + " " + product.getProductName() +
				"\nQuantidade: " + quantity +
				"\nsubTotal: " + subTotal;
	}
}
