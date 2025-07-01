package com.cruzze.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"vehicle_type", "city"})
})
public class Pricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "base_fare", precision = 10, scale = 2, nullable = false)
    private BigDecimal baseFare;

    @Column(name = "per_km_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal perKmRate;

    @Column(name = "per_minute_rate", precision = 10, scale = 2, nullable = false)
    private BigDecimal perMinuteRate;

    @Column(name = "minimum_fare", precision = 10, scale = 2, nullable = false)
    private BigDecimal minimumFare;

    @Column(name = "maximum_fare", precision = 10, scale = 2)
    private BigDecimal maximumFare;

    @Column(name = "surge_multiplier", precision = 3, scale = 2)
    private BigDecimal surgeMultiplier = BigDecimal.valueOf(1.00);

    @Column(name = "night_charges_multiplier", precision = 3, scale = 2)
    private BigDecimal nightChargesMultiplier = BigDecimal.valueOf(1.00);

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum VehicleType {
        BIKE, AUTO, CAB, PARCEL
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public VehicleType getVehicleType() {
		return vehicleType;
	}

	public void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public BigDecimal getBaseFare() {
		return baseFare;
	}

	public void setBaseFare(BigDecimal baseFare) {
		this.baseFare = baseFare;
	}

	public BigDecimal getPerKmRate() {
		return perKmRate;
	}

	public void setPerKmRate(BigDecimal perKmRate) {
		this.perKmRate = perKmRate;
	}

	public BigDecimal getPerMinuteRate() {
		return perMinuteRate;
	}

	public void setPerMinuteRate(BigDecimal perMinuteRate) {
		this.perMinuteRate = perMinuteRate;
	}

	public BigDecimal getMinimumFare() {
		return minimumFare;
	}

	public void setMinimumFare(BigDecimal minimumFare) {
		this.minimumFare = minimumFare;
	}

	public BigDecimal getMaximumFare() {
		return maximumFare;
	}

	public void setMaximumFare(BigDecimal maximumFare) {
		this.maximumFare = maximumFare;
	}

	public BigDecimal getSurgeMultiplier() {
		return surgeMultiplier;
	}

	public void setSurgeMultiplier(BigDecimal surgeMultiplier) {
		this.surgeMultiplier = surgeMultiplier;
	}

	public BigDecimal getNightChargesMultiplier() {
		return nightChargesMultiplier;
	}

	public void setNightChargesMultiplier(BigDecimal nightChargesMultiplier) {
		this.nightChargesMultiplier = nightChargesMultiplier;
	}

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

    
}
