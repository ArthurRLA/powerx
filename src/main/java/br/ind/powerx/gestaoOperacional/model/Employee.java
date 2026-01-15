package br.ind.powerx.gestaoOperacional.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "employee")
public class Employee {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@CPF
	@Column(name = "cpf", nullable = false, unique = true, length = 14)
	private String cpf;
	
	@Email
	@Column(name = "email", nullable = false, unique = true, length = 255)
	private String email;
	
	@NotBlank
    @Size(max = 255)
	@Column(name = "name", nullable = false, length = 255)
	private String name;
	
	@NotBlank
    @Size(max = 15)
	@Column(name = "phone", nullable = false, length = 255)
	private String phone;
	
	@Past
	@Column(name = "birth_date", nullable = false)
	private LocalDate birthDate;
	
	@Column(name = "active", nullable = false)
	private boolean active;
	
	@ToString.Exclude
	@ManyToMany
	@JoinTable(name = "employee_function",
    joinColumns = @JoinColumn(name = "employee_id"),
    inverseJoinColumns = @JoinColumn(name = "function_id"))
	private List<Function> functions = new ArrayList<>();
	
	@ToString.Exclude
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Sale> sales = new ArrayList<>();
	
	@ToString.Exclude
	@ManyToMany(mappedBy = "employees", cascade = { CascadeType.MERGE, CascadeType.PERSIST })
	private List<Customer> customers = new ArrayList<>();
	
	@ToString.Exclude
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Incentive> incentives = new ArrayList<>();
	
	@ToString.Exclude
	@ManyToMany
	@JoinTable(name = "employee_apuration_type",
    joinColumns = @JoinColumn(name = "employee_id"),
    inverseJoinColumns = @JoinColumn(name = "apuration_type_id"))
	private List<ApurationType> apurationTypes = new ArrayList<>();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_method_id")
	private PaymentMethod paymentMethod;
	
	@OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
	private List<MonitoringSale> monitoringSales = new ArrayList<>();
	
	
	public void addSale(Sale sale) {
		if(sale != null && !sales.contains(sale)) {
			sales.add(sale);
			sale.setEmployee(this);
		}
	}
	
	public void removeSale(Sale sale) {
		if(sales.remove(sale)) {
			sale.setEmployee(null);
		}
	}
	
	public void addFunction(Function function) {
		if(function != null && !functions.contains(function)) {
			functions.add(function);
			function.addEmployee(this);
		}
	}
	
	public void removeFunction(Function function) {
		if(functions.remove(function)) {
			function.removeEmployee(null);
		}
	}
	
	public void addIncentive(Incentive incentive) {
		if(incentive != null && !incentives.contains(incentive)) {
			incentives.add(incentive);
			incentive.setEmployee(this);
		}
	}
	
	public void removeIncentive(Incentive incentive) {
		if(incentives.remove(incentive)) {
			incentive.setEmployee(null);
		}
	}

	public void addCustomer(Customer customer) {
		if(customer != null && !customers.contains(customer)) {
			customers.add(customer);
			customer.addEmployee(this);
		}
	}

	public void removeCustomer(Customer customer) {
		if(customers.remove(customer)) {
			customer.removeEmployee(this);
		}
		
	}
	
	public void addApurationType(ApurationType apurationType) {
		if(!apurationTypes.contains(apurationType)) {
			apurationTypes.add(apurationType);
			apurationType.addEmployee(this);
		}
	}
	
	public void removeApurationType(ApurationType apurationType) {
		if(apurationTypes.remove(apurationType)) {
			apurationType.removeEmployee(this);
		}
	}
	
	public void addMonitoringSale(MonitoringSale sale) {
		if(sale != null && !monitoringSales.contains(sale)) {
			sale.setEmployee(this);
			if(sale.getEmployee() != this) {
				sale.setEmployee(this);
			}
		}
	}
	
	public void removeMonitoringSale(MonitoringSale sale) {
		if(monitoringSales.remove(sale)) {
			if(sale.getEmployee() == this) {
				sale.setEmployee(null);
			}
		}
	}
}

