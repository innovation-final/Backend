package com.innovation.stockstock.chatRedis;

import com.innovation.stockstock.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @MessageMapping("/chat") // 메시지 전송
    public void message(ChatMessage chatMessage) {
        messageService.sendChat(chatMessage);
    }
}