
package com.cruzze.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class SocketIOServiceImpl implements SocketIOService {

    private final String SOCKET_SERVER_URL = "https://roqet-socket.up.railway.app/emit";

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void sendRideRequest(String driverId, Rides ride) {
        emitEvent("ride_request", "driver:" + driverId, ride);
    }

    @Override
    public void sendRideAccepted(String userId, Rides updatedRide) {
        emitEvent("ride_accepted", "user:" + userId, updatedRide);
    }

    private void emitEvent(String type, String room, Object payload) {
        Map<String, Object> body = new HashMap<>();
        body.put("type", type);
        body.put("room", room);
        body.put("payload", payload);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(SOCKET_SERVER_URL, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to emit event: " + response.getBody());
        }
    }
}
