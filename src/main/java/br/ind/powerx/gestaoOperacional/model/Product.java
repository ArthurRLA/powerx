package br.ind.powerx.gestaoOperacional.model;

import java.util.ArrayList;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "productCode")
@ToString
@Entity
@Table(name = "product")
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "unydoft_code", unique = true, length = 10)
    private String unysoftCode;
	
	@Column(name = "product_code", nullable = false, unique = true)
	private String productCode;
	
	@Column(name = "product_name", nullable = false, unique = true)
	private String productName;
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<IncentiveValue> incentiveValues = new ArrayList<>();
	
	@ToString.Exclude
	@ManyToMany(mappedBy = "products")
	private List<Group> groups = new ArrayList<>();
	
	@ToString.Exclude
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<TablePrice> tables = new ArrayList<>();
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<RevenueItem> orders = new ArrayList<>();
	
	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
	private List<MonitoringSale> monitoringSales = new ArrayList<>();
	
	public void addIncentiveValue(IncentiveValue incentiveValue) {
		if(incentiveValue != null && !incentiveValues.contains(incentiveValue)) {
			incentiveValues.add(incentiveValue);
			incentiveValue.setProduct(this);
		}
	}
	
	public void removeIncentiveValue(IncentiveValue incentiveValue) {
		if(incentiveValues.remove(incentiveValue)) {
			incentiveValue.setProduct(null);
		}
	}
	
	public void addGroup(Group group) {
		if(group != null && !groups.contains(group)) {
			groups.add(group);
		}
	}
	
	public void removeGroup(Group group) {
		groups.remove(group);
		
	}
	
	public void addTable(TablePrice table) {
		if(table != null && !tables.contains(table)) {
			tables.add(table);
			if(table.getProduct() != this) {
				table.setProduct(this);
			}
		}
	}
	
	public void removeTable(TablePrice table) {
		if(tables.remove(table)) {
			if(table.getProduct() == this) {
				table.setProduct(null);
			}
		}
	}
	
	public void addOrder(RevenueItem order) {
		if(order != null && !orders.contains(order)) {
			orders.add(order);
			if(order.getProduct() != this) {
				order.setProduct(this);
			}
		}
	}
	
	public void removeOrder(RevenueItem order) {
		if(orders.remove(order)) {
			if(order.getProduct() == this) {
				order.setProduct(null);
			}
		}
	}
	
}

























