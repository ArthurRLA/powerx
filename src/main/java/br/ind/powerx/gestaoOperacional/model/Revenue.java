package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.ind.powerx.gestaoOperacional.model.enums.OperationType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "revenue")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class Revenue {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "invoice_number", unique = true, nullable = false)
	private Integer invoiceNumber;
	
	@Column(name = "unysoft_id")
	private Integer unysoftId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "operation_type", nullable = false)
	private OperationType operationType;
	
	@Column(name = "date")
	private LocalDate date;
	
	@ManyToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
	@OneToMany(mappedBy = "revenue", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<RevenueItem> revenueItems = new ArrayList<>();
	
	private BigDecimal total;
	
	public void addRevenueItem(RevenueItem revenueItem) {
		if (revenueItem != null && !revenueItems.contains(revenueItem)) {
			revenueItems.add(revenueItem);
			if (revenueItem.getRevenue() != this) {
				revenueItem.setRevenue(this);
			}
		}
	}

	public void removeRevenueItem(RevenueItem revenueItem) {
		if (revenueItems.remove(revenueItem)) {
			if (revenueItem.getRevenue() == this) {
				revenueItem.setRevenue(null);
			}
		}
	}

	public BigDecimal totalValue() {
		return revenueItems.stream()
				.filter(item -> item.getQuantity() != 0)
				.map(item -> item.getSubTotal())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	
	@Override
	public String toString() {
		return "N°: " + invoiceNumber +
				"\nRepresentante: " + user.getName() +
				"\nNatureza: " + operationType +
				"\nData: " + date +
				"\nCliente: " + customer.getFantasyName() + 
				"\nTotal: " + total +
				"\nItems: \n" + revenueItems;
	}
}
