package com.innovation.stockstock.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chat")
    public void message(ChatMessage chatMessage) throws Exception{
        simpMessageSendingOperations.convertAndSend("/sub/chat", chatMessage);
    }
}
