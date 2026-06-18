SET FOREIGN_KEY_CHECKS = 0;

-- Delete old seed records with IDs >= 1101 to ensure script is idempotent
DELETE FROM chat_message WHERE id >= 1101;
DELETE FROM chat_session WHERE id >= 1101;
DELETE FROM contact_message WHERE id >= 1101;
DELETE FROM stock_notification WHERE id >= 1101;
DELETE FROM notification WHERE id >= 1101;
DELETE FROM product_answer WHERE id >= 1101;
DELETE FROM product_question WHERE id >= 1101;
DELETE FROM product_review WHERE id >= 1101;
DELETE FROM loyalty_transaction WHERE id >= 1101;
DELETE FROM flash_sale_product WHERE id >= 1101;
DELETE FROM flash_sale WHERE id >= 1101;
DELETE FROM voucher WHERE id >= 1101;
DELETE FROM stock_movement WHERE id >= 1101;
DELETE FROM inventory WHERE id >= 1101;
DELETE FROM warehouse WHERE id >= 1101;
DELETE FROM order_item WHERE id >= 1101;
DELETE FROM order_status_history WHERE id >= 1101;
DELETE FROM orders WHERE id >= 1101;
DELETE FROM address WHERE id >= 1101;
DELETE FROM rel_customer__wishlist WHERE customer_id >= 1101;
DELETE FROM rel_collection__products WHERE collection_id >= 1101;
DELETE FROM collection WHERE id >= 1101;
DELETE FROM product_variant WHERE id >= 1101;
DELETE FROM product WHERE id >= 1101;
DELETE FROM customer WHERE id >= 1101;
DELETE FROM jhi_user WHERE id >= 1101;

-- 1. Seed data for JHI_USER (ids 1101 to 1110)
INSERT INTO jhi_user (id, login, password_hash, first_name, last_name, email, activated, created_by, created_date) VALUES
(1101, 'seed_user1', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'John', 'Doe', 'seed_user1@example.com', 1, 'system', NOW()),
(1102, 'seed_user2', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'Jane', 'Smith', 'seed_user2@example.com', 1, 'system', NOW()),
(1103, 'seed_user3', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'Michael', 'Johnson', 'seed_user3@example.com', 1, 'system', NOW()),
(1104, 'seed_user4', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'Emily', 'Brown', 'seed_user4@example.com', 1, 'system', NOW()),
(1105, 'seed_user5', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'David', 'Davis', 'seed_user5@example.com', 1, 'system', NOW()),
(1106, 'seed_user6', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'Sarah', 'Miller', 'seed_user6@example.com', 1, 'system', NOW()),
(1107, 'seed_user7', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'James', 'Wilson', 'seed_user7@example.com', 1, 'system', NOW()),
(1108, 'seed_user8', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'Lisa', 'Moore', 'seed_user8@example.com', 1, 'system', NOW()),
(1109, 'seed_user9', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'Robert', 'Taylor', 'seed_user9@example.com', 1, 'system', NOW()),
(1110, 'seed_user10', '$2a$10$gSAhZ1YspTgtIryWRkX.1e9D/w/D/4c44N5g.65F.E8f8r.g2s51q', 'Patricia', 'Anderson', 'seed_user10@example.com', 1, 'system', NOW());

-- 2. Seed data for CUSTOMER (ids 1101 to 1110)
INSERT INTO customer (id, first_name, last_name, phone, tier, loyalty_points, user_id, birthday) VALUES
(1101, 'John', 'Doe', '0901234561', 'BRONZE', 100, 1101, '1990-01-01'),
(1102, 'Jane', 'Smith', '0901234562', 'SILVER', 250, 1102, '1992-05-15'),
(1103, 'Michael', 'Johnson', '0901234563', 'GOLD', 600, 1103, '1988-11-20'),
(1104, 'Emily', 'Brown', '0901234564', 'BRONZE', 50, 1104, '1995-07-08'),
(1105, 'David', 'Davis', '0901234565', 'SILVER', 300, 1105, '1985-03-30'),
(1106, 'Sarah', 'Miller', '0901234566', 'GOLD', 850, 1106, '1991-09-12'),
(1107, 'James', 'Wilson', '0901234567', 'BRONZE', 0, 1107, '1994-12-25'),
(1108, 'Lisa', 'Moore', '0901234568', 'SILVER', 150, 1108, '1993-02-14'),
(1109, 'Robert', 'Taylor', '0901234569', 'GOLD', 1200, 1109, '1987-06-18'),
(1110, 'Patricia', 'Anderson', '0901234570', 'BRONZE', 90, 1110, '1989-10-05');

