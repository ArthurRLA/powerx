package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import br.ind.powerx.gestaoOperacional.model.enums.Fuel;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "prevision")
public class Prevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", updatable = false, nullable = false)
    private User user;

    @Column(name = "fuel_price", nullable = false)
    private BigDecimal fuelPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Fuel fuel;

    @Column(name = "average_consumption", nullable = false)
    private Double averageConsumption;

    @Column(name = "total", nullable = false)
    private BigDecimal total;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(name = "last_update")
    private LocalDateTime lastUpdate;

    @OneToMany(mappedBy = "prevision", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Route> routes = new ArrayList<>();

    public Prevision() {
    }

    public Prevision(Long id, User user, BigDecimal fuelPrice, Fuel fuel, Double averageConsumption,
                           LocalDateTime creationDate, LocalDateTime lastUpdate, List<Route> routes) {
        this.id = id;
        this.user = user;
        this.fuelPrice = fuelPrice;
        this.fuel = fuel;
        this.averageConsumption = averageConsumption;
        this.creationDate = creationDate;
        this.lastUpdate = lastUpdate;
        this.routes = (routes != null) ? routes : new ArrayList<>();
        this.total = calculateTotal();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public BigDecimal getFuelPrice() {
        return fuelPrice;
    }

    public void setFuelPrice(BigDecimal fuelPrice) {
        this.fuelPrice = fuelPrice;
    }

    public Fuel getFuel() {
        return fuel;
    }

    public void setFuel(Fuel fuel) {
        this.fuel = fuel;
    }

    public Double getAverageConsumption() {
        return averageConsumption;
    }

    public void setAverageConsumption(Double averageConsumption) {
        this.averageConsumption = averageConsumption;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public LocalDateTime getLastUpdate() {
        return lastUpdate;
    }

    public List<Route> getRoutes() {
        return new ArrayList<>(routes);
    }

    public void setRoutes(List<Route> routes) {
        this.routes = (routes != null) ? routes : new ArrayList<>();
        updateTotal();
    }

    public void addRoute(Route route) {
        if (route != null && !routes.contains(route)) {
            routes.add(route);
            route.setPrevision(this);
            updateTotal();
        }
    }

    public void removeRoute(Route route) {
        if (routes.remove(route)) {
            route.setPrevision(null);
            updateTotal();
        }
    }

    @PrePersist
    @PreUpdate
    protected void updateTotal() {
        this.total = calculateTotal();
    }

    private BigDecimal calculateTotal() {
        BigDecimal totalCost = BigDecimal.ZERO;
        for (Route route : this.routes) {
            if (route.getRouteCost() != null) {
                totalCost = totalCost.add(route.getRouteCost());
            }
        }
        return totalCost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Prevision other = (Prevision) obj;
        return Objects.equals(id, other.id);
    }
}
