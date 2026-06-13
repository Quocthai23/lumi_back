package com.lumiere.app.service.kafka;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.Notification;
import com.lumiere.app.repository.CustomerRepository;
import com.lumiere.app.repository.NotificationRepository;
import com.lumiere.app.service.dto.NotificationMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Kafka consumer để lưu notifications vào database.
 */
@Service
public class NotificationConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationConsumer.class);

    private final NotificationRepository notificationRepository;
    private final CustomerRepository customerRepository;

    public NotificationConsumer(
        NotificationRepository notificationRepository,
        CustomerRepository customerRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.customerRepository = customerRepository;
    }

    @KafkaListener(topics = "${spring.kafka.topic.notification:notification-topic}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void consumeNotification(NotificationMessage message, Acknowledgment acknowledgment) {
        try {
            LOG.debug("Received notification message: {}", message);

            Notification notification = new Notification();
            notification.setType(message.getType());
            notification.setMessage(message.getMessage());
            notification.setLink(message.getLink());
            notification.setIsRead(false);
            notification.setCreatedAt(message.getCreatedAt() != null ? message.getCreatedAt() : java.time.Instant.now());

            // Nếu có customerId, set customer; nếu không thì null (notification cho admin)
            if (message.getCustomerId() != null) {
                Optional<Customer> customer = customerRepository.findById(message.getCustomerId());
                if (customer.isPresent()) {
                    notification.setCustomer(customer.get());
                } else {
                    LOG.warn("Customer not found with id: {}, skipping notification", message.getCustomerId());
                    acknowledgment.acknowledge();
                    return;
                }
            } else {
                notification.setCustomer(null); // Notification cho admin
            }

            notificationRepository.save(notification);
            LOG.info("Saved notification to database: type={}, customerId={}", message.getType(), message.getCustomerId());

            acknowledgment.acknowledge();
        } catch (Exception e) {
            LOG.error("Error processing notification message", e);
            // Không acknowledge để message có thể được retry
            // Trong production, có thể implement retry logic hoặc dead letter queue
        }
    }
}