-- 3. Seed data for ADDRESS (ids 1101 to 1110)
INSERT INTO address (id, full_name, phone, street, city, is_default, customer_id) VALUES
(1101, 'John Doe', '0901234561', '123 Le Loi Street', 'Ho Chi Minh City', 1, 1101),
(1102, 'Jane Smith', '0901234562', '456 Nguyen Hue Street', 'Ho Chi Minh City', 1, 1102),
(1103, 'Michael Johnson', '0901234563', '789 Tran Hung Dao Street', 'Da Nang', 1, 1103),
(1104, 'Emily Brown', '0901234564', '101 Ba Trieu Street', 'Hanoi', 1, 1104),
(1105, 'David Davis', '0901234565', '202 Kim Ma Street', 'Hanoi', 1, 1105),
(1106, 'Sarah Miller', '0901234566', '303 Hung Vuong Street', 'Hue', 1, 1106),
(1107, 'James Wilson', '0901234567', '404 CMT8 Street', 'Can Tho', 1, 1107),
(1108, 'Lisa Moore', '0901234568', '505 Ton Duc Thang Street', 'Ho Chi Minh City', 1, 1108),
(1109, 'Robert Taylor', '0901234569', '606 Hoang Dieu Street', 'Da Nang', 1, 1109),
(1110, 'Patricia Anderson', '0901234570', '707 Lang Ha Street', 'Hanoi', 1, 1110);

-- 4. Seed data for PRODUCT (ids 1101 to 1110)
INSERT INTO product (id, code, name, slug, description, status, category, material, average_rating, review_count, images, created_at, updated_at) VALUES
(1101, 'PROD-001', 'Elegant Silk Dress', 'elegant-silk-dress', 'A beautiful and elegant silk dress perfect for formal events.', 'ACTIVE', 'Dresses', 'Silk', 4.5, 2, '["/uploads/silk-dress-1.jpg", "/uploads/silk-dress-2.jpg"]', NOW(), NOW()),
(1102, 'PROD-002', 'Casual Cotton T-Shirt', 'casual-cotton-t-shirt', 'Comfortable everyday cotton t-shirt.', 'ACTIVE', 'T-Shirts', 'Cotton', 4.0, 1, '["/uploads/tshirt-1.jpg"]', NOW(), NOW()),
(1103, 'PROD-003', 'Slim Fit Denim Jeans', 'slim-fit-denim-jeans', 'Stylish slim fit blue denim jeans.', 'ACTIVE', 'Jeans', 'Denim', 4.8, 1, '["/uploads/jeans-1.jpg"]', NOW(), NOW()),
(1104, 'PROD-004', 'Classic Leather Jacket', 'classic-leather-jacket', 'Premium leather jacket with modern details.', 'ACTIVE', 'Jackets', 'Leather', 4.7, 1, '["/uploads/jacket-1.jpg"]', NOW(), NOW()),
(1105, 'PROD-005', 'Cozy Wool Sweater', 'cozy-wool-sweater', 'Warm and cozy wool sweater for winter.', 'ACTIVE', 'Sweaters', 'Wool', 4.2, 1, '["/uploads/sweater-1.jpg"]', NOW(), NOW()),
(1106, 'PROD-006', 'Sporty Running Shorts', 'sporty-running-shorts', 'Breathable athletic shorts for running and workouts.', 'ACTIVE', 'Shorts', 'Polyester', 4.1, 1, '["/uploads/shorts-1.jpg"]', NOW(), NOW()),
(1107, 'PROD-007', 'Linen Button-Up Shirt', 'linen-button-up-shirt', 'Lightweight linen shirt for warm summer days.', 'ACTIVE', 'Shirts', 'Linen', 4.4, 1, '["/uploads/shirt-1.jpg"]', NOW(), NOW()),
(1108, 'PROD-008', 'Chic Pleated Skirt', 'chic-pleated-skirt', 'Fashionable pleated skirt for casual outings.', 'ACTIVE', 'Skirts', 'Polyester', 4.3, 1, '["/uploads/skirt-1.jpg"]', NOW(), NOW()),
(1109, 'PROD-009', 'Formal Office Blazer', 'formal-office-blazer', 'Professional blazer for work and meetings.', 'ACTIVE', 'Blazers', 'Wool Blend', 4.6, 1, '["/uploads/blazer-1.jpg"]', NOW(), NOW()),
(1110, 'PROD-010', 'Active Fleece Hoodie', 'active-fleece-hoodie', 'Soft fleece hoodie with front pockets.', 'ACTIVE', 'Hoodies', 'Fleece', 4.5, 0, '["/uploads/hoodie-1.jpg"]', NOW(), NOW());

-- 5. Seed data for PRODUCT_VARIANT (ids 1101 to 1110)
INSERT INTO product_variant (id, sku, name, price, compare_at_price, currency, stock_quantity, is_default, color, size, product_id, url_image) VALUES
(1101, 'SKU-001-M-RED', 'Elegant Silk Dress - Red - M', 120.00, 150.00, 'USD', 50, 1, 'Red', 'M', 1101, '/uploads/silk-dress-red.jpg'),
(1102, 'SKU-002-L-WHT', 'Casual Cotton T-Shirt - White - L', 25.00, 30.00, 'USD', 120, 1, 'White', 'L', 1102, '/uploads/tshirt-white.jpg'),
(1103, 'SKU-003-32-BLU', 'Slim Fit Denim Jeans - Blue - 32', 65.00, 80.00, 'USD', 75, 1, 'Blue', '32', 1103, '/uploads/jeans-blue.jpg'),
(1104, 'SKU-004-XL-BLK', 'Classic Leather Jacket - Black - XL', 210.00, 250.00, 'USD', 30, 1, 'Black', 'XL', 1104, '/uploads/jacket-black.jpg'),
(1105, 'SKU-005-S-GRY', 'Cozy Wool Sweater - Grey - S', 55.00, 70.00, 'USD', 45, 1, 'Grey', 'S', 1105, '/uploads/sweater-grey.jpg'),
(1106, 'SKU-006-M-BLK', 'Sporty Running Shorts - Black - M', 30.00, 35.00, 'USD', 90, 1, 'Black', 'M', 1106, '/uploads/shorts-black.jpg'),
(1107, 'SKU-007-L-BLU', 'Linen Button-Up Shirt - Light Blue - L', 45.00, 50.00, 'USD', 60, 1, 'Light Blue', 'L', 1107, '/uploads/shirt-blue.jpg'),
(1108, 'SKU-008-S-PNK', 'Chic Pleated Skirt - Pink - S', 38.00, 45.00, 'USD', 40, 1, 'Pink', 'S', 1108, '/uploads/skirt-pink.jpg'),
(1109, 'SKU-009-M-NVY', 'Formal Office Blazer - Navy - M', 95.00, 120.00, 'USD', 25, 1, 'Navy', 'M', 1109, '/uploads/blazer-navy.jpg'),
(1110, 'SKU-010-XL-GRY', 'Active Fleece Hoodie - Grey - XL', 50.00, 60.00, 'USD', 70, 1, 'Grey', 'XL', 1110, '/uploads/hoodie-grey.jpg');

