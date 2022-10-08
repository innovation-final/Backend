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
        return ResponseEntity.ok().body(notificationService.getNotification(member.getId()));
    }

    @GetMapping("/api/auth/notifications/read")
    public ResponseEntity<?> readOk() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
        return ResponseEntity.ok().body(notificationService.readOk(member.getId()));
    }

    @DeleteMapping("/api/auth/notifications")
    public ResponseEntity<?> deleteNotification() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();
        return ResponseEntity.ok().body(notificationService.deleteNotification(member.getId()));
    }

    @GetMapping(value = "/api/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return emitterService.createEmitter(lastEventId);
    }
}
