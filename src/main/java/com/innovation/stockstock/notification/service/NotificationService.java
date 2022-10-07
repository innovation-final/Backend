package com.innovation.stockstock.notification.service;

import com.innovation.stockstock.common.ErrorCode;
import com.innovation.stockstock.common.dto.ResponseDto;
import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.notification.domain.Notification;
import com.innovation.stockstock.notification.dto.NotificationRequestDto;
import com.innovation.stockstock.notification.dto.NotificationResponseDto;
import com.innovation.stockstock.notification.repository.EmitterRepository;
import com.innovation.stockstock.notification.repository.NotificationRepository;
import com.innovation.stockstock.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationService {
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;
    private final MemberRepository memberRepository;
    private final EmitterService emitterService;

    public ResponseDto<?> send(Long id, NotificationRequestDto requestDto) {
        Optional<Member> member = memberRepository.findById(id);
        if(member.isEmpty()){
            return ResponseDto.fail(ErrorCode.NULL_ID);
        }
        if (requestDto == null) {
            return ResponseDto.fail(ErrorCode.NULL_ID);
        }
        Notification notification = new Notification(requestDto, member.get());

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(String.valueOf(id));

        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, notification);
                    emitterService.sendToClient(emitter, key, new NotificationResponseDto(notification));
                }
        );
        return ResponseDto.success("Send Notification");
    }

    public ResponseDto<?> getNotification(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if(member.isEmpty()){
            return ResponseDto.fail(ErrorCode.NULL_ID);
        }
        List<NotificationResponseDto> responseDtoList = new ArrayList<>();
        List<Notification> notificationList = notificationRepository.findByMemberOrderByCreatedAtDesc(member.get());
        for (Notification n : notificationList) {
            responseDtoList.add(new NotificationResponseDto(n));
        }
        return ResponseDto.success(responseDtoList);
    }

    @Transactional
    public ResponseDto<?> readOk(Long id){
        Optional<Member> member = memberRepository.findById(id);
        if(member.isEmpty()){
            return ResponseDto.fail(ErrorCode.NULL_ID);
        }
        List<Notification> notificationList = notificationRepository.findByMemberOrderByCreatedAtDesc(member.get());
        for (Notification n : notificationList) {
            n.changeState();
        }
        return ResponseDto.success("읽음 처리 완료");
    }

    @Transactional
    public ResponseDto<?> deleteNotification(Long id){
        Optional<Member> member = memberRepository.findById(id);
        if(member.isEmpty()){
            return ResponseDto.fail(ErrorCode.NULL_ID);
        }
        notificationRepository.deleteByMember(member.get());
        return ResponseDto.success("알림 삭제 완료");
    }
}
