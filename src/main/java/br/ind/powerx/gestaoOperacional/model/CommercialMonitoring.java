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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "commercial_monitoring")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CommercialMonitoring {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "monitoring_name", unique = true, nullable = false, length = 100)
	private String name;
	
	@OneToOne
	@JoinColumn(name = "customer_id")
	private Customer customer;
	
    @OneToMany(mappedBy = "commercialMonitoring", cascade = { CascadeType.MERGE, CascadeType.PERSIST }, fetch = FetchType.LAZY)
    @OrderBy("product")
    private List<MonitoringSale> monitoringSales = new ArrayList<>();
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_stock_id")
    private ProductStock productStock;
    
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
 
    public void addMonitoringSale(MonitoringSale sale) {
    	if(sale != null && !monitoringSales.contains(sale)) {
    		monitoringSales.add(sale);
    		if(sale.getCommercialMonitoring() != this) {
    			sale.setCommercialMonitoring(this);
    		}
    	}
    }
    
    public void removeMonitoringSale(MonitoringSale sale) {
    	if(monitoringSales.remove(sale)){
    		if(sale.getCommercialMonitoring() == this) {
    			sale.setCommercialMonitoring(null);
    		}
    	}
    }
	
}
























