//package com.cruzze.controller;
//
//import com.cruzze.entity.Drivers;
//import com.cruzze.entity.Rides;
//import com.cruzze.entity.Users;
//import com.cruzze.service.DriverService;
//import com.cruzze.service.RidesService;
//import com.cruzze.service.UsersService;
//import com.cruzze.util.ResponseStructure;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.math.BigDecimal;
//import java.util.Map;
//import java.util.Optional;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//
//@RestController
//@RequestMapping("/rides")
//public class RidesController {
//	
//	private static final Logger log = LoggerFactory.getLogger(RidesController.class);
//
//
//    @Autowired
//    private RidesService ridesService;
//
//    @Autowired
//    private UsersService userService;
//
//    @Autowired
//    private DriverService driverService;
//
//    @PostMapping("/rideRequest")
//    public ResponseEntity<?> requestRide(@RequestBody Map<String, Object> body) {
//        String clerkUserId = body.get("clerkUserId").toString();;
//        BigDecimal pickupLat = new BigDecimal(body.get("pickupLatitude").toString());
//        BigDecimal pickupLng = new BigDecimal(body.get("pickupLongitude").toString());
//        BigDecimal dropLat = new BigDecimal(body.get("dropLatitude").toString());
//        BigDecimal dropLng = new BigDecimal(body.get("dropLongitude").toString());
//        String notes = (String) body.getOrDefault("notes", "");
//
//        Rides.VehicleType vehicleType = null;
//        if (body.containsKey("vehicleType")) {
//            vehicleType = Rides.VehicleType.valueOf(body.get("vehicleType").toString());
//        }
//
//        Users user = userService.getUserByClerkId(clerkUserId).getData();
//        Rides ride = ridesService.requestRide(user, pickupLat, pickupLng, dropLat, dropLng, vehicleType, notes);
//        return ResponseEntity.ok(ride);
//    }
//
//    
//    @PostMapping("/accept")
//    public ResponseEntity<?> acceptRide(@RequestBody Map<String,Object>body) {
//    			try {
//    				Long rideId=Long.valueOf(body.get("rideId").toString());
//    		    	  String clerkDriverId = body.get("clerkDriverId").toString();;
//            log.info("🔁 Accept request received: rideId={}, clerkDriverId={}", rideId, clerkDriverId);
//
//            Optional<Rides> updatedRide = ridesService.assignDriver(rideId, clerkDriverId);
//
//            if (updatedRide.isPresent()) {
//                log.info("✔️ Ride assigned successfully: {}", updatedRide.get().getId());
//                return ResponseEntity.ok(updatedRide.get());
//            } else {
//                log.warn("❌ assignDriver returned empty Optional for rideId={}", rideId);
//                return ResponseEntity.badRequest().body("Invalid Ride ID or already assigned");
//            }
//
//        } catch (Exception ex) {
//            log.error("⚠️ Unexpected error while assigning ride", ex);
//            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
//        }
//    }
//   
//
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getRide(@PathVariable Long id) {
//        Optional<Rides> ride = ridesService.getRide(id);
//        return ride.map(ResponseEntity::ok)
//                   .orElseGet(() -> ResponseEntity.notFound().build());
//    }
//    
//    @PostMapping("/complete")
//    public ResponseEntity<Rides> completeRide(@RequestParam Long rideId) {
//        Optional<Rides> ride = ridesService.completeRide(rideId);
//        if (ride.isPresent()) {
//            return ResponseEntity.ok(ride.get());
//        } else {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Ride ID");
//        }
//    }
//    
//    @PostMapping("/cancel")
//    public ResponseEntity<?> cancelRide(@RequestParam Long rideId,
//                                         @RequestParam(required = false) String cancelledBy) {
//        try {
//            Optional<Rides> optionalRide = ridesService.cancelRide(rideId, cancelledBy != null ? cancelledBy : "UNKNOWN");
//            if (optionalRide.isPresent()) {
//                return ResponseEntity.ok(optionalRide.get());
//            } else {
//                return ResponseEntity.badRequest().body("Invalid Ride ID or ride cannot be cancelled");
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
//        }
//    }
//
//
//}






























package com.cruzze.controller;

import com.cruzze.entity.Drivers;
import com.cruzze.entity.Rides;
import com.cruzze.entity.Users;
import com.cruzze.service.DriverService;
import com.cruzze.service.RidesService;
import com.cruzze.service.UsersService;
import com.cruzze.util.ResponseStructure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;


@RestController
@RequestMapping("/rides")
public class RidesController {
	
	private static final Logger log = LoggerFactory.getLogger(RidesController.class);


    @Autowired
    private RidesService ridesService;

    @Autowired
    private UsersService userService;

    @Autowired
    private DriverService driverService;

    // Test endpoint to debug authentication
    @GetMapping("/test-auth")
    public ResponseEntity<?> testAuth() {
        try {
            // Get authentication from security context
            String clerkUserId = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("✅ Test auth endpoint - Clerk User ID: {}", clerkUserId);
            
            return ResponseEntity.ok(Map.of(
                "message", "Authentication successful",
                "clerkUserId", clerkUserId,
                "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            log.error("❌ Test auth endpoint failed", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Authentication test failed",
                "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/rideRequest")
    public ResponseEntity<?> requestRide(@RequestBody Map<String, Object> body) {
        try {
            log.info("🚗 Ride request received - Body: {}", body);
            
            // Validate required fields
            if (!body.containsKey("clerkUserId") || !body.containsKey("pickupLatitude") || 
                !body.containsKey("pickupLongitude") || !body.containsKey("dropLatitude") || 
                !body.containsKey("dropLongitude")) {
                log.error("❌ Missing required fields in ride request");
                return ResponseEntity.badRequest().body(Map.of("error", "Missing required fields"));
            }
            
            String clerkUserId = body.get("clerkUserId").toString();
            BigDecimal pickupLat = new BigDecimal(body.get("pickupLatitude").toString());
            BigDecimal pickupLng = new BigDecimal(body.get("pickupLongitude").toString());
            BigDecimal dropLat = new BigDecimal(body.get("dropLatitude").toString());
            BigDecimal dropLng = new BigDecimal(body.get("dropLongitude").toString());
            String notes = (String) body.getOrDefault("notes", "");

            Rides.VehicleType vehicleType = null;
            
            if (body.containsKey("vehicleType")) {
                try {
                    vehicleType = Rides.VehicleType.valueOf(body.get("vehicleType").toString());
                } catch (IllegalArgumentException e) {
                    log.error("❌ Invalid vehicle type: {}", body.get("vehicleType"));
                    return ResponseEntity.badRequest().body(Map.of("error", "Invalid vehicle type"));
                }
            }

            log.info("🚗 Processing ride request for user: {}", clerkUserId);
            
            Users user = userService.getUserByClerkId(clerkUserId).getData();
            if (user == null) {
                log.error("❌ User not found for clerkUserId: {}", clerkUserId);
                return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
            }
            
            log.info("🚗 Creating ride for user: {}", user.getClerkUserId());
            
            Rides ride = ridesService.requestRide(user, pickupLat, pickupLng, dropLat, dropLng, vehicleType, notes);
            
            log.info("✅ Ride request successful - Ride ID: {}", ride.getId());
            return ResponseEntity.ok(ride);
            
        } catch (NumberFormatException e) {
            log.error("❌ Invalid number format in ride request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid number format in coordinates"));
        } catch (Exception e) {
            log.error("❌ Ride request failed", e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Ride request failed", 
                "message", e.getMessage(),
                "details", "Please try again or contact support"
            ));
        }
    }

    
    @PostMapping("/accept")
    public ResponseEntity<?> acceptRide(@RequestBody Map<String,Object>body) {
    			try {
    				Long rideId=Long.valueOf(body.get("rideId").toString());
    		    	  String clerkDriverId = body.get("clerkDriverId").toString();;
            log.info("🔁 Accept request received: rideId={}, clerkDriverId={}", rideId, clerkDriverId);

            Optional<Rides> updatedRide = ridesService.assignDriver(rideId, clerkDriverId);

            if (updatedRide.isPresent()) {
                log.info("✔ Ride assigned successfully: {}", updatedRide.get().getId());
                return ResponseEntity.ok(updatedRide.get());
            } else {
                log.warn("❌ assignDriver returned empty Optional for rideId={}", rideId);
                return ResponseEntity.badRequest().body("Invalid Ride ID or already assigned");
            }

        } catch (Exception ex) {
            log.error("⚠ Unexpected error while assigning ride", ex);
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
   
    
//    @PostMapping("/accept")
//    public ResponseEntity<?> acceptRide(@RequestParam Long rideId,
//                                         @RequestParam String clerkDriverId) {
//        try {
//            log.info("🔁 Accept request received: rideId={}, clerkDriverId={}", rideId, clerkDriverId);
//
//            Optional<Rides> updatedRide = ridesService.assignDriver(rideId, clerkDriverId);
//
//            if (updatedRide.isPresent()) {
//                log.info("✔ Ride assigned successfully: {}", updatedRide.get().getId());
//                return ResponseEntity.ok(updatedRide.get());
//            } else {
//                log.warn("❌ assignDriver returned empty Optional for rideId={}", rideId);
//                return ResponseEntity.badRequest().body("Invalid Ride ID or already assigned");
//            }
//
//        } catch (Exception ex) {
//            log.error("⚠ Unexpected error while assigning ride", ex);
//            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
//        }
//    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getRide(@PathVariable Long id) {
        Optional<Rides> ride = ridesService.getRide(id);
        return ride.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    @PostMapping("/complete")
    public ResponseEntity<Rides> completeRide(@RequestParam Long rideId) {
        Optional<Rides> ride = ridesService.completeRide(rideId);
        if (ride.isPresent()) {
            return ResponseEntity.ok(ride.get());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid Ride ID");
        }
    }
    
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelRide(@RequestParam Long rideId,
                                         @RequestParam(required = false) String cancelledBy) {
        try {
            Optional<Rides> optionalRide = ridesService.cancelRide(rideId, cancelledBy != null ? cancelledBy : "UNKNOWN");
            if (optionalRide.isPresent()) {
                return ResponseEntity.ok(optionalRide.get());
            } else {
                return ResponseEntity.badRequest().body("Invalid Ride ID or ride cannot be cancelled");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "Internal Server Error"));
        }
    }


}
