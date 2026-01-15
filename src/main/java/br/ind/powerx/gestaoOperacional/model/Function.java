package br.ind.powerx.gestaoOperacional.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
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
@Table(name = "function")
public class Function {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", nullable = false, unique = true)
	private String name;
	
	@ManyToMany(mappedBy = "functions")
    private List<Employee> employees = new ArrayList<>();
	
	public void addEmployee(Employee employee) {
		if(employee != null && !employees.contains(employee)) {
			employees.add(employee);
			employee.addFunction(this);
		}
	}
	
	public void removeEmployee(Employee employee) {
		if(employees.remove(employee)) {
			employee.removeFunction(this);
		}
	}
}
