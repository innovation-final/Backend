package com.innovation.stockstock.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class EmitterRepository {
    private Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    public SseEmitter save(String emitterId, SseEmitter emitter) {
        emitterMap.put(emitterId, emitter);
        return emitter;
    }
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }
    public Map<String, SseEmitter> findAllEmitterStartWithByMemberId(String memberId) {
        return emitterMap.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    public Map<String, Object> findAllEventCacheStartWithByMemberId(String memberId) {
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(memberId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
    public void deleteById(String emitterId) {
        if (emitterMap != null) {
            emitterMap.remove(emitterId);
        }
    }
    public void deleteAllEmitterStartWithId(String memberId) {
        emitterMap.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        emitterMap.remove(key);
                    }
                }
        );
    }
    public void deleteAllEventCacheStartWithId(String memberId) {
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }

    public Optional<SseEmitter> get(String id) {
        return Optional.ofNullable(emitterMap.get(id));
    }
}
