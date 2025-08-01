//package com.cruzze.service;
//
//import com.cruzze.dao.RidesDao;
//import com.cruzze.entity.*;
//import com.cruzze.repository.DriverRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@Service
//public class RidesService {
//
//    private static final double SEARCH_RADIUS_KM = 5.0; // Range within which drivers will receive requests
//
//    @Autowired
//    private RidesDao ridesDao;
//
//    @Autowired
//    private DriverRepository driverRepository;
//
//    @Autowired
//    private SocketIOService socketIOService;
//    
//    @Autowired
//    private RideTrackingService rideTrackingService;
//
//
//    public Rides requestRide(Users user, BigDecimal pickupLat, BigDecimal pickupLng,
//            BigDecimal dropLat, BigDecimal dropLng,
//            Rides.VehicleType vehicleType, String notes) {
//
//            Rides ride = new Rides();
//            ride.setUser(user);
//            ride.setPickupLatitude(pickupLat);
//            ride.setPickupLongitude(pickupLng);
//            ride.setDropLatitude(dropLat);
//            ride.setDropLongitude(dropLng);
//            ride.setVehicleType(vehicleType);
//            ride.setNotes(notes);
//            ride.setStatus(Rides.RideStatus.PENDING);
//
//// 👇 Calculate distance
//BigDecimal distanceKm = BigDecimal.valueOf(
//haversine(pickupLat.doubleValue(), pickupLng.doubleValue(),
//     dropLat.doubleValue(), dropLng.doubleValue())
//);
//
//// 👇 Set pricing logic
//BigDecimal baseFare = BigDecimal.valueOf(25); // Base fare
//BigDecimal perKmRate = BigDecimal.valueOf(8); // ₹ per km
//BigDecimal totalFare = baseFare.add(perKmRate.multiply(distanceKm));
//
//ride.setFare(totalFare); // 👈 set fare in ride
//
//// Save ride
//Rides savedRide = ridesDao.save(ride);
//
//// Send to nearby drivers
//List<Drivers> nearbyDrivers = driverRepository.findAll().stream()
//.filter(driver -> driver.getIsOnline() != null && driver.getIsOnline()
//   && driver.getCurrentLatitude() != null && driver.getCurrentLongitude() != null
//   && haversine(pickupLat.doubleValue(), pickupLng.doubleValue(),
//                driver.getCurrentLatitude().doubleValue(), driver.getCurrentLongitude().doubleValue()) <= SEARCH_RADIUS_KM
//   && (vehicleType == null || driver.getVehicle().getVehicleType().name().equals(vehicleType.name()))
//)
//.collect(Collectors.toList());
//
//for (Drivers driver : nearbyDrivers) {
//socketIOService.sendRideRequest(driver.getClerkDriverId(), savedRide);
//}
//
//return savedRide;
//}
//
//
//    private double haversine(double lat1, double lon1, double lat2, double lon2) {
//        final int R = 6371; // Radius of Earth in km
//        double dLat = Math.toRadians(lat2 - lat1);
//        double dLon = Math.toRadians(lon2 - lon1);
//        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
//                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        return R * c;
//    }
//
//    
//    
//    public Optional<Rides> assignDriver(Long rideId, String clerkDriverId) {
//        Optional<Rides> optionalRide = ridesDao.findById(rideId);
//        if (optionalRide.isPresent()) {
//            Rides ride = optionalRide.get();
//
//            // ❌ Already accepted by another driver?
//            if (ride.getStatus() != Rides.RideStatus.PENDING) {
//                return Optional.empty(); 
//            }
//
//            // ✅ Assign driverClerkId
//            ride.setClerkDriverId(clerkDriverId);
//            ride.setStatus(Rides.RideStatus.ACCEPTED);
//            Rides updated = ridesDao.save(ride);
//
//            // ✅ Notify user
//            socketIOService.sendRideAccepted(ride.getUser().getClerkUserId(), updated);
//
//            return Optional.of(updated);
//        }
//        return Optional.empty();
//    }
//
//
//
//    public Optional<Rides> getRide(Long id) {
//        return ridesDao.findById(id);
//    }
//    
//    public Optional<Rides> completeRide(Long rideId) {
//        Optional<Rides> optionalRide = ridesDao.findById(rideId);
//        if (optionalRide.isPresent()) {
//            Rides ride = optionalRide.get();
//
//            // 1. Calculate actual distance
//            BigDecimal distanceKm = rideTrackingService.calculateActualDistanceKm(rideId);
//
//            // 2. Pricing logic
//            BigDecimal baseFare = BigDecimal.valueOf(25); // base fare
//            BigDecimal perKmRate = BigDecimal.valueOf(8); // ₹ per km
//            BigDecimal totalFare = baseFare.add(perKmRate.multiply(distanceKm));
//
//            // 3. Update ride with fare and status
//            ride.setFare(totalFare);
//            ride.setStatus(Rides.RideStatus.COMPLETED);
//            ride.setPaymentStatus(Rides.PaymentStatus.PAID); // or keep as PENDING
//
//            Rides updated = ridesDao.save(ride);
//
//            // 🔔 Notify user (or driver) that ride is completed
//            socketIOService.sendRideCompleted(ride.getUser().getClerkUserId(), updated);
//
//
//            return Optional.of(updated);
//        }
//        return Optional.empty();
//    }
//
//    public Optional<Rides> cancelRide(Long rideId, String cancelledBy) {
//        Optional<Rides> optionalRide = ridesDao.findById(rideId);
//        if (optionalRide.isPresent()) {
//            Rides ride = optionalRide.get();
//
//            // 🚫 cannot cancel if already completed or cancelled
//            if (ride.getStatus() == Rides.RideStatus.COMPLETED || 
//                ride.getStatus() == Rides.RideStatus.CANCELLED) {
//                return Optional.empty();
//            }
//
//            // optionally record who cancelled (add a column if desired)
//            // ride.setCancelledBy(cancelledBy);
//
//            ride.setStatus(Rides.RideStatus.CANCELLED);
//
//            Rides updatedRide = ridesDao.save(ride);
//
//            // 🔔 notify user/driver
//            socketIOService.sendRideCancelled(cancelledBy, updatedRide);
//
//
//            return Optional.of(updatedRide);
//        }
//        return Optional.empty();
//    }
//    
//
//}




















