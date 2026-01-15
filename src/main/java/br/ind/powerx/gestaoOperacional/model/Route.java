package br.ind.powerx.gestaoOperacional.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
@Table(name = "route")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prevision_id", nullable = false)
    private Prevision prevision;

    @Column(name = "starting_point", nullable = false, length = 255)
    private String startingPoint;

    @Column(name = "destination_point", nullable = false, length = 255)
    private String destinationPoint;

    @Column(name = "km_route", nullable = false)
    private Double kmRoute;

    @Column(name = "route_cost", nullable = false)
    private BigDecimal routeCost;

    @Column(name = "km_urban", nullable = false)
    private Double kmUrban;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    public Route() {
    }

    public Route(Long id, Prevision prevision, String startingPoint, String destinationPoint,
                       Double kmRoute, BigDecimal routeCost, Double kmUrban, LocalDateTime creationDate) {
        this.id = id;
        this.prevision = prevision;
        this.startingPoint = startingPoint;
        this.destinationPoint = destinationPoint;
        this.kmRoute = kmRoute;
        this.routeCost = routeCost;
        this.kmUrban = kmUrban;
        this.creationDate = creationDate;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Prevision getPrevision() {
        return prevision;
    }

    public void setPrevision(Prevision prevision) {
        this.prevision = prevision;
    }

    public String getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(String startingPoint) {
        this.startingPoint = startingPoint;
    }

    public String getDestinationPoint() {
        return destinationPoint;
    }

    public void setDestinationPoint(String destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public Double getKmRoute() {
        return kmRoute;
    }

    public void setKmRoute(Double kmRoute) {
        this.kmRoute = kmRoute;
    }

    public BigDecimal getRouteCost() {
        return routeCost;
    }

    public void setRouteCost(BigDecimal routeCost) {
        this.routeCost = routeCost;
        if (this.prevision != null) {
            this.prevision.updateTotal();
        }
    }

    public Double getKmUrban() {
        return kmUrban;
    }

    public void setKmUrban(Double kmUrban) {
        this.kmUrban = kmUrban;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
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
        Route other = (Route) obj;
        return Objects.equals(id, other.id);
    }
}
