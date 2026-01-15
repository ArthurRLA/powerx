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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "groups")
public class Group {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "name", length = 255, nullable = false, unique = true)
	private String name;
	
	@OneToMany(mappedBy = "group", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@OrderBy("fantasyName ASC")
	private List<Customer> customers = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(name = "group_product",
    joinColumns = @JoinColumn(name = "group_id"),
    inverseJoinColumns = @JoinColumn(name = "product_id"))
	@OrderBy("productCode ASC")
	private List<Product> products = new ArrayList<>();
	
	public void addCustomer(Customer customer) {
        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
            if (customer.getGroup() != this) {
                customer.setGroup(this);
            }
        }
    }

    public void removeCustomer(Customer customer) {
        if (customers.remove(customer)) {
            if (customer.getGroup() == this) {
                customer.setGroup(null);
            }
        }
    }
	
	public void addProduct(Product product) {
		if(product != null && !products.contains(product)) {
			products.add(product);
			product.addGroup(this);
		}
	}
	
	public void removeProduct(Product product) {
		if(products.remove(product)) {
			product.removeGroup(this);
		}
	}
	
	
}





















