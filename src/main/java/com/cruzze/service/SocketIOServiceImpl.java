
package com.cruzze.service;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.cruzze.entity.Rides;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SocketIOServiceImpl implements SocketIOService {

    @Autowired
    private SocketIOServer socketServer;
    
    

    // Maintain connected clients by their user/driver ID
    private final Map<Long, SocketIOClient> driverClients = new ConcurrentHashMap<>();
    private final Map<Long, SocketIOClient> userClients = new ConcurrentHashMap<>();

    public void registerDriverClient(Long driverId, SocketIOClient client) {
        driverClients.put(driverId, client);
    }

    public void registerUserClient(Long userId, SocketIOClient client) {
        userClients.put(userId, client);
    }

    @Override
    public void sendRideRequest(String driverClerkId, Rides ride) {
        SocketIOClient client = driverClients.get(driverClerkId);
        if (client != null) {
            client.sendEvent("ride_request", ride);
        }
    }

    @Override
    public void sendRideAccepted(String clerkUserId, Rides updatedRide) {
        SocketIOClient client = userClients.get(clerkUserId);
        if (client != null) {
            client.sendEvent("ride_accepted", updatedRide);
        }
    }
    
    public SocketIOClient getUserClient(Long userId) {
        return userClients.get(userId);  // Assuming userClients map exists
    }

    
    
}