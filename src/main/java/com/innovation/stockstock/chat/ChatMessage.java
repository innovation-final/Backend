package com.innovation.stockstock.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ChatMessage {

    private String type;
    private String sender;

//    private String channelId;

    private String message;

    public void setSender(String sender) {this.sender = sender;}
    public void newConnect(){this.type = "new";}
    public void closeConnect(){this.type = "close";}

}