-- 6. Seed data for COLLECTION (ids 1101 to 1110)
INSERT INTO collection (id, name, slug, description, image_url, look_image_url) VALUES
(1101, 'Summer Breeze Collection', 'summer-breeze', 'Lightweight and stylish outfits for hot summer days.', '/uploads/col-summer.jpg', '/uploads/look-summer.jpg'),
(1102, 'Autumn Leaves Collection', 'autumn-leaves', 'Cozy and colorful wear for the autumn season.', '/uploads/col-autumn.jpg', '/uploads/look-autumn.jpg'),
(1103, 'Winter Comfort Collection', 'winter-comfort', 'Warm, stylish layerable clothing for the winter chill.', '/uploads/col-winter.jpg', '/uploads/look-winter.jpg'),
(1104, 'Spring Blossom Collection', 'spring-blossom', 'Pastel colors and floral patterns to celebrate spring.', '/uploads/col-spring.jpg', '/uploads/look-spring.jpg'),
(1105, 'Denim & More Collection', 'denim-and-more', 'All kinds of high-quality denim styles.', '/uploads/col-denim.jpg', '/uploads/look-denim.jpg'),
(1106, 'Athleisure & Gym Collection', 'athleisure-gym', 'Sporty, functional clothing that looks great in the gym and on the street.', '/uploads/col-gym.jpg', '/uploads/look-gym.jpg'),
(1107, 'Office Essentials Collection', 'office-essentials', 'Professional, polished attire for the workplace.', '/uploads/col-office.jpg', '/uploads/look-office.jpg'),
(1108, 'Midnight Party Collection', 'midnight-party', 'Stunning, sparkling outfits for nights out.', '/uploads/col-party.jpg', '/uploads/look-party.jpg'),
(1109, 'Eco-Friendly Line Collection', 'eco-friendly', 'Sustainable, organic materials crafted beautifully.', '/uploads/col-eco.jpg', '/uploads/look-eco.jpg'),
(1110, 'Casual Comforts Collection', 'casual-comforts', 'Simple, comfortable clothing for lounging at home or quick errands.', '/uploads/col-casual.jpg', '/uploads/look-casual.jpg');

-- 7. Seed data for REL_COLLECTION__PRODUCTS
INSERT INTO rel_collection__products (collection_id, products_id) VALUES
(1101, 1101),
(1101, 1107),
(1102, 1105),
(1103, 1104),
(1103, 1105),
(1105, 1103),
(1106, 1106),
(1107, 1109),
(1108, 1101),
(1110, 1102);

-- 8. Seed data for REL_CUSTOMER__WISHLIST
INSERT INTO rel_customer__wishlist (customer_id, wishlist_id) VALUES
(1101, 1101),
(1102, 1102),
(1103, 1103),
(1104, 1104),
(1105, 1105),
(1106, 1106),
(1107, 1107),
(1108, 1108),
(1109, 1109),
(1110, 1110);

-- 9. Seed data for WAREHOUSE (ids 1101 to 1110)
INSERT INTO warehouse (id, name, address, is_active) VALUES
(1101, 'Main Warehouse Saigon', '100 QL1A, District 12, HCMC', 1),
(1102, 'Hanoi Distribution Center', '45 Pham Hung, Cau Giay, Hanoi', 1),
(1103, 'Da Nang Transit Hub', '88 Nguyen Luong Bang, Da Nang', 1),
(1104, 'Can Tho Storage Facility', '12 Mieu Noi, Ninh Kieu, Can Tho', 1),
(1105, 'Haiphong Port Warehouse', '5 Terminal Rd, Haiphong', 1),
(1106, 'Nha Trang Hub', '20 Tran Phu, Nha Trang', 1),
(1107, 'Vinh Center', '14 Le Loi, Vinh', 1),
(1108, 'Bien Hoa Facility', '30 Dong Khoi, Bien Hoa', 1),
(1109, 'Quang Ninh Hub', '8 Ha Long Rd, Ha Long', 1),
(1110, 'Buon Ma Thuot Storage', '70 Le Duan, Buon Ma Thuot', 1);

-- 10. Seed data for INVENTORY (ids 1101 to 1110)
INSERT INTO inventory (id, stock_quantity, product_variant_id, warehouse_id) VALUES
(1101, 30, 1101, 1101),
(1102, 100, 1102, 1102),
(1103, 50, 1103, 1103),
(1104, 20, 1104, 1101),
(1105, 35, 1105, 1102),
(1106, 60, 1106, 1103),
(1107, 45, 1107, 1101),
(1108, 30, 1108, 1102),
(1109, 15, 1109, 1103),
(1110, 50, 1110, 1101);

-- 11. Seed data for STOCK_MOVEMENT (ids 1101 to 1110)
INSERT INTO stock_movement (id, created_at, delta, product_variant_id, reason, ref_order_id, warehouse_id, note, quantity_change) VALUES
(1101, NOW(), 30, 1101, 'PURCHASE', NULL, 1101, 'Initial inventory import', 30),
(1102, NOW(), 100, 1102, 'PURCHASE', NULL, 1102, 'Initial inventory import', 100),
(1103, NOW(), 50, 1103, 'PURCHASE', NULL, 1103, 'Initial inventory import', 50),
(1104, NOW(), 20, 1104, 'PURCHASE', NULL, 1101, 'Initial inventory import', 20),
(1105, NOW(), 35, 1105, 'PURCHASE', NULL, 1102, 'Initial inventory import', 35),
(1106, NOW(), 60, 1106, 'PURCHASE', NULL, 1103, 'Initial inventory import', 60),
(1107, NOW(), 45, 1107, 'PURCHASE', NULL, 1101, 'Initial inventory import', 45),
(1108, NOW(), 30, 1108, 'PURCHASE', NULL, 1102, 'Initial inventory import', 30),
(1109, NOW(), 15, 1109, 'PURCHASE', NULL, 1103, 'Initial inventory import', 15),
(1110, NOW(), 50, 1110, 'PURCHASE', NULL, 1101, 'Initial inventory import', 50);

-- 12. Seed data for VOUCHER (ids 1101 to 1110)
INSERT INTO voucher (id, code, created_at, discount_type, discount_value, max_discount_value, min_order_value, status, updated_at, usage_limit, used_count, valid_from, valid_to, type, usage_count, value) VALUES
(1101, 'LUMI10', NOW(), 'PERCENT', 10.00, 15.00, 50.00, 'ACTIVE', NOW(), 100, 0, '2026-01-01 00:00:00', '2027-01-01 00:00:00', 'PERCENTAGE', 0, 10.00),
(1102, 'SUMMER20', NOW(), 'PERCENT', 20.00, 30.00, 100.00, 'ACTIVE', NOW(), 50, 0, '2026-06-01 00:00:00', '2026-09-01 00:00:00', 'PERCENTAGE', 0, 20.00),
(1103, 'FIXED5', NOW(), 'FIXED', 5.00, 5.00, 20.00, 'ACTIVE', NOW(), 200, 0, '2026-01-01 00:00:00', '2027-01-01 00:00:00', 'FIXED_AMOUNT', 0, 5.00),
(1104, 'WELCOME50', NOW(), 'FIXED', 50.00, 50.00, 300.00, 'ACTIVE', NOW(), 10, 0, '2026-01-01 00:00:00', '2027-01-01 00:00:00', 'FIXED_AMOUNT', 0, 50.00),
(1105, 'FALL15', NOW(), 'PERCENT', 15.00, 20.00, 60.00, 'ACTIVE', NOW(), 100, 0, '2026-09-01 00:00:00', '2026-12-01 00:00:00', 'PERCENTAGE', 0, 15.00),
(1106, 'WINTER25', NOW(), 'PERCENT', 25.00, 40.00, 120.00, 'ACTIVE', NOW(), 50, 0, '2026-12-01 00:00:00', '2027-03-01 00:00:00', 'PERCENTAGE', 0, 25.00),
(1107, 'VIP100', NOW(), 'FIXED', 100.00, 100.00, 500.00, 'ACTIVE', NOW(), 5, 0, '2026-01-01 00:00:00', '2027-01-01 00:00:00', 'FIXED_AMOUNT', 0, 100.00),
(1108, 'FREESHIP', NOW(), 'FIXED', 10.00, 10.00, 40.00, 'ACTIVE', NOW(), 500, 0, '2026-01-01 00:00:00', '2027-01-01 00:00:00', 'FIXED_AMOUNT', 0, 10.00),
(1109, 'SILVER5', NOW(), 'PERCENT', 5.00, 10.00, 40.00, 'ACTIVE', NOW(), 100, 0, '2026-01-01 00:00:00', '2027-01-01 00:00:00', 'PERCENTAGE', 0, 5.00),
(1110, 'GOLD12', NOW(), 'PERCENT', 12.00, 50.00, 100.00, 'ACTIVE', NOW(), 100, 0, '2026-01-01 00:00:00', '2027-01-01 00:00:00', 'PERCENTAGE', 0, 12.00);

-- 13. Seed data for ORDERS (ids 1101 to 1110)
INSERT INTO orders (id, code, currency, customer_id, fulfillment_status, note, payment_status, placed_at, status, total_amount, discount_amount, payment_method, redeemed_points, voucher_id, shipping_cost, shipping_info) VALUES
(1101, 'ORD-2026001', 'USD', 1101, 'UNFULFILLED', 'Leave at the front door', 'UNPAID', NOW(), 'PENDING', 120.00, 0.00, 'COD', 0, NULL, 5.00, '{"carrier":"VNPost","tracking_number":""}'),
(1102, 'ORD-2026002', 'USD', 1102, 'UNFULFILLED', 'Call before delivery', 'UNPAID', NOW(), 'PENDING', 25.00, 0.00, 'MOMO', 0, NULL, 3.00, '{"carrier":"GHTK","tracking_number":""}'),
(1103, 'ORD-2026003', 'USD', 1103, 'UNFULFILLED', '', 'PAID', NOW(), 'CONFIRMED', 65.00, 0.00, 'VNPAY', 10, NULL, 0.00, '{"carrier":"ViettelPost","tracking_number":""}'),
(1104, 'ORD-2026004', 'USD', 1104, 'UNFULFILLED', 'Deliver in office hours', 'UNPAID', NOW(), 'PENDING', 210.00, 10.00, 'COD', 0, 1101, 5.00, '{"carrier":"AhaMove","tracking_number":""}'),
(1105, 'ORD-2026005', 'USD', 1105, 'UNFULFILLED', '', 'PAID', NOW(), 'PENDING', 55.00, 0.00, 'PAYPAL', 0, NULL, 4.00, '{"carrier":"GHTK","tracking_number":""}'),
(1106, 'ORD-2026006', 'USD', 1106, 'UNFULFILLED', '', 'UNPAID', NOW(), 'DRAFT', 30.00, 0.00, 'COD', 0, NULL, 3.00, '{"carrier":"VNPost","tracking_number":""}'),
(1107, 'ORD-2026007', 'USD', 1107, 'UNFULFILLED', 'Fragile item, handle with care', 'UNPAID', NOW(), 'PENDING', 45.00, 0.00, 'MOMO', 0, NULL, 3.00, '{"carrier":"GHTK","tracking_number":""}'),
(1108, 'ORD-2026008', 'USD', 1108, 'UNFULFILLED', '', 'PAID', NOW(), 'COMPLETED', 38.00, 0.00, 'VNPAY', 0, NULL, 0.00, '{"carrier":"ViettelPost","tracking_number":"VT1234567"}'),
(1109, 'ORD-2026009', 'USD', 1109, 'UNFULFILLED', '', 'PAID', NOW(), 'CONFIRMED', 95.00, 0.00, 'VNPAY', 20, NULL, 0.00, '{"carrier":"GHTK","tracking_number":""}'),
(1110, 'ORD-2026010', 'USD', 1110, 'UNFULFILLED', '', 'UNPAID', NOW(), 'CANCELED', 50.00, 5.00, 'COD', 0, 1103, 5.00, '{"carrier":"VNPost","tracking_number":""}');

-- 14. Seed data for ORDER_ITEM (ids 1101 to 1110)
INSERT INTO order_item (id, name_snapshot, order_id, quantity, sku_snapshot, total_price, unit_price, product_variant_id) VALUES
(1101, 'Elegant Silk Dress - Red - M', 1101, 1, 'SKU-001-M-RED', 120.00, 120.00, 1101),
(1102, 'Casual Cotton T-Shirt - White - L', 1102, 1, 'SKU-002-L-WHT', 25.00, 25.00, 1102),
(1103, 'Slim Fit Denim Jeans - Blue - 32', 1103, 1, 'SKU-003-32-BLU', 65.00, 65.00, 1103),
(1104, 'Classic Leather Jacket - Black - XL', 1104, 1, 'SKU-004-XL-BLK', 210.00, 210.00, 1104),
(1105, 'Cozy Wool Sweater - Grey - S', 1105, 1, 'SKU-005-S-GRY', 55.00, 55.00, 1105),
(1106, 'Sporty Running Shorts - Black - M', 1106, 1, 'SKU-006-M-BLK', 30.00, 30.00, 1106),
(1107, 'Linen Button-Up Shirt - Light Blue - L', 1107, 1, 'SKU-007-L-BLU', 45.00, 45.00, 1107),
(1108, 'Chic Pleated Skirt - Pink - S', 1108, 1, 'SKU-008-S-PNK', 38.00, 38.00, 1108),
(1109, 'Formal Office Blazer - Navy - M', 1109, 1, 'SKU-009-M-NVY', 95.00, 95.00, 1109),
(1110, 'Active Fleece Hoodie - Grey - XL', 1110, 1, 'SKU-010-XL-GRY', 50.00, 50.00, 1110);

-- 15. Seed data for ORDER_STATUS_HISTORY (ids 1101 to 1110)
INSERT INTO order_status_history (id, description, status, timestamp, order_id) VALUES
(1101, 'Order created successfully.', 'PENDING', NOW(), 1101),
(1102, 'Order created successfully.', 'PENDING', NOW(), 1102),
(1103, 'Payment confirmed via VNPAY. Order moved to CONFIRMED.', 'CONFIRMED', NOW(), 1103),
(1104, 'Order created successfully.', 'PENDING', NOW(), 1104),
(1105, 'Order created successfully.', 'PENDING', NOW(), 1105),
(1106, 'Saved draft.', 'DRAFT', NOW(), 1106),
(1107, 'Order created successfully.', 'PENDING', NOW(), 1107),
(1108, 'Order delivered successfully to buyer.', 'COMPLETED', NOW(), 1108),
(1109, 'Payment confirmed. Order moved to CONFIRMED.', 'CONFIRMED', NOW(), 1109),
(1110, 'Customer requested cancellation.', 'CANCELLED', NOW(), 1110);

-- 16. Seed data for FLASH_SALE (ids 1101 to 1110)
INSERT INTO flash_sale (id, name, start_time, end_time) VALUES
(1101, 'Mid-Night Madness', NOW(), DATE_ADD(NOW(), INTERVAL 4 HOUR)),
(1102, 'Summer Kickoff Flash', NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR)),
(1103, 'Golden Hour Deal', NOW(), DATE_ADD(NOW(), INTERVAL 1 HOUR)),
(1104, 'Tech- Tuesday Fashion', NOW(), DATE_ADD(NOW(), INTERVAL 5 HOUR)),
(1105, 'Weekend Special Sale', NOW(), DATE_ADD(NOW(), INTERVAL 8 HOUR)),
(1106, 'Payday Treat', NOW(), DATE_ADD(NOW(), INTERVAL 12 HOUR)),
(1107, 'Holiday Quick Sale', NOW(), DATE_ADD(NOW(), INTERVAL 3 HOUR)),
(1108, 'Clearance Rush', NOW(), DATE_ADD(NOW(), INTERVAL 6 HOUR)),
(1109, 'Members-Only Sale', NOW(), DATE_ADD(NOW(), INTERVAL 2 HOUR)),
(1110, 'Final Countdown Deal', NOW(), DATE_ADD(NOW(), INTERVAL 1 HOUR));

-- 17. Seed data for FLASH_SALE_PRODUCT (ids 1101 to 1110)
INSERT INTO flash_sale_product (id, sale_price, quantity, sold, flash_sale_id, product_id, product_variant_id) VALUES
(1101, 80.00, 10, 2, 1101, 1101, 1101),
(1102, 18.00, 50, 15, 1102, 1102, 1102),
(1103, 45.00, 30, 8, 1103, 1103, 1103),
(1104, 150.00, 15, 1, 1104, 1104, 1104),
(1105, 38.00, 20, 5, 1105, 1105, 1105),
(1106, 20.00, 40, 12, 1106, 1106, 1106),
(1107, 30.00, 25, 4, 1107, 1107, 1107),
(1108, 25.00, 15, 6, 1108, 1108, 1108),
(1109, 70.00, 10, 3, 1109, 1109, 1109),
(1110, 35.00, 30, 10, 1110, 1110, 1110);

-- 18. Seed data for LOYALTY_TRANSACTION (ids 1101 to 1110)
INSERT INTO loyalty_transaction (id, created_at, description, points, type, customer_id) VALUES
(1101, NOW(), 'Points earned from ORD-2026001', 12, 'EARNED', 1101),
(1102, NOW(), 'Points earned from signup', 100, 'EARNED', 1102),
(1103, NOW(), 'Redeemed points on ORD-2026003', -10, 'REDEEMED', 1103),
(1104, NOW(), 'Bonus customer service adjustment', 50, 'ADJUSTMENT', 1104),
(1105, NOW(), 'Points earned from review', 15, 'EARNED', 1105),
(1106, NOW(), 'Points earned from ORD-2026006', 3, 'EARNED', 1106),
(1107, NOW(), 'Points earned from signup promo', 50, 'EARNED', 1107),
(1108, NOW(), 'Points earned from ORD-2026008', 38, 'EARNED', 1108),
(1109, NOW(), 'Redeemed points on ORD-2026009', -20, 'REDEEMED', 1109),
(1110, NOW(), 'Points earned from ORD-2026010 refund', 5, 'ADJUSTMENT', 1110);

-- 19. Seed data for PRODUCT_REVIEW (ids 1101 to 1110)
INSERT INTO product_review (id, author, comment, created_at, rating, status, product_id, reply) VALUES
(1101, 'John Doe', 'Excellent quality, fits perfectly!', NOW(), 'FIVE', 'APPROVED', 1101, 'Thank you for your feedback!'),
(1102, 'Jane Smith', 'Fabric is nice, but color is slightly different from picture.', NOW(), 'FOUR', 'APPROVED', 1101, 'We appreciate your input, we will adjust the photos.'),
(1103, 'Michael Johnson', 'Very comfortable for everyday wear.', NOW(), 'FOUR', 'APPROVED', 1102, 'Glad you like it!'),
(1104, 'Emily Brown', 'Perfect denim fit. Love the wash!', NOW(), 'FIVE', 'APPROVED', 1103, 'Awesome! Thanks for shopping with us.'),
(1105, 'David Davis', 'Real premium leather. Exceeded my expectations.', NOW(), 'FIVE', 'APPROVED', 1104, 'Enjoy your premium jacket!'),
(1106, 'Sarah Miller', 'Warm but a bit itchy.', NOW(), 'THREE', 'APPROVED', 1105, 'Sorry to hear that, wool can be sensitive.'),
(1107, 'James Wilson', 'Perfect for morning runs.', NOW(), 'FOUR', 'APPROVED', 1106, 'Keep up the good runs!'),
(1108, 'Lisa Moore', 'Lightweight linen, perfect for summer.', NOW(), 'FIVE', 'APPROVED', 1107, 'Stay cool this summer!'),
(1109, 'Robert Taylor', 'Beautiful pink color, very chic.', NOW(), 'FIVE', 'APPROVED', 1108, 'Thank you!'),
(1110, 'Patricia Anderson', 'Fits nicely, great professional look.', NOW(), 'FIVE', 'APPROVED', 1109, 'We are happy to help you look your best!');

