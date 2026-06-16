package br.ind.powerx.gestaoOperacional.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.ind.powerx.gestaoOperacional.model.enums.Position;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import br.ind.powerx.gestaoOperacional.util.DbTextUpper;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "users")
public class User{
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(name = "unydoft_code", length = 10)
    private String unysoftCode;

    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 14)
    private String cpf;
    
    @Past
    private LocalDate birthday;
    
    @Column(length = 255)
    private String address;

    /** Matrícula / registro de RH */
    @Column(length = 50)
    private String rh;

    @Column(name = "vehicle_brand", length = 80)
    private String vehicleBrand;

    @Column(name = "vehicle_model", length = 80)
    private String vehicleModel;

    @Column(name = "vehicle_year")
    private Integer vehicleYear;

    @Column(name = "vehicle_fuel", length = 40)
    private String vehicleFuel;

    @Column(nullable = false, unique = true, length = 150)
    private String email;
    
    @Column( unique = true, length = 20)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Position position;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private State state;

    @Column(nullable = false)
    private boolean active; 

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;
    
    @Column(name = "start_of_activities", updatable = false)
    private LocalDate startOfActivities;
    
    @Column(name = "state_id", nullable = true)
    private Long stateId;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
    @OrderBy("fantasyName ASC")
    private List<Customer> customers = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Prevision> previsions = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Incentive> incentives = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommercialMonitoring> commercialMonitoring = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Revenue> revenues = new ArrayList<>();

    public User(String email, String passwordHash, String role) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.role = role;
	}

    public Position getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = Position.fromString(position);
    }
    
    public void setPosition(Position position) {
        this.position = position;
    }

    public State getState() {
        return state;
    }

    public void setState(String state) {
        this.state = State.fromString(state);
    }
    
    public void setState(State state) {
        this.state = state;
    }

    public boolean isActive() { 
        return active;
    }

    public void setActive(boolean status) { 
        this.active = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public List<Prevision> getPrevisions() {
        return previsions;
    }
    
    public void addCustomer(Customer customer) {
        if (customer != null && !customers.contains(customer)) {
            customers.add(customer);
            if (customer.getUser() != this) {
                customer.setUser(this);
            }
        }
    }

    public void removeCustomer(Customer customer) {
        if (customers.remove(customer)) {
            if (customer.getUser() == this) {
                customer.setUser(null);
            }
        }
    }


    public void addPrevision(Prevision prevision) {
        if (prevision != null && !previsions.contains(prevision)) {
            previsions.add(prevision);
            prevision.setUser(this);
        }
    }

    public void removePrevision(Prevision prevision) {
        if (previsions.remove(prevision)) {
            prevision.setUser(null);
        }
    }
    
    public void addCommercialMonitoring(CommercialMonitoring monitoring) {
    	if(monitoring != null && !commercialMonitoring.contains(monitoring)) {
    		commercialMonitoring.add(monitoring);
    		if(monitoring.getUser() != this) {
    			monitoring.setUser(this);
    		}
    	}
    }
    
    public void removeCommercialMonitoring(CommercialMonitoring monitoring) {
    	if(commercialMonitoring.remove(monitoring)) {
    		if(monitoring.getUser() == this) {
    			monitoring.setUser(null);
    		}
    	}
    }
    
    public void addRevenue(Revenue revenue) {
    	if(revenue != null && !revenues.contains(revenue)) {
    		revenues.add(revenue);
    		if(revenue.getUser() != this) {
    			revenue.setUser(this);
    		}
    	}
    }
    
    public void removeRevenue(Revenue revenue) {
    	if(revenues.remove(revenue)) {
    		if(revenue.getUser() == this) {
    			revenue.setUser(null);
    		}
    	}
    }

	@PrePersist
	@PreUpdate
	private void normalizeVarcharFields() {
		unysoftCode = DbTextUpper.upper(unysoftCode);
		name = DbTextUpper.upper(name);
		cpf = DbTextUpper.upper(cpf);
		address = DbTextUpper.upper(address);
		rh = DbTextUpper.upper(rh);
		vehicleBrand = DbTextUpper.upper(vehicleBrand);
		vehicleModel = DbTextUpper.upper(vehicleModel);
		vehicleFuel = DbTextUpper.upper(vehicleFuel);
		phone = DbTextUpper.upper(phone);
		role = DbTextUpper.upper(role);
	}
    
    @Override
    public String toString() {
    	return "Usuario:" +
    			"\nId - " + this.id +
    			"\nNome - " + this.name +
    			"\nEmail - " + this.email +
    			"\nRole - " + this.role +
    			"\nCargo - " + this.position +
    			"\nRegião - " + this.state +
    			"\nAtivo - " + this.active;
    			
    }
   
}
