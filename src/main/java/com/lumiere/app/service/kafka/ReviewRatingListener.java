package com.lumiere.app.service.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductReview;
import com.lumiere.app.domain.enumeration.ReviewStatus;
import com.lumiere.app.repository.ProductRepository;
import com.lumiere.app.repository.ProductReviewRepository;
import com.lumiere.app.service.dto.ReviewRatingMessage;
import com.lumiere.app.utils.RatingUtils;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Kafka Listener để xử lý việc tính lại rating trung bình cho sản phẩm khi có review mới.
 */
@Service
public class ReviewRatingListener {

    private static final Logger LOG = LoggerFactory.getLogger(ReviewRatingListener.class);

    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;
    private final ObjectMapper objectMapper;

    public ReviewRatingListener(
        ProductRepository productRepository,
        ProductReviewRepository productReviewRepository,
        ObjectMapper objectMapper
    ) {
        this.productRepository = productRepository;
        this.productReviewRepository = productReviewRepository;
        this.objectMapper = objectMapper;
    }

    /**
     * Lắng nghe topic "review-rating" để cập nhật rating trung bình của sản phẩm.
     *
     * @param message message từ Kafka
     * @param acknowledgment acknowledgment để commit offset
     */
    @KafkaListener(topics = "review-rating", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional
    public void handleReviewRating(String message, Acknowledgment acknowledgment) {
        try {
            LOG.debug("Received Kafka message for review-rating: {}", message);

            // Parse message
            ReviewRatingMessage reviewMessage = objectMapper.readValue(message, ReviewRatingMessage.class);
            LOG.info("Processing review rating for product: {}, review: {}", reviewMessage.getProductId(), reviewMessage.getReviewId());

            Product product = productRepository.findById(reviewMessage.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + reviewMessage.getProductId()));

            // Tính lại average rating và review count
            updateProductRating(product);

            // Commit offset sau khi xử lý thành công
            acknowledgment.acknowledge();
            LOG.info("Successfully processed review rating for product: {}", reviewMessage.getProductId());

        } catch (Exception e) {
            LOG.error("Error processing review rating message: {}", message, e);
            // Không commit offset để Kafka retry message này
            // Hoặc có thể gửi vào dead letter queue nếu muốn
        }
    }

    /**
     * Cập nhật average rating và review count của product dựa trên tất cả reviews đã được approve.
     *
     * @param product product cần cập nhật
     */
    private void updateProductRating(Product product) {
        // Lấy tất cả reviews đã được approve của product này
        List<ProductReview> approvedReviews = productReviewRepository.findAll().stream()
            .filter(review ->
                review.getProduct() != null &&
                review.getProduct().getId().equals(product.getId())
            )
            .toList();

        if (approvedReviews.isEmpty()) {
            product.setAverageRating(0.0);
            product.setReviewCount(0);
        } else {
            // Tính tổng rating (chuyển RatingType thành số)
            double totalRating = approvedReviews.stream()
                .mapToDouble(review -> RatingUtils.toNumber(review.getRating()))
                .sum();

            // Tính rating trung bình
            double averageRating = totalRating / approvedReviews.size();

            product.setAverageRating(averageRating);
            product.setReviewCount(approvedReviews.size());
        }

        productRepository.save(product);
        LOG.info("Updated product {} rating: average={}, count={}",
            product.getId(), product.getAverageRating(), product.getReviewCount());
    }
}

