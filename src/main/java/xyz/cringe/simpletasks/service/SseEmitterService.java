package xyz.cringe.simpletasks.service;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class SseEmitterService {
    private final SpringTemplateEngine templateEngine;

    public SseEmitterService(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public SseEmitter createSseEmitter(ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters, String username, Logger logger) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.putIfAbsent(username, new CopyOnWriteArrayList<>());
        sseEmitters.get(username).add(sseEmitter);
        sseEmitter.onCompletion(() -> {
            sseEmitters.get(username).remove(sseEmitter);
            if (sseEmitters.get(username).isEmpty()) {
                sseEmitters.remove(username);
            }
        });
        sseEmitter.onTimeout(() -> {
            sseEmitters.get(username).remove(sseEmitter);
            if (sseEmitters.get(username).isEmpty()) {
                sseEmitters.remove(username);
            }
            sseEmitter.complete();
        });
        sseEmitter.onError((e) -> {
            logger.info("SseEmitter got error:", e);
            sseEmitters.get(username).remove(sseEmitter);
            if (sseEmitters.get(username).isEmpty()) {
                sseEmitters.remove(username);
            }
        });
        return sseEmitter;
    }

    public SseEmitter.SseEventBuilder buildData(String templateName, Object data, String dataName) {
        Context context = new Context();
        context.setVariable(dataName, data);
        String html = templateEngine.process(templateName, context);
        SseEmitter.SseEventBuilder event = SseEmitter.event();
        event.data(html);
        return event;
    }

    public void sendEvent(Logger logger, ConcurrentMap<String, CopyOnWriteArrayList<SseEmitter>> sseEmitters, SseEmitter.SseEventBuilder event) {
        for (var sseEmitterList : sseEmitters.entrySet()) {
            Collection<SseEmitter> emitters = sseEmitterList.getValue();
            for (SseEmitter emitter : emitters) {
                try {
                    emitter.send(event);
                    // TODO need to fix unhandled exception
                } catch (IOException e) {
                    logger.info("SseEmitter got error:", e);
                }
            }
        }
    }

}
