package com.backend.service.notification;

import com.backend.entity.meeting.Meeting;
import com.backend.entity.notification.Notification;
import com.backend.entity.notification.NotificationType;
import com.backend.entity.user.User;
import com.backend.repository.notification.EmittersRepository;
import com.backend.repository.notification.NotificationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final String CONNECTION_NAME = "notification";
    private final EmittersRepository emittersRepository;
    private final NotificationRepository notificationRepository;

    private static Notification buildNotification(User user, NotificationType type) {
        return Notification.builder()
                .user(user)
                .type(type)
                .isRead(false)
                .build();
    }

    public SseEmitter subscribe(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);
        try {
            sseEmitter.send(SseEmitter.event().name(CONNECTION_NAME));
        } catch (IOException e) {
            e.printStackTrace();
        }

        emittersRepository.addEmitter(userId, sseEmitter);

        sseEmitter.onCompletion(() -> emittersRepository.removeEmitter(userId));
        sseEmitter.onTimeout(() -> emittersRepository.removeEmitter(userId));
        sseEmitter.onError((e) -> emittersRepository.removeEmitter(userId));

        return sseEmitter;
    }

    public void sendNotification(User user, Meeting meeting, NotificationType type) {
        Notification notification = buildNotification(user, type);
        notificationRepository.save(notification);

        SseEmitter emitter = emittersRepository.getEmitter(user.getId());

        if (emitter != null) {
            try {
                Map<String, String> data = new HashMap<>();
                data.put("notification_type", type.toString());
                data.put("nickname", user.getNickname());
                data.put("meeting_id", meeting.getId().toString());
                data.put("title", meeting.getTitle());
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(data);

                emitter.send(SseEmitter.event().name(type.toString()).data(jsonData));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
