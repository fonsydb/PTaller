package com.sistemasdistr.basico.controller;

import com.sistemasdistr.basico.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    // Página de chat simple (sin layout)
    @GetMapping("/chat-simple")
    public String chatSimple() {
        return "chat-simple";
    }

    // Endpoint WebSocket
    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        System.out.println("[MENSAJE]: " + message.getSender() + " -> " + message.getContent());
        return message;
    }
}