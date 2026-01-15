package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import br.ind.powerx.gestaoOperacional.model.enums.State;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
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
@Table(name = "incentive")
public class Incentive{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@PastOrPresent
	@Column(name = "reference_date", nullable = false)
	private LocalDate referenceDate;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "state", nullable = false)
	private State state;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_method_id")
	private PaymentMethod paymentMethod;
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "apuration_type_id")
	private ApurationType apurationType;
	
	@ManyToOne(cascade = CascadeType.MERGE)
	@JoinColumn(name = "employee_id")
	private Employee employee;
	
	@CPF
	@Column(name = "cpf", nullable = false)
	private String cpf;
	
	@NotNull
    @DecimalMin("0.0")
    @Column(name = "incentive_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal incentiveValue;
	
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "function_id")
	private Function employeeFunction; 
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@Column(name = "sale_document_number", nullable = false)
    private Integer saleDocumentNumber;
	
	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@Override
	public String toString() {
		return  "--------------------------------------------------------" +
				"INCENTIVO " +
				"\nId - " + this.getId() +
				"\nData - " + this.getReferenceDate() +
				"\nRegião - " + this.getState().getState() +
				"\nMétodo de Pagamento - " + this.getPaymentMethod().getName() +
				"\nApuração - " + this.getApurationType().getName() +
				"\nPremiado - " + this.getEmployee().getName() +
				"\nCPF - " + this.getCpf() +
				"\nValor - " + this.getIncentiveValue() +
				"\nCliente - " + this.customer.getFantasyName() +
				"\nCNPJ - " + this.getCustomer().getCnpj() +
				"\nOrdem de venda - " + this.getSaleDocumentNumber() +
				"Usuario responsável - " + this.getUser().getName() +
				"--------------------------------------------------------\n";
		
	}
	
}
























