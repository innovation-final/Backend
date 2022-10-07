package com.innovation.stockstock.notification.repository;

import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.notification.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByMemberOrderByCreatedAtDesc(Member member);
    void deleteByMember(Member member);
}
