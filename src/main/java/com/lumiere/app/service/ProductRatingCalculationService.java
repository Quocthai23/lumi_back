package com.lumiere.app.service;

import com.lumiere.app.domain.OrderItem;
import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductReview;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.domain.enumeration.RatingType;
import com.lumiere.app.domain.enumeration.ReviewStatus;
import com.lumiere.app.repository.OrderItemRepository;
import com.lumiere.app.repository.OrdersRepository;
import com.lumiere.app.repository.ProductRepository;
import com.lumiere.app.repository.ProductReviewRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service để tính lại số sao trung bình của sản phẩm dựa trên các đơn hàng đã hoàn thành.
 */
@Service
@Transactional
public class ProductRatingCalculationService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductRatingCalculationService.class);

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final ProductReviewRepository productReviewRepository;

    public ProductRatingCalculationService(
        OrdersRepository ordersRepository,
        OrderItemRepository orderItemRepository,
        ProductRepository productRepository,
        ProductReviewRepository productReviewRepository
    ) {
        this.ordersRepository = ordersRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.productReviewRepository = productReviewRepository;
    }

    /**
     * Scheduled job chạy mỗi ngày lúc 3:00 AM để tính lại số sao của các sản phẩm
     * từ các đơn hàng đã hoàn thành gần đây (trong 7 ngày qua).
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void recalculateProductRatings() {
        LOG.info("Starting scheduled job to recalculate product ratings");

        // Lấy các đơn hàng đã hoàn thành trong 7 ngày qua, sắp xếp theo thời gian đặt hàng giảm dần
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<com.lumiere.app.domain.Orders> completedOrders = ordersRepository
            .findAll()
            .stream()
            .filter(order ->
                (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.DELIVERED) &&
                order.getPlacedAt() != null &&
                order.getPlacedAt().isAfter(sevenDaysAgo)
            )
            .sorted((o1, o2) -> o2.getPlacedAt().compareTo(o1.getPlacedAt())) // Sắp xếp giảm dần
            .collect(Collectors.toList());

        if (completedOrders.isEmpty()) {
            LOG.info("No completed orders found in the last 7 days");
            return;
        }

        LOG.info("Found {} completed orders to process", completedOrders.size());

        // Lấy tất cả product IDs từ các đơn hàng này
        Set<Long> productIds = new HashSet<>();
        for (com.lumiere.app.domain.Orders order : completedOrders) {
            List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(order.getId());
            for (OrderItem item : orderItems) {
                if (item.getProductVariant() != null && item.getProductVariant().getProduct() != null) {
                    productIds.add(item.getProductVariant().getProduct().getId());
                }
            }
        }

        if (productIds.isEmpty()) {
            LOG.info("No products found in completed orders");
            return;
        }

        LOG.info("Recalculating ratings for {} products", productIds.size());

        int updatedCount = 0;
        for (Long productId : productIds) {
            if (recalculateProductRating(productId)) {
                updatedCount++;
            }
        }

        LOG.info("Completed rating recalculation job. Updated {} products", updatedCount);
    }

    /**
     * Tính lại số sao trung bình cho một sản phẩm.
     *
     * @param productId ID sản phẩm
     * @return true nếu đã cập nhật, false nếu không
     */
    public boolean recalculateProductRating(Long productId) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isEmpty()) {
            return false;
        }

        Product product = productOpt.get();

        // Lấy tất cả reviews đã được approved của sản phẩm
        List<ProductReview> approvedReviews = productReviewRepository.findByProductIdAndStatus(
            productId,
            ReviewStatus.APPROVED
        );

        if (approvedReviews.isEmpty()) {
            // Nếu không có review nào, set về null hoặc 0
            product.setAverageRating(null);
            product.setReviewCount(0);
            productRepository.save(product);
            return true;
        }

        // Tính số sao trung bình
        double totalRating = 0.0;
        for (ProductReview review : approvedReviews) {
            totalRating += ratingToNumber(review.getRating());
        }

        double averageRating = totalRating / approvedReviews.size();
        int reviewCount = approvedReviews.size();

        // Làm tròn đến 1 chữ số thập phân
        averageRating = Math.round(averageRating * 10.0) / 10.0;

        // Cập nhật product
        product.setAverageRating(averageRating);
        product.setReviewCount(reviewCount);
        product.setUpdatedAt(Instant.now());
        productRepository.save(product);

        LOG.debug(
            "Updated product {} rating: {} (from {} reviews)",
            productId,
            averageRating,
            reviewCount
        );

        return true;
    }

    /**
     * Chuyển đổi RatingType thành số.
     *
     * @param rating RatingType
     * @return số tương ứng (1-5)
     */
    private int ratingToNumber(RatingType rating) {
        if (rating == null) {
            return 0;
        }
        return switch (rating) {
            case ONE -> 1;
            case TWO -> 2;
            case THREE -> 3;
            case FOUR -> 4;
            case FIVE -> 5;
        };
    }
}

