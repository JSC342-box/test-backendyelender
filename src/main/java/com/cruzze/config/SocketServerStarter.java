package com.cruzze.config;


import com.corundumstudio.socketio.SocketIOServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class SocketServerStarter {

    @Autowired
    private SocketIOServer server;

    @Autowired
    private SocketEventHandler socketEventHandler; // 👈 inject listener

    @EventListener
    public void start(ContextRefreshedEvent event) {
        // ✅ Register listener class explicitly
        server.addListeners(socketEventHandler);  // ✅ this line is missing in your current code!

        server.start();
        System.out.println("✅ Socket.IO server started on port 9092");
    }
}

