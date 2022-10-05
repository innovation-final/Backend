package com.innovation.stockstock.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class MessageController {
    private final SimpMessageSendingOperations simpMessageSendingOperations;

    @MessageMapping("/chat")
    public void message(ChatMessage chatMessage) throws Exception{
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            simpMessageSendingOperations.convertAndSend("/sub/chat", chatMessage);
        } else if(ChatMessage.MessageType.TALK.equals(chatMessage.getType())) {
            simpMessageSendingOperations.convertAndSend("/sub/chat", chatMessage);
        }
    }
}
