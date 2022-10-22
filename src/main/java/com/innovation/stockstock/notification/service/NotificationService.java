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
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<?> send(Long memberId, NotificationRequestDto requestDto) {
        Optional<Member> member = memberRepository.findById(memberId);
        if(member.isEmpty()){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        if (requestDto == null) {
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        String eventId = memberId+"_"+System.currentTimeMillis();
        Notification notification = new Notification(requestDto, member.get());
        // 접속자가 여러 브라우저를 통해 연결한 emitter 전부 불러오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByMemberId(String.valueOf(memberId));
        // 접속자의 emitter 정보에 해당되는 event를 send하기
        sseEmitters.forEach(
                (key, emitter) -> {
                    emitterService.sendToClient(emitter, eventId, key, new NotificationResponseDto(notification));
                }
        );
        notificationRepository.save(notification);
        return ResponseEntity.ok().body(ResponseDto.success("Send Notification"));
    }

    public ResponseEntity<?> getNotification(Long id) {
        Optional<Member> member = memberRepository.findById(id);
        if(member.isEmpty()){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        List<NotificationResponseDto> responseDtoList = new ArrayList<>();
        List<Notification> notificationList = notificationRepository.findByMemberOrderByCreatedAtDesc(member.get());
        for (Notification n : notificationList) {
            responseDtoList.add(new NotificationResponseDto(n));
        }
        return ResponseEntity.ok().body(ResponseDto.success(responseDtoList));
    }

    @Transactional
    public ResponseEntity<?> readOk(Long id){
        Optional<Member> member = memberRepository.findById(id);
        if(member.isEmpty()){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        List<Notification> notificationList = notificationRepository.findByMemberOrderByCreatedAtDesc(member.get());
        for (Notification n : notificationList) {
            n.changeState();
        }
        return ResponseEntity.ok().body(ResponseDto.success("읽음 처리 완료"));
    }

    @Transactional
    public ResponseEntity<?> deleteNotification(Long id){
        Optional<Member> member = memberRepository.findById(id);
        if(member.isEmpty()){
            return ResponseEntity.badRequest().body(ResponseDto.fail(ErrorCode.NULL_ID));
        }
        notificationRepository.deleteByMember(member.get());
        return ResponseEntity.ok().body(ResponseDto.success("알림 삭제 완료"));
    }
}
