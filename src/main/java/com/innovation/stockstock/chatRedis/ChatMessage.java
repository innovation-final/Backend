package com.innovation.stockstock.chatRedis;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class ChatMessage {
    
    private MessageType type;
    private String sendTime;
    private String nickName; // sender
    private Long userId; // sender
    private String imageUrl; // sender
    private String message;
    }
