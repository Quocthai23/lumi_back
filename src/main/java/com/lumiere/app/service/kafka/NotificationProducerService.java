package com.lumiere.app.service.kafka;

import com.lumiere.app.domain.enumeration.NotificationType;
import com.lumiere.app.service.dto.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service để gửi notification messages qua Kafka.
 */
@Service
public class NotificationProducerService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationProducerService.class);

    @Value("${spring.kafka.topic.notification:notification-topic}")
    private String notificationTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public NotificationProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Gửi notification message qua Kafka.
     *
     * @param type loại notification
     * @param message nội dung thông báo
     * @param link link liên kết (có thể null)
     * @param customerId ID khách hàng (null nếu là notification cho admin)
     */
    public void sendNotification(NotificationType type, String message, String link, Long customerId) {
        try {
            NotificationMessage notificationMessage = new NotificationMessage(type, message, link, customerId);
            kafkaTemplate.send(notificationTopic, notificationMessage);
            LOG.info("Sent notification message to Kafka: type={}, customerId={}, message={}", type, customerId, message);
        } catch (Exception e) {
            LOG.error("Failed to send notification message to Kafka", e);
            // Không throw exception để không ảnh hưởng đến business logic chính
        }
    }

    /**
     * Gửi notification cho admin (customerId = null).
     */
    public void sendAdminNotification(NotificationType type, String message, String link) {
        sendNotification(type, message, link, null);
    }

    /**
     * Gửi notification cho customer.
     */
    public void sendCustomerNotification(Long customerId, NotificationType type, String message, String link) {
        sendNotification(type, message, link, customerId);
    }
}