package com.cruzze.service;

import com.cruzze.dao.RidesDao;
import com.cruzze.entity.*;
import com.cruzze.repository.DriverRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RidesService {

    private static final double SEARCH_RADIUS_KM = 5.0; // Range within which drivers will receive requests

    @Autowired
    private RidesDao ridesDao;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private SocketIOService socketIOService;
    
    @Autowired
    private RideTrackingService rideTrackingService;


    public Rides requestRide(Users user, BigDecimal pickupLat, BigDecimal pickupLng,
            BigDecimal dropLat, BigDecimal dropLng,
            Rides.VehicleType vehicleType, String notes) {

        try {
            System.out.println("🚗 RidesService: Creating ride for user: " + user.getClerkUserId());
            
            Rides ride = new Rides();
            ride.setUser(user);
            ride.setPickupLatitude(pickupLat);
            ride.setPickupLongitude(pickupLng);
            ride.setDropLatitude(dropLat);
            ride.setDropLongitude(dropLng);
            ride.setVehicleType(vehicleType);
            ride.setNotes(notes);
            ride.setStatus(Rides.RideStatus.PENDING);

            // 👇 Calculate distance
            BigDecimal distanceKm = BigDecimal.valueOf(
                haversine(pickupLat.doubleValue(), pickupLng.doubleValue(),
                     dropLat.doubleValue(), dropLng.doubleValue())
            );

            // 👇 Set pricing logic
            BigDecimal baseFare = BigDecimal.valueOf(25); // Base fare
            BigDecimal perKmRate = BigDecimal.valueOf(8); // ₹ per km
            BigDecimal totalFare = baseFare.add(perKmRate.multiply(distanceKm));

            ride.setFare(totalFare); // 👈 set fare in ride

            System.out.println("🚗 RidesService: Saving ride to database...");
            
            // Save ride
            Rides savedRide = ridesDao.save(ride);
            
            System.out.println("✅ RidesService: Ride saved successfully with ID: " + savedRide.getId());

            // Send to nearby drivers (non-blocking)
            try {
                System.out.println("🔍 RidesService: Looking for nearby drivers...");
                
                List<Drivers> nearbyDrivers = driverRepository.findAll().stream()
                    .filter(driver -> {
                        try {
                            return driver.getIsOnline() != null && driver.getIsOnline()
                               && driver.getCurrentLatitude() != null && driver.getCurrentLongitude() != null
                               && haversine(pickupLat.doubleValue(), pickupLng.doubleValue(),
                                            driver.getCurrentLatitude().doubleValue(), driver.getCurrentLongitude().doubleValue()) <= SEARCH_RADIUS_KM
                               && (vehicleType == null || (driver.getVehicle() != null && driver.getVehicle().getVehicleType() != null && 
                                   driver.getVehicle().getVehicleType().name().equals(vehicleType.name())));
                        } catch (Exception e) {
                            System.err.println("⚠ Error filtering driver " + driver.getClerkDriverId() + ": " + e.getMessage());
                            return false;
                        }
                    })
                    .collect(Collectors.toList());

                System.out.println("🔍 RidesService: Found " + nearbyDrivers.size() + " nearby drivers");

                for (Drivers driver : nearbyDrivers) {
                    try {
                        socketIOService.sendRideRequest(driver.getClerkDriverId(), savedRide);
                        System.out.println("✅ RidesService: Ride request sent to driver: " + driver.getClerkDriverId());
                    } catch (Exception e) {
                        System.err.println("❌ RidesService: Failed to send ride request to driver " + driver.getClerkDriverId() + ": " + e.getMessage());
                        // Continue with other drivers
                    }
                }
            } catch (Exception e) {
                System.err.println("❌ RidesService: Error in driver notification: " + e.getMessage());
                // Don't fail the ride creation if driver notification fails
            }

            System.out.println("✅ RidesService: Ride request completed successfully");
            return savedRide;
            
        } catch (Exception e) {
            System.err.println("❌ RidesService: Error creating ride: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let controller handle it
        }
    }


    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of Earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    
    
    public Optional<Rides> assignDriver(Long rideId, String clerkDriverId) {
        Optional<Rides> optionalRide = ridesDao.findById(rideId);
        if (optionalRide.isPresent()) {
            Rides ride = optionalRide.get();

            // ❌ Already accepted by another driver?
            if (ride.getStatus() != Rides.RideStatus.PENDING) {
                return Optional.empty(); 
            }

            // ✅ Assign driverClerkId
            ride.setClerkDriverId(clerkDriverId);
            ride.setStatus(Rides.RideStatus.ACCEPTED);
            Rides updated = ridesDao.save(ride);

            // ✅ Notify user
            socketIOService.sendRideAccepted(ride.getUser().getClerkUserId(), updated);

            return Optional.of(updated);
        }
        return Optional.empty();
    }



    public Optional<Rides> getRide(Long id) {
        return ridesDao.findById(id);
    }
    
    public Optional<Rides> completeRide(Long rideId) {
        Optional<Rides> optionalRide = ridesDao.findById(rideId);
        if (optionalRide.isPresent()) {
            Rides ride = optionalRide.get();

            // 1. Calculate actual distance
            BigDecimal distanceKm = rideTrackingService.calculateActualDistanceKm(rideId);

            // 2. Pricing logic
            BigDecimal baseFare = BigDecimal.valueOf(25); // base fare
            BigDecimal perKmRate = BigDecimal.valueOf(8); // ₹ per km
            BigDecimal totalFare = baseFare.add(perKmRate.multiply(distanceKm));

            // 3. Update ride with fare and status
            ride.setFare(totalFare);
            ride.setStatus(Rides.RideStatus.COMPLETED);
            ride.setPaymentStatus(Rides.PaymentStatus.PAID); // or keep as PENDING

            Rides updated = ridesDao.save(ride);

            // 🔔 Notify user (or driver) that ride is completed
            socketIOService.sendRideCompleted(ride.getUser().getClerkUserId(), updated);


            return Optional.of(updated);
        }
        return Optional.empty();
    }

    public Optional<Rides> cancelRide(Long rideId, String cancelledBy) {
        Optional<Rides> optionalRide = ridesDao.findById(rideId);
        if (optionalRide.isPresent()) {
            Rides ride = optionalRide.get();

            // 🚫 cannot cancel if already completed or cancelled
            if (ride.getStatus() == Rides.RideStatus.COMPLETED || 
                ride.getStatus() == Rides.RideStatus.CANCELLED) {
                return Optional.empty();
            }

            // optionally record who cancelled (add a column if desired)
            // ride.setCancelledBy(cancelledBy);

            ride.setStatus(Rides.RideStatus.CANCELLED);

            Rides updatedRide = ridesDao.save(ride);

            // 🔔 notify user/driver
            socketIOService.sendRideCancelled(cancelledBy, updatedRide);


            return Optional.of(updatedRide);
        }
        return Optional.empty();
    }
    

}