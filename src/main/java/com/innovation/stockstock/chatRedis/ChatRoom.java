package com.innovation.stockstock.chatRedis;

import lombok.*;
import org.springframework.stereotype.Component;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Component
@Entity
public class ChatRoom {
    @Id
    @GeneratedValue
    Long id;
    String name;
    Long userCnt;

    public void updateNum(Boolean isAdded) {
        if (isAdded) {
            this.userCnt = userCnt + 1;
        } else {
            if (this.userCnt <= 1) {
                this.userCnt = 0L;
            } else {
                this.userCnt = userCnt - 1;
            }
        }
    }
}