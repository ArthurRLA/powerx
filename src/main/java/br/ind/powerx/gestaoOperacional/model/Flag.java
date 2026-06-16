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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import br.ind.powerx.gestaoOperacional.util.DbTextUpper;

@Entity
@Table(name = "flag")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Flag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", unique = true, nullable = false)
	private String name;
	
	@OneToMany(mappedBy = "flag", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Customer> customers = new ArrayList<>();
	
	public void addCustomer(Customer customer) {
        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
            if (customer.getFlag() != this) {
                customer.setFlag(this);
            }
        }
    }

    public void removeCustomer(Customer customer) {
        if (customers.remove(customer)) {
            if (customer.getFlag() == this) {
                customer.setFlag(null);
            }
        }
    }

	@PrePersist
	@PreUpdate
	private void normalizeVarcharFields() {
		name = DbTextUpper.upper(name);
	}
}














