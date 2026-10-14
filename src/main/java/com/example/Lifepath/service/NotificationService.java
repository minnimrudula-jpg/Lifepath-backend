package com.example.Lifepath.service;

import com.example.Lifepath.dto.ApiDtos.NotificationView;
import com.example.Lifepath.entity.Notification;
import com.example.Lifepath.exception.ApiException;
import com.example.Lifepath.repository.NotificationRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {
    private final NotificationRepository notifications;

    public NotificationService(NotificationRepository notifications) {
        this.notifications = notifications;
    }

    @Transactional(readOnly = true)
    public List<NotificationView> list(Long userId) {
        return notifications.findAllByUserIdOrderByCreatedAtDesc(userId).stream().map(this::view).toList();
    }

    @Transactional
    public NotificationView markRead(Long userId, Long notificationId) {
        Notification notification = owned(userId, notificationId);
        notification.markRead();
        return view(notifications.save(notification));
    }

    @Transactional
    public void markAllRead(Long userId) {
        notifications.findAllByUserIdOrderByCreatedAtDesc(userId).forEach(Notification::markRead);
    }

    private Notification owned(Long userId, Long notificationId) {
        Notification notification = notifications.findById(notificationId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (!notification.getUser().getId().equals(userId))
            throw new ApiException(HttpStatus.NOT_FOUND, "Notification not found");
        return notification;
    }

    private NotificationView view(Notification notification) {
        return new NotificationView(notification.getId(), notification.getMessage(), notification.getCreatedAt(),
                notification.isRead());
    }
}
