package com.innovation.stockstock.notification.service;

import com.innovation.stockstock.member.domain.Member;
import com.innovation.stockstock.member.repository.MemberRepository;
import com.innovation.stockstock.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmitterService {
    private final EmitterRepository emitterRepository;
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60 * 24;
    private final MemberRepository memberRepository;

    public SseEmitter createEmitter(Long memberId, String lastEventId) {
        Optional<Member> member = memberRepository.findById(memberId);
        String emitterId = member.get().getId()+"_"+System.currentTimeMillis();

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));
        emitter.onError(e -> {emitterRepository.deleteById(emitterId);});

        String eventId = memberId + "_" + System.currentTimeMillis();
        sendToClient(emitter, eventId, emitterId, "EventStream Created. [nickName="+member.get().getNickname()+"]");

        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), emitterId, entry.getValue()));
        }
        return emitter;
    }

    public void sendToClient(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
            throw new RuntimeException("연결 오류!");
        }
    }
}