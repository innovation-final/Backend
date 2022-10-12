package com.innovation.stockstock.chatRedis;

import com.innovation.stockstock.member.domain.Member;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class ChatMessage {
    
    private MessageType type;
    private String sendTime;
//    private Member member;
    private String nickName; // sender
    private Long userId; // sender
    private String imageUrl; // sender
    private String message;

}
