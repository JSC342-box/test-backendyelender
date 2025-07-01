package com.cruzze.controller;


import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.MediaType; 

import com.cruzze.entity.Drivers;
import com.cruzze.entity.Users;
import com.cruzze.service.DriverService;
import com.cruzze.util.ResponseStructure;

@RestController
@RequestMapping("/drivers")
public class DriverController {

    @Autowired
    private DriverService driverService;

    @PostMapping(value = "/createDrivers", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseStructure<Drivers>> createDriver(
            @RequestPart("driver") @RequestBody Drivers driver,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @RequestPart(value = "licenseImage", required = false) MultipartFile licenseImage) {

        try {
            if (profileImage != null && !profileImage.isEmpty()) {
                driver.setProfileImage(profileImage.getBytes());
            }

            if (licenseImage != null && !licenseImage.isEmpty()) {
                driver.setLicenseImage(licenseImage.getBytes());
            }

            ResponseStructure<Drivers> response = driverService.createDriver(driver);
            return ResponseEntity.status(response.getStatus()).body(response);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error processing image upload", e);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseStructure<Drivers>> getDriverById(@PathVariable Long id) {
        ResponseStructure<Drivers> response = driverService.getDriverById(id);
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    
  
    @GetMapping("get/{id}")
    public ResponseEntity<Drivers> getDriverEntityById(@PathVariable Long id) {
        Drivers driver = driverService.getDriverEntityById(id);
        if (driver != null) {
            return ResponseEntity.ok(driver);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/getUserByClerkUserId/{clerkUserId}")
    public ResponseEntity<ResponseStructure<Drivers>> getDriversByClerkId(@PathVariable String clerkDriverId) {
        ResponseStructure<Drivers> response = driverService.getDriverByClerkId(clerkDriverId);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    
    @PutMapping("/update-location/{driverId}")
    public ResponseEntity<ResponseStructure<Drivers>> updateLocation(
            @PathVariable String driverId,
            @RequestBody Map<String, Object> body) {

        BigDecimal lat = new BigDecimal(body.get("latitude").toString());
        BigDecimal lng = new BigDecimal(body.get("longitude").toString());
        Boolean isOnline = Boolean.parseBoolean(body.get("isOnline").toString());

        ResponseStructure<Drivers> response = driverService.updateDriverLocation(driverId, lat, lng, isOnline);
        return ResponseEntity.ok(response);
    }



    
}
