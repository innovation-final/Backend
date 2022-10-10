package com.innovation.stockstock.notification.domain;

import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.common.dto.Timestamped;
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
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 알람을 누르면 이동할 링크

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
