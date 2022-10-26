package com.innovation.stockstock.notification.dto;

import com.innovation.stockstock.notification.domain.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequestDto implements Serializable {
    private Event type;
    private String message;
    private Long postId;
}
