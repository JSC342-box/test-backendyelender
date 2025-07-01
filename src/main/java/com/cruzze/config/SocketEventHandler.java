package com.cruzze.config;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.cruzze.service.SocketIOServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;

@Component
public class SocketEventHandler {

    @Autowired
    private SocketIOServiceImpl socketIOService;

    @OnConnect
    public void onConnect(SocketIOClient client) {
        String userType = client.getHandshakeData().getSingleUrlParam("type");
        Long id = Long.valueOf(client.getHandshakeData().getSingleUrlParam("id"));
        System.out.println("🟢 Client connected: ID=" + id + ", type=" + userType);

        if ("driver".equalsIgnoreCase(userType)) {
            socketIOService.registerDriverClient(id, client);
        } else if ("user".equalsIgnoreCase(userType)) {
            socketIOService.registerUserClient(id, client);
        }
    }

  

    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        // Optional: Remove clients on disconnect if needed
    }
}
