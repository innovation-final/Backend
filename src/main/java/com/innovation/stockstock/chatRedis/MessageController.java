package com.innovation.stockstock.chatRedis;

import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.security.UserDetailsImpl;
import com.innovation.stockstock.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final JwtProvider jwtProvider;

    @MessageMapping("/chat") // 메시지 전송
    public void message(ChatMessage chatMessage,@Header("Authorization") String token) { // 이렇게 받을 수 있는 지.
        Authentication authentication = jwtProvider.getAuthentication(token.substring(7));
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        messageService.sendChat(chatMessage,userDetails);
    }
}