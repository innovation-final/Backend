package com.innovation.stockstock.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatMessage {

    public enum MessageType { //메세지 타입 /참여, 채팅
        ENTER, TALK, ALARM
    }
    private MessageType type;
    private String sendTime;
    private String nickName;
    private Long userId;
    private String imageUrl;
    private String message;

}
