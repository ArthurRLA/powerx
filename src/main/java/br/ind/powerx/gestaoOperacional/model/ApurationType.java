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
@EqualsAndHashCode(of = "name")
@ToString
@Getter
@Setter
@Entity
@Table(name = "apuration_type")
public class ApurationType {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	@ToString.Exclude
	@OneToMany(mappedBy = "apurationType", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Incentive> incentives = new ArrayList<>();
	
	@ManyToMany(mappedBy = "apurationTypes", cascade = CascadeType.ALL)
    private List<Employee> employees = new ArrayList<>();
	
	public void addIncentive(Incentive incentive) {
		if(incentive != null && !incentives.contains(incentive)) {
			incentives.add(incentive);
			incentive.setApurationType(null);
		}
	}
	
	public void removeIncentive(Incentive incentive) {
		if(incentives.remove(incentive)) {
			incentive.setApurationType(null);
		}
	}

	public void addEmployee(Employee employee) {
		if(!employees.contains(employee)) {
			employees.add(employee);
			employee.addApurationType(this);
		}
		
	}

	public void removeEmployee(Employee employee) {
		if(employees.remove(employee)) {
			employee.removeApurationType(this);
		}
		
	}
}
