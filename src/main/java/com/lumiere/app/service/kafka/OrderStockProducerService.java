package com.lumiere.app.service.kafka;

import com.lumiere.app.service.dto.OrderStockProcessingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Service để gửi message xử lý stock quantity qua Kafka.
 */
@Service
public class OrderStockProducerService {

    private static final Logger LOG = LoggerFactory.getLogger(OrderStockProducerService.class);

    @Value("${spring.kafka.topic.order-stock-processing:order-stock-processing}")
    private String orderStockProcessingTopic;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderStockProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Gửi message để xử lý giảm stock quantity cho đơn hàng.
     *
     * @param message message chứa thông tin đơn hàng và items cần trừ stock
     */
    public void sendStockProcessingMessage(OrderStockProcessingMessage message) {
        try {
            kafkaTemplate.send(orderStockProcessingTopic, message);
            LOG.info("Sent stock processing message to Kafka: orderId={}, itemsCount={}", 
                message.getOrderId(), 
                message.getItems() != null ? message.getItems().size() : 0);
        } catch (Exception e) {
            LOG.error("Failed to send stock processing message to Kafka for order: {}", 
                message.getOrderId(), e);
            // Không throw exception để không ảnh hưởng đến việc tạo đơn hàng
            // Message sẽ được retry bởi Kafka hoặc có thể xử lý thủ công sau
        }
    }
}


