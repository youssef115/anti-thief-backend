package com.youssef.demo.service;

import com.youssef.demo.entity.Location;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class LocationBroadcaster {

    private static final long SSE_TIMEOUT_MS = 30 * 60 * 1000;
    private static final int MAX_CONNECTIONS = 10;

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public SseEmitter subscribe() {
        if (emitters.size() >= MAX_CONNECTIONS) {
            log.warn("Max SSE connections reached, rejecting new connection");
            SseEmitter rejected = new SseEmitter(0L);
            rejected.completeWithError(new RuntimeException("Max connections reached"));
            return rejected;
        }

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        log.debug("New SSE client subscribed, total clients: {}", emitters.size());
        return emitter;
    }

    public void broadcast(Location location) {
        String json = String.format(
            "{\"deviceId\":\"%s\",\"latitude\":%f,\"longitude\":%f,\"timestamp\":%d}",
            location.getDeviceId(),
            location.getLatitude(),
            location.getLongitude(),
            location.getTimestamp()
        );

        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                        .name("location")
                        .data(json));
            } catch (IOException e) {
                emitters.remove(emitter);
                log.debug("Removed disconnected SSE client");
            }
        }
    }

    public void broadcastAll(List<Location> locations) {
        for (Location location : locations) {
            broadcast(location);
        }
    }
}
