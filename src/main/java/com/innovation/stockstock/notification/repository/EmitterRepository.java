package com.innovation.stockstock.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {
    private Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter emitter) {
        emitterMap.put(emitterId, emitter);
        return emitter;
    }
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return emitterMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    public void deleteById(String emitterId) {
        if (emitterMap != null) {
            emitterMap.remove(emitterId);
        }
    }
}
