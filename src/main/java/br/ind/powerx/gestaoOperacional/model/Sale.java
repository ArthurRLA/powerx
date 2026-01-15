  package br.ind.powerx.gestaoOperacional.model;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "sale")
public class Sale {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@PastOrPresent
	@Column(name = "reference_date", nullable = false)
	private LocalDate referenceDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "product_id")
	private Product product;
	
	@Column(name = "quantity", nullable = false)
	private Integer quantity;
	
	@Column(name = "document_number", nullable = false)
    private Integer documentNumber;
	
	private String function;
	
	public Sale(Customer customer, Employee employee, Product product, Integer quantity, String function) {
		this.customer = customer;
		this.employee = employee;
		this.product = product;
		this.quantity = quantity;
		this.function = function;
	}
	
	public void setCustomer(Customer customer) {
		if(customer != null) {
			this.customer = customer;
		}
	}
	
	public void setEmployee(Employee employee) {
		if(employee != null) {
			this.employee = employee;
		}
	}
	
	public void setProduct(Product product) {
		if(product != null) {
			this.product = product;
		}
	}
	
	@Override
	public String toString() {
		
		return "Venda - Ordem " + this.getDocumentNumber() + 
				"\nCliente: Nome - " + this.customer.getRegisteredName() + 
				"\ncnpj - " + this.customer.getCnpj() +
				"\nVendedor: Nome - " + this.employee.getName() + 
				"\nCpf - " + this.employee.getCpf() + 
				"\nProduto: Nome - " + this.product.getProductName() + 
				"\nCodigo - " + this.product.getProductCode() +
				"\nQuantidade: Nome - " + this.quantity + "\n"; 
	}
	
}
























