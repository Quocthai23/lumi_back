package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ProductReview;
import com.lumiere.app.domain.enumeration.NotificationType;
import com.lumiere.app.domain.enumeration.ReviewStatus;
import com.lumiere.app.repository.ProductReviewRepository;
import com.lumiere.app.service.ProductReviewService;
import com.lumiere.app.service.dto.ProductReviewDTO;
import com.lumiere.app.service.dto.ReviewRatingMessage;
import com.lumiere.app.service.kafka.NotificationProducerService;
import com.lumiere.app.service.mapper.ProductReviewMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.ProductReview}.
 */
@Service
@Transactional
public class ProductReviewServiceImpl implements ProductReviewService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductReviewServiceImpl.class);

    private final ProductReviewRepository productReviewRepository;

    private final ProductReviewMapper productReviewMapper;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final NotificationProducerService notificationProducerService;

    public ProductReviewServiceImpl(
        ProductReviewRepository productReviewRepository,
        ProductReviewMapper productReviewMapper,
        KafkaTemplate<String, Object> kafkaTemplate,
        NotificationProducerService notificationProducerService
    ) {
        this.productReviewRepository = productReviewRepository;
        this.productReviewMapper = productReviewMapper;
        this.kafkaTemplate = kafkaTemplate;
        this.notificationProducerService = notificationProducerService;
    }

    @Override
    public ProductReviewDTO save(ProductReviewDTO productReviewDTO) {
        LOG.debug("Request to save ProductReview : {}", productReviewDTO);
        ProductReview productReview = productReviewMapper.toEntity(productReviewDTO);
        productReview = productReviewRepository.save(productReview);
        
        // Gửi notification cho admin về review mới
        if (productReview.getProduct() != null) {
            String productName = productReview.getProduct().getName() != null ? 
                productReview.getProduct().getName() : "Sản phẩm #" + productReview.getProduct().getId();
            String adminMessage = String.format("Có đánh giá mới từ %s cho sản phẩm: %s", 
                productReview.getAuthor() != null ? productReview.getAuthor() : "Khách hàng",
                productName);
            notificationProducerService.sendAdminNotification(
                NotificationType.NEW_REVIEW,
                adminMessage,
                "/admin/product-reviews/" + productReview.getId()
            );
        }
        
        return productReviewMapper.toDto(productReview);
    }

    @Override
    public ProductReviewDTO update(ProductReviewDTO productReviewDTO) {
        LOG.debug("Request to update ProductReview : {}", productReviewDTO);
        
        // Lấy review cũ để so sánh status
        Optional<ProductReview> oldReviewOpt = productReviewRepository.findById(productReviewDTO.getId());
        ReviewStatus oldStatus = oldReviewOpt.map(ProductReview::getStatus).orElse(null);
        
        ProductReview productReview = productReviewMapper.toEntity(productReviewDTO);
        productReview = productReviewRepository.save(productReview);
        
        // Nếu status thay đổi, gửi Kafka message để tính lại rating
        if (oldStatus != null && oldStatus != productReview.getStatus()) {
            sendReviewRatingMessage(productReview);
        }
        
        return productReviewMapper.toDto(productReview);
    }

    @Override
    public Optional<ProductReviewDTO> partialUpdate(ProductReviewDTO productReviewDTO) {
        LOG.debug("Request to partially update ProductReview : {}", productReviewDTO);

        return productReviewRepository
            .findById(productReviewDTO.getId())
            .map(existingProductReview -> {
                ReviewStatus oldStatus = existingProductReview.getStatus();
                productReviewMapper.partialUpdate(existingProductReview, productReviewDTO);

                // Kiểm tra xem status có thay đổi không
                boolean statusChanged = oldStatus != existingProductReview.getStatus();
                
                ProductReview savedReview = productReviewRepository.save(existingProductReview);
                
                // Nếu status thay đổi, gửi Kafka message
                if (statusChanged) {
                    sendReviewRatingMessage(savedReview);
                }
                
                return savedReview;
            })
            .map(productReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all ProductReviews");
        return productReviewRepository.findAll(pageable).map(productReviewMapper::toDto);
    }

    public Page<ProductReviewDTO> findAllWithEagerRelationships(Pageable pageable) {
        return productReviewRepository.findAllWithEagerRelationships(pageable).map(productReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductReviewDTO> findOne(Long id) {
        LOG.debug("Request to get ProductReview : {}", id);
        return productReviewRepository.findOneWithEagerRelationships(id).map(productReviewMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ProductReview : {}", id);
        
        // Lấy review trước khi xóa để gửi Kafka message
        Optional<ProductReview> reviewOpt = productReviewRepository.findById(id);
        
        productReviewRepository.deleteById(id);
        
        // Gửi Kafka message để tính lại rating sau khi xóa review
        reviewOpt.ifPresent(this::sendReviewRatingMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewDTO> findByProductId(Long productId, Pageable pageable) {
        LOG.debug("Request to get ProductReviews by productId: {}", productId);
        return productReviewRepository
            .findByProductIdOrderByCreatedAtDesc(productId, pageable)
            .map(productReviewMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductReviewDTO> findByProductIdAndApproved(Long productId, Pageable pageable) {
        LOG.debug("Request to get approved ProductReviews by productId: {}", productId);
        return productReviewRepository
            .findByProductIdAndApprovedOrderByCreatedAtDesc(productId, pageable)
            .map(productReviewMapper::toDto);
    }

    /**
     * Gửi Kafka message để tính lại rating cho product.
     *
     * @param review review đã được cập nhật/xóa
     */
    private void sendReviewRatingMessage(ProductReview review) {
        if (review.getProduct() == null) {
            LOG.warn("Review {} has no product, skipping Kafka message", review.getId());
            return;
        }

        try {
            ReviewRatingMessage message = new ReviewRatingMessage(
                review.getProduct().getId(),
                review.getId(),
                review.getRating(),
                null, // customerId không cần thiết khi update status
                null  // orderId không cần thiết khi update status
            );
            kafkaTemplate.send("review-rating", message);
            LOG.info("Sent Kafka message for review status change: {}", message);
        } catch (Exception e) {
            LOG.error("Failed to send Kafka message for review {}", review.getId(), e);
            // Không throw exception để không rollback transaction
        }
    }
}
