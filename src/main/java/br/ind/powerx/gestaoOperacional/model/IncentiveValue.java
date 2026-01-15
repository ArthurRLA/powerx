package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "incentive_value")
public class IncentiveValue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "function_id")
	private Function function;
	
	@ToString.Exclude
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;
	
	@NotNull
    @DecimalMin("0.0")
    @Column(name = "cc_value", nullable = false, precision = 10, scale = 2)
	private BigDecimal ccValue;
	
	@NotNull
    @DecimalMin("0.0")
    @Column(name = "nfs_value", nullable = false, precision = 10, scale = 2)
	private BigDecimal nfsValue;
	
    @DecimalMin("0.0")
    @Column(name = "over_value", precision = 10, scale = 2)
	private BigDecimal overValue;
	
	@Override
	public String toString() {
		return "\nValor do Incentivo para: \n" +
				"Cliente - " + customer.getFantasyName() +
				"\nFuncão - " + function.getName() +
				"\nProduto - " + product.getProductCode() + " " + product.getProductName() +
				"\nConta Corrente - " + ccValue +
				"\nNF Serviço - " + nfsValue + "\n\n";
				
				
	}
}
