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

@Entity
@Table(name = "mechanic_apuration")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class MechanicApuration {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(unique = true, nullable = false, length = 50)
	private String name;
	
	@OneToMany(mappedBy = "mechanicApuration" , cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Customer> customers = new ArrayList<>();
	
	public void addCustomer(Customer customer) {
        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
            if (customer.getMechanicApuration() != this) {
                customer.setMechanicApuration(this);
            }
        }
    }

    public void removeCustomer(Customer customer) {
        if (customers.remove(customer)) {
            if (customer.getMechanicApuration() == this) {
                customer.setMechanicApuration(null);
            }
        }
    }
}






