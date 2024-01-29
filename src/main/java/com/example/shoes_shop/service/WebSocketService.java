package com.example.shoes_shop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketService {

    private final SimpMessagingTemplate simpMessagingTemplate;


    public <T> void send(String user, String destination, T message) {
        this.simpMessagingTemplate.convertAndSendToUser(user, destination, message);
    }

    public <T> void broadcast(String destination, T message) {
        this.simpMessagingTemplate.convertAndSend(destination, message);
    }
}
