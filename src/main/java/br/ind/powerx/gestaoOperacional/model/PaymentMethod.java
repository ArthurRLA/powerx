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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "name")
@Getter
@Setter
@Entity
@Table(name = "payment_method")
public class PaymentMethod {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	@OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Incentive> incentives = new ArrayList<>();
	
	@OneToMany(mappedBy = "paymentMethod", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private List<Employee> Employees = new ArrayList<>();
	
	public void addIncentive(Incentive incentive) {
		if(incentive != null && !incentives.contains(incentive)) {
			incentives.add(incentive);
			incentive.setPaymentMethod(this);
		}
	}
	
	public void removeIncentive(Incentive incentive) {
		if(incentives.remove(incentive)) {
			incentive.setPaymentMethod(null);
		}
	}

	public void addEmployee(Employee employee) {
		if(employee != null && !Employees.contains(employee)) {
			Employees.add(employee);
			employee.setPaymentMethod(this);
		}
	}

	public void removeEmployee(Employee employee) {
		if(Employees.remove(employee)) {
			employee.setPaymentMethod(null);
		}
	}
}

