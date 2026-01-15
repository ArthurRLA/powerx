package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
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
@Table(name = "table_price")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = "id")
public class TablePrice {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "customer_id", nullable = false)
	private Customer customer;
	
	@ManyToOne
	@JoinColumn(name = "product_id", nullable = false)
	private Product product;
	
	@Column(nullable = false)
	private BigDecimal price;
	
	public void setCustomer(Customer customer) {
		this.customer = customer;
		customer.addTable(this);
	}
	
	public void setProduct(Product product) {
		this.product = product;
		product.addTable(this);
	}
	
	@Override
	public String toString() {
		return "\nTABELA: " +
			   "\ncliente: " + customer.getFantasyName() + 
			   "\nproduto:" + product.getProductCode() +
			   "\npreço: " + price;
	}
}
