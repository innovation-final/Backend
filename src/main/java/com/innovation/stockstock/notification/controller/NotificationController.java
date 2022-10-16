package com.innovation.stockstock.notification.controller;

import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.notification.service.EmitterService;
import com.innovation.stockstock.notification.service.NotificationService;
import com.innovation.stockstock.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final EmitterService emitterService;
    private final NotificationService notificationService;

    @GetMapping("/api/auth/notifications")
    public ResponseEntity<?> getNotification() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
        return notificationService.getNotification(member.getId());
    }

    @GetMapping("/api/auth/notifications/read")
    public ResponseEntity<?> readOk() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
        return notificationService.readOk(member.getId());
    }

    @DeleteMapping("/api/auth/notifications")
    public ResponseEntity<?> deleteNotification() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
        return notificationService.deleteNotification(member.getId());
    }

    @GetMapping(value = "/api/subscribe/{id}", produces = "text/event-stream")
    public SseEmitter subscribe(@PathVariable Long id, @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return emitterService.createEmitter(id);
    }

    @GetMapping("/api/auth/tracelikestock")
    // 지속적으로 프론트에서 api요청을 하거나 아님 서버에서 스케쥴링을 돌려 해당 이벤트 발생 시 리턴해주거나.
    // 불필요한 api 요청 발생. vs 불필요한 전체종목 조회 발생.
    public ResponseEntity<?> noticeLikeStockPrice() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
        return notificationService.noticeLikeStockPrice(member);
    }
}
