package com.cruzze.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "drivers")
public class Drivers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "clerk_driver_id", nullable = false, unique = true)
    private String clerkDriverId;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "Date_Of_Birth", nullable = false)
    private String dateOfBirth;
    
    @Column(name = "gender", nullable = false)
    private String gender;
    
    @Column(name = "address", nullable = false)
    private String address;
    
    @Column(name = "emergency_Contact_Name", nullable = false)
    private String emergency_Contact_Name;
    
    @Column(name = "emergency_Contact_Number", nullable = false)
    private String emergency_Contact_Number;
    
    @Column(name = "registration_Date", nullable = false)
    private String registration_Date;
    
    @Lob
    @Column(name = "profile_image", columnDefinition = "BYTEA")
    private byte[] profileImage;

    @Lob
    @Column(name = "license_image", columnDefinition = "BYTEA")
    private byte[] licenseImage;

    
    
    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(name = "license_expiry_date", nullable = false)
    private LocalDate licenseExpiryDate;

    @Column(name = "aadhar_number", nullable = false, unique = true)
    private String aadharNumber;

    @Column(name = "pan_number", nullable = false, unique = true)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "driver_status", nullable = false)
    private DriverStatus driverStatus = DriverStatus.PENDING;

    @Column(name = "is_online")
    private Boolean isOnline = false;

    @Column(precision = 3, scale = 2)
    private BigDecimal rating = BigDecimal.valueOf(0.00);

    @Column(name = "total_rides")
    private Integer totalRides = 0;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    @Column(name = "ifsc_code")
    private String ifscCode;

    @Column(name = "account_holder_name")
    private String accountHolderName;

    @Column(name = "current_latitude", precision = 10, scale = 8)
    private BigDecimal currentLatitude;

    @Column(name = "current_longitude", precision = 11, scale = 8)
    private BigDecimal currentLongitude;

    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;

    @OneToOne(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JsonManagedReference
    private Vehicles vehicle;

    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum DriverStatus {
        PENDING, APPROVED, REJECTED, SUSPENDED
    }

    // Getters and Setters
    // (Include all fields, including vehicle)
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public LocalDate getLicenseExpiryDate() {
        return licenseExpiryDate;
    }

    public void setLicenseExpiryDate(LocalDate licenseExpiryDate) {
        this.licenseExpiryDate = licenseExpiryDate;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    public String getPanNumber() {
        return panNumber;
    }

    public void setPanNumber(String panNumber) {
        this.panNumber = panNumber;
    }

    public DriverStatus getDriverStatus() {
        return driverStatus;
    }

    public void setDriverStatus(DriverStatus driverStatus) {
        this.driverStatus = driverStatus;
    }

    public Boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(Boolean isOnline) {
        this.isOnline = isOnline;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public Integer getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(Integer totalRides) {
        this.totalRides = totalRides;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public BigDecimal getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(BigDecimal currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public BigDecimal getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(BigDecimal currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public LocalDateTime getLastLocationUpdate() {
        return lastLocationUpdate;
    }

    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) {
        this.lastLocationUpdate = lastLocationUpdate;
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

	public Vehicles getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicles vehicle) {
		this.vehicle = vehicle;
	}
	
	public String getFirstName() {
	    return firstName;
	}

	public void setFirstName(String firstName) {
	    this.firstName = firstName;
	}

	public String getLastName() {
	    return lastName;
	}

	public void setLastName(String lastName) {
	    this.lastName = lastName;
	}

	public String getPhoneNumber() {
	    return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
	    this.phoneNumber = phoneNumber;
	}

	public byte[] getProfileImage() {
	    return profileImage;
	}

	public void setProfileImage(byte[] profileImage) {
	    this.profileImage = profileImage;
	}

	public byte[] getLicenseImage() {
	    return licenseImage;
	}

	public void setLicenseImage(byte[] licenseImage) {
	    this.licenseImage = licenseImage;
	}

	public String getClerkDriverId() {
		return clerkDriverId;
	}

	public void setClerkDriverId(String clerkDriverId) {
		this.clerkDriverId = clerkDriverId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmergency_Contact_Name() {
		return emergency_Contact_Name;
	}

	public void setEmergency_Contact_Name(String emergency_Contact_Name) {
		this.emergency_Contact_Name = emergency_Contact_Name;
	}

	public String getEmergency_Contact_Number() {
		return emergency_Contact_Number;
	}

	public void setEmergency_Contact_Number(String emergency_Contact_Number) {
		this.emergency_Contact_Number = emergency_Contact_Number;
	}

	public String getRegistration_Date() {
		return registration_Date;
	}

	public void setRegistration_Date(String registration_Date) {
		this.registration_Date = registration_Date;
	}
	
	

    
}