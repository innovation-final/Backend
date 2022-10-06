package com.innovation.stockstock.notification.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.innovation.stockstock.notification.domain.Event;
import com.innovation.stockstock.notification.domain.Notification;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class NotificationResponseDto {

    private Event type;
    private String message;
    private boolean isRead;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public NotificationResponseDto(Notification notification) {
        this.type = notification.getType();
        this.message = notification.getMessage();
        this.isRead = notification.isRead();
        this.createdAt = notification.getCreatedAt();
    }
}
