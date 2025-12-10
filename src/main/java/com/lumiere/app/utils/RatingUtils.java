package com.lumiere.app.utils;

import com.lumiere.app.domain.enumeration.RatingType;

/**
 * Utility class để xử lý Rating.
 */
public final class RatingUtils {

    private RatingUtils() {
        // Private constructor để ngăn khởi tạo instance
    }

    /**
     * Chuyển đổi RatingType enum thành số.
     *
     * @param ratingType rating type enum
     * @return số tương ứng (1-5), hoặc 0 nếu null
     */
    public static double toNumber(RatingType ratingType) {
        if (ratingType == null) {
            return 0.0;
        }

        return switch (ratingType) {
            case ONE -> 1.0;
            case TWO -> 2.0;
            case THREE -> 3.0;
            case FOUR -> 4.0;
            case FIVE -> 5.0;
        };
    }

    /**
     * Chuyển đổi số thành RatingType enum.
     *
     * @param number số từ 1-5
     * @return RatingType tương ứng
     * @throws IllegalArgumentException nếu số không hợp lệ
     */
    public static RatingType fromNumber(int number) {
        return switch (number) {
            case 1 -> RatingType.ONE;
            case 2 -> RatingType.TWO;
            case 3 -> RatingType.THREE;
            case 4 -> RatingType.FOUR;
            case 5 -> RatingType.FIVE;
            default -> throw new IllegalArgumentException("Invalid rating number: " + number + ". Must be between 1 and 5.");
        };
    }
}