-- 20. Seed data for PRODUCT_QUESTION (ids 1101 to 1110)
INSERT INTO product_question (id, author, created_at, question_text, status, product_id) VALUES
(1101, 'CuriousBuyer1', NOW(), 'Is this silk dry-clean only?', 'ANSWERED', 1101),
(1102, 'TshirtFan', NOW(), 'Does this shrink after washing?', 'ANSWERED', 1102),
(1103, 'JeansLover', NOW(), 'Are these stretch denim?', 'ANSWERED', 1103),
(1104, 'LeatherAddict', NOW(), 'Is the hood detachable?', 'ANSWERED', 1104),
(1105, 'ColdWinter', NOW(), 'What percentage of this is organic wool?', 'ANSWERED', 1105),
(1106, 'RunnerX', NOW(), 'Do these shorts have zip pockets?', 'ANSWERED', 1106),
(1107, 'LinenFan', NOW(), 'Is the linen see-through?', 'ANSWERED', 1107),
(1108, 'SkirtLover', NOW(), 'Does the skirt have lining?', 'ANSWERED', 1108),
(1109, 'OfficeStyle', NOW(), 'Does the blazer have shoulder pads?', 'ANSWERED', 1109),
(1110, 'WarmHoodie', NOW(), 'Is the fleece inside very thick?', 'PENDING', 1110);

-- 21. Seed data for PRODUCT_ANSWER (ids 1101 to 1110)
INSERT INTO product_answer (id, answer_text, author, created_at, question_id) VALUES
(1101, 'Yes, we recommend dry cleaning to preserve the quality of the silk.', 'Admin Team', NOW(), 1101),
(1102, 'It is pre-shrunk cotton, so shrinkage is minimal if washed in cold water.', 'Admin Team', NOW(), 1102),
(1103, 'It has 2% elastane for a comfortable stretch fit.', 'Admin Team', NOW(), 1103),
(1104, 'No, the hood is not detachable on this specific model.', 'Admin Team', NOW(), 1104),
(1105, 'It is made of 80% merino wool and 20% organic cotton.', 'Admin Team', NOW(), 1105),
(1106, 'Yes, there is one secure zipper pocket on the back.', 'Admin Team', NOW(), 1106),
(1107, 'It is lightweight, but not transparent under normal lighting.', 'Admin Team', NOW(), 1107),
(1108, 'Yes, it comes with a soft inner lining.', 'Admin Team', NOW(), 1108),
(1109, 'Yes, it has subtle shoulder padding for a structured professional look.', 'Admin Team', NOW(), 1109),
(1110, 'Yes, it is double-layered fleece, very warm for winter.', 'Admin Team', NOW(), 1109);

-- 22. Seed data for CONTACT_MESSAGE (ids 1101 to 1110)
INSERT INTO contact_message (id, admin_note, created_at, email, full_name, message, status, subject) VALUES
(1101, NULL, NOW(), 'user1@example.com', 'David Miller', 'Do you ship to Da Nang?', 'NEW', 'Shipping Inquiry'),
(1102, 'Informed about summer collection.', NOW(), 'user2@example.com', 'Emma Stone', 'When will the Summer Collection launch?', 'REPLIED', 'Collection Launch'),
(1103, NULL, NOW(), 'user3@example.com', 'James Bond', 'I want to partner as a supplier.', 'READ', 'Partnership Inquiry'),
(1104, NULL, NOW(), 'user4@example.com', 'Sophie Turner', 'Can I change my delivery address?', 'NEW', 'Order Edit Request'),
(1105, 'Refund processed.', NOW(), 'user5@example.com', 'Chris Evans', 'Requested refund status on order ORD-123.', 'REPLIED', 'Refund Status'),
(1106, NULL, NOW(), 'user6@example.com', 'Scarlett Johansson', 'Is there any store in Hanoi?', 'NEW', 'Store Location'),
(1107, NULL, NOW(), 'user7@example.com', 'Tom Hiddleston', 'Voucher is not applying.', 'NEW', 'Promo Code Issue'),
(1108, NULL, NOW(), 'user8@example.com', 'Mark Ruffalo', 'Bulk buying discounts?', 'READ', 'Wholesale Inquiry'),
(1109, 'Archived spam.', NOW(), 'spam@example.com', 'Spam Bot', 'Buy cheap things here.', 'ARCHIVED', 'Spam Message'),
(1110, NULL, NOW(), 'user10@example.com', 'Robert Downey', 'Loved your packaging, very eco-friendly!', 'NEW', 'Feedback');

