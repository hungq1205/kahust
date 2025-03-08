package com.hungq.kahust.gateway.websocket;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.hungq.kahust.gateway.model.WebSocketMessage;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class WebSocketController {
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/{roomId}")
    @SendTo("/room/{roomId}")
    public WebSocketMessage handleMessage(WebSocketMessage message) {
        return message;
    }

    public void sendMessageToRoom(String roomId, WebSocketMessage message) {
        messagingTemplate.convertAndSend("/room/" + roomId, message);
    }
}
