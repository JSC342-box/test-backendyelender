//package com.cruzze.service;
//
//import com.cruzze.entity.Rides;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class SocketIOServiceImpl implements SocketIOService {
//
//    // 🌐 Your Railway deployed Node.js REST endpoint
//    private static final String SOCKET_REST_URL = "https://testsocketio-roqet.up.railway.app";
//
//    private final RestTemplate restTemplate = new RestTemplate();
//
//    @Override
//    public void sendRideRequest(String driverId, Rides ride) {
//        emitEvent("ride_request", "driver:" + driverId, ride);
//    }
//
//    @Override
//    public void sendRideAccepted(String userId, Rides updatedRide) {
//        emitEvent("ride_accepted", "user:" + userId, updatedRide);
//    }
//
//    @Override
//    public void sendRideCompleted(String userId, Rides ride) {
//        emitEvent("ride_completed", "user:" + userId, ride);
//    }
//
//    @Override
//    public void sendRideCancelled(String cancelledBy, Rides ride) {
//        String room;
//        if ("rider".equalsIgnoreCase(cancelledBy)) {
//            room = "driver:" + ride.getClerkDriverId(); // notify driver
//        } else if ("driver".equalsIgnoreCase(cancelledBy)) {
//            room = "user:" + ride.getUser().getClerkUserId(); // notify user
//        } else {
//            room = "user:" + ride.getUser().getClerkUserId(); // default notify user
//        }
//        emitEvent("ride_cancelled", room, ride);
//    }
//
//    /**
//     * Internal helper to POST an event to the Node.js server
//     */
//    private void emitEvent(String type, String room, Object payload) {
//        Map<String, Object> body = new HashMap<>();
//        body.put("type", type);
//        body.put("room", room);
//        body.put("payload", payload);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
//
//        ResponseEntity<String> response =
//                restTemplate.postForEntity(SOCKET_REST_URL, request, String.class);
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new RuntimeException("Failed to emit event: " + response.getBody());
//        } else {
//            System.out.printf("✅ Event [%s] sent to room [%s]%n", type, room);
//        }
//    }
//}



















package com.cruzze.service;

import com.cruzze.entity.Rides;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.HashMap;
import java.util.Map;

@Service
public class SocketIOServiceImpl implements SocketIOService {

    // 🌐 Your Railway deployed Node.js REST endpoint
    private static final String SOCKET_REST_URL = "https://testsocketio-roqet.up.railway.app";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendRideRequest(String driverId, Rides ride) {
        try {
            emitEvent("ride_request", "driver:" + driverId, ride);
        } catch (Exception e) {
            System.err.println("❌ Failed to send ride request to driver " + driverId + ": " + e.getMessage());
            // Don't throw exception - just log the error
        }
    }

    @Override
    public void sendRideAccepted(String userId, Rides updatedRide) {
        try {
            emitEvent("ride_accepted", "user:" + userId, updatedRide);
        } catch (Exception e) {
            System.err.println("❌ Failed to send ride accepted to user " + userId + ": " + e.getMessage());
            // Don't throw exception - just log the error
        }
    }

    @Override
    public void sendRideCompleted(String userId, Rides ride) {
        try {
            emitEvent("ride_completed", "user:" + userId, ride);
        } catch (Exception e) {
            System.err.println("❌ Failed to send ride completed to user " + userId + ": " + e.getMessage());
            // Don't throw exception - just log the error
        }
    }

    @Override
    public void sendRideCancelled(String cancelledBy, Rides ride) {
        try {
            String room;
            if ("rider".equalsIgnoreCase(cancelledBy)) {
                room = "driver:" + ride.getClerkDriverId(); // notify driver
            } else if ("driver".equalsIgnoreCase(cancelledBy)) {
                room = "user:" + ride.getUser().getClerkUserId(); // notify user
            } else {
                room = "user:" + ride.getUser().getClerkUserId(); // default notify user
            }
            emitEvent("ride_cancelled", room, ride);
        } catch (Exception e) {
            System.err.println("❌ Failed to send ride cancelled notification: " + e.getMessage());
            // Don't throw exception - just log the error
        }
    }

    /**
     * Internal helper to POST an event to the Node.js server
     */
    private void emitEvent(String type, String room, Object payload) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("type", type);
            body.put("room", room);
            body.put("payload", payload);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(SOCKET_REST_URL, request, String.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.err.println("⚠ Socket.IO server returned non-2xx status: " + response.getStatusCode() + " - " + response.getBody());
                // Don't throw exception - just log the warning
            } else {
                System.out.printf("✅ Event [%s] sent to room [%s] successfully%n", type, room);
            }
        } catch (RestClientException e) {
            System.err.println("❌ Failed to connect to Socket.IO server: " + e.getMessage());
            // Don't throw exception - just log the error
        } catch (Exception e) {
            System.err.println("❌ Unexpected error in emitEvent: " + e.getMessage());
            // Don't throw exception - just log the error
        }
    }
}