-- 23. Seed data for NOTIFICATION (ids 1101 to 1110)
INSERT INTO notification (id, channel, created_at, customer_id, last_tried_at, payload, retry_count, send_status, subject, survey_id, ticket_id, type, is_read, link, message) VALUES
(1101, 'WEBHOOK', NOW(), 1101, NOW(), '{"order_id":1101}', 0, 'SENT', 'Order Placed', NULL, NULL, 'SYSTEM', 0, '/orders/1101', 'Your order ORD-2026001 has been placed successfully.'),
(1102, 'EMAIL', NOW(), 1102, NOW(), '{"order_id":1102}', 0, 'SENT', 'Order Placed', NULL, NULL, 'SYSTEM', 0, '/orders/1102', 'Your order ORD-2026002 has been placed successfully.'),
(1103, 'SMS', NOW(), 1103, NOW(), '{"order_id":1103}', 0, 'SENT', 'Payment Confirmed', NULL, NULL, 'SYSTEM', 1, '/orders/1103', 'Payment confirmed for order ORD-2026003.'),
(1104, 'WEBHOOK', NOW(), 1104, NOW(), '{"points":50}', 0, 'SENT', 'Loyalty Bonus', NULL, NULL, 'SYSTEM', 0, '/profile', 'You received 50 loyalty points!'),
(1105, 'EMAIL', NOW(), 1105, NOW(), '{"review_id":1105}', 0, 'SENT', 'Review Approved', NULL, NULL, 'SYSTEM', 0, '/products/1105', 'Your product review has been approved.'),
(1106, 'PUSH', NOW(), 1106, NOW(), '{"qa_id":1106}', 0, 'SENT', 'Question Answered', NULL, NULL, 'SYSTEM', 0, '/products/1106', 'Your question has been answered by admin.'),
(1107, 'SMS', NOW(), 1107, NOW(), '{"voucher_code":"WELCOME50"}', 0, 'SENT', 'New Voucher', NULL, NULL, 'SYSTEM', 0, '/promotions', 'Welcome! Use code WELCOME50 for your first purchase.'),
(1108, 'EMAIL', NOW(), 1108, NOW(), '{"order_id":1108}', 0, 'SENT', 'Order Delivered', NULL, NULL, 'SYSTEM', 1, '/orders/1108', 'Your order ORD-2026008 has been delivered.'),
(1109, 'PUSH', NOW(), 1109, NOW(), '{"order_id":1109}', 0, 'SENT', 'Order Confirmed', NULL, NULL, 'SYSTEM', 0, '/orders/1109', 'Your order ORD-2026009 is confirmed.'),
(1110, 'WEBHOOK', NOW(), 1110, NOW(), '{"order_id":1110}', 0, 'SENT', 'Order Canceled', NULL, NULL, 'SYSTEM', 0, '/orders/1110', 'Your order ORD-2026010 has been canceled.');

-- 24. Seed data for STOCK_NOTIFICATION (ids 1101 to 1110)
INSERT INTO stock_notification (id, created_at, email, notified, product_variant_id) VALUES
(1101, NOW(), 'buyer1@example.com', 0, 1101),
(1102, NOW(), 'buyer2@example.com', 0, 1102),
(1103, NOW(), 'buyer3@example.com', 0, 1103),
(1104, NOW(), 'buyer4@example.com', 0, 1104),
(1105, NOW(), 'buyer5@example.com', 0, 1105),
(1106, NOW(), 'buyer6@example.com', 0, 1106),
(1107, NOW(), 'buyer7@example.com', 0, 1107),
(1108, NOW(), 'buyer8@example.com', 0, 1108),
(1109, NOW(), 'buyer9@example.com', 0, 1109),
(1110, NOW(), 'buyer10@example.com', 0, 1110);

-- 25. Seed data for CHAT_SESSION (ids 1101 to 1110)
INSERT INTO chat_session (id, customer_id, created_at) VALUES
(1101, '1101', NOW()),
(1102, '1102', NOW()),
(1103, '1103', NOW()),
(1104, '1104', NOW()),
(1105, '1105', NOW()),
(1106, '1106', NOW()),
(1107, '1107', NOW()),
(1108, '1108', NOW()),
(1109, '1109', NOW()),
(1110, '1110', NOW());

-- 26. Seed data for CHAT_MESSAGE (ids 1101 to 1110)
INSERT INTO chat_message (id, sender, text, timestamp, session_id, contact_message_id) VALUES
(1101, 'CUSTOMER', 'Hello, is anyone online?', NOW(), 1101, NULL),
(1102, 'ADMIN', 'Hi there! How can we assist you today?', NOW(), 1101, NULL),
(1103, 'CUSTOMER', 'I need help sizing for a dress.', NOW(), 1102, NULL),
(1104, 'ADMIN', 'Sure! Please share your measurements.', NOW(), 1102, NULL),
(1105, 'CUSTOMER', 'My order is delayed.', NOW(), 1103, NULL),
(1106, 'ADMIN', 'Let me look up your order number.', NOW(), 1103, NULL),
(1107, 'CUSTOMER', 'Thanks for the quick response!', NOW(), 1104, NULL),
(1108, 'CUSTOMER', 'Are the linen shirts preshrunk?', NOW(), 1105, NULL),
(1109, 'ADMIN', 'Yes, they are pre-washed during manufacturing.', NOW(), 1105, NULL),
(1110, 'CUSTOMER', 'Awesome, placing an order now.', NOW(), 1106, NULL);

SET FOREIGN_KEY_CHECKS = 1;
