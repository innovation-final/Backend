package com.innovation.stockstock.notification.domain;

import com.innovation.stockstock.entity.Member;
import com.innovation.stockstock.entity.Timestamped;
import com.innovation.stockstock.notification.dto.NotificationRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Notification extends Timestamped {

    @Id
    @GeneratedValue
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private Event type;

    @Column
    private String message;

    @Column(nullable = false)
    private boolean isRead;

    @ManyToOne
    @JoinColumn
    private Member member;

    public Notification(NotificationRequestDto requestDto, Member member) {
        this.type = requestDto.getType();
        this.message = requestDto.getMessage();
        this.isRead = false;
        this.member = member;
    }

    public void changeState() {
        this.isRead = true;
    }
}