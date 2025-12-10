# Tài Liệu Dự Án Lumiere Fashion Store

## Mục Lục
1. [Tổng Quan Dự Án](#tổng-quan-dự-án)
2. [Kiến Trúc Hệ Thống](#kiến-trúc-hệ-thống)
3. [Cơ Sở Dữ Liệu - Các Entity và Mối Quan Hệ](#cơ-sở-dữ-liệu)
4. [Use Cases](#use-cases)
5. [Đặc Tả Kỹ Thuật](#đặc-tả-kỹ-thuật)
6. [Tương Tác Giữa Các Bảng](#tương-tác-giữa-các-bảng)
7. [API Endpoints](#api-endpoints)

---

## Tổng Quan Dự Án

### Giới Thiệu
**Lumiere Fashion Store** là một hệ thống thương mại điện tử (E-commerce) được xây dựng bằng Spring Boot (Java 17) và JHipster Framework. Hệ thống cung cấp đầy đủ các chức năng cho một cửa hàng thời trang trực tuyến, bao gồm quản lý sản phẩm, đơn hàng, khách hàng, kho hàng, khuyến mãi, và tương tác người dùng.

### Công Nghệ Sử Dụng
- **Backend Framework**: Spring Boot 3.4.5
- **Database**: MySQL 8
- **ORM**: Hibernate/JPA
- **Database Migration**: Liquibase
- **Security**: Spring Security với JWT
- **Build Tool**: Maven
- **Cache**: Caffeine
- **Message Queue**: Kafka (Spring Cloud Stream)
- **Search**: Elasticsearch
- **File Storage**: Google Cloud Storage
- **Push Notifications**: Firebase Admin SDK
- **API Documentation**: SpringDoc OpenAPI (Swagger)

---

## Kiến Trúc Hệ Thống

### Kiến Trúc Tổng Thể
Hệ thống được xây dựng theo kiến trúc **Layered Architecture** với các tầng:

```
┌─────────────────────────────────────┐
│      Presentation Layer              │
│  (REST Controllers / Resources)      │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Service Layer                   │
│  (Business Logic)                    │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Repository Layer                │
│  (Data Access)                      │
└─────────────────────────────────────┘
              ↓
┌─────────────────────────────────────┐
│      Database (MySQL)               │
└─────────────────────────────────────┘
```

### Cấu Trúc Package
```
com.lumiere.app
├── domain/          # Các entity JPA
├── repository/       # JPA Repositories
├── service/          # Business logic
│   ├── impl/         # Service implementations
│   ├── dto/          # Data Transfer Objects
│   └── mapper/       # MapStruct mappers
├── web/rest/         # REST Controllers
├── security/         # Security configuration
└── config/           # Configuration classes
```

---

## Cơ Sở Dữ Liệu

### Sơ Đồ ER (Entity Relationship)

Hệ thống bao gồm các nhóm entity chính:

#### 1. **Nhóm Quản Lý Sản Phẩm**
- `Product` - Sản phẩm
- `ProductVariant` - Biến thể sản phẩm (size, màu)
- `Category` - Danh mục sản phẩm
- `Collection` - Bộ sưu tập
- `ProductAttachment` - File đính kèm sản phẩm
- `OptionGroup`, `OptionSelect`, `OptionVariant` - Tùy chọn sản phẩm

#### 2. **Nhóm Quản Lý Kho Hàng**
- `Warehouse` - Kho hàng
- `Inventory` - Tồn kho (số lượng biến thể tại mỗi kho)
- `StockMovement` - Lịch sử thay đổi tồn kho
- `InventoryAdjustment` - Điều chỉnh tồn kho
- `StockNotification` - Thông báo hết hàng

#### 3. **Nhóm Quản Lý Đơn Hàng**
- `Orders` - Đơn hàng
- `OrderItem` - Chi tiết đơn hàng
- `OrderStatusHistory` - Lịch sử trạng thái đơn hàng
- `CartItem` - Giỏ hàng

#### 4. **Nhóm Quản Lý Khách Hàng**
- `User` - Tài khoản người dùng (JHipster)
- `Customer` - Hồ sơ khách hàng
- `Address` - Địa chỉ giao hàng
- `CustomerInfo` - Thông tin bổ sung khách hàng
- `WishlistItem` - Danh sách yêu thích

#### 5. **Nhóm Khuyến Mãi & Loyalty**
- `Voucher` - Mã giảm giá
- `CustomerVoucher` - Voucher của khách hàng
- `FlashSale` - Sự kiện Flash Sale
- `FlashSaleProduct` - Sản phẩm trong Flash Sale
- `LoyaltyTransaction` - Giao dịch điểm thưởng

#### 6. **Nhóm Tương Tác & Thông Báo**
- `ProductReview` - Đánh giá sản phẩm
- `ProductQuestion` - Câu hỏi về sản phẩm
- `ProductAnswer` - Trả lời câu hỏi
- `Notification` - Thông báo
- `ChatSession` - Phiên chat
- `ChatMessage` - Tin nhắn chat
- `ContactMessage` - Tin nhắn liên hệ

#### 7. **Nhóm Hệ Thống**
- `Authority` - Quyền truy cập
- `User` - Người dùng hệ thống
- `Attachment` - File đính kèm

---

### Chi Tiết Các Entity

#### 1. Product (Sản Phẩm)
**Bảng**: `product`

| Trường | Kiểu | Mô Tả |
|--------|------|-------|
| id | BIGINT | Primary Key |
| code | VARCHAR(64) | Mã sản phẩm (unique) |
| name | VARCHAR(200) | Tên sản phẩm |
| slug | VARCHAR | URL-friendly name (unique) |
| description | TEXT | Mô tả sản phẩm |
| status | ENUM | ACTIVE, INACTIVE, DRAFT |
| category | VARCHAR | Danh mục |
| material | VARCHAR | Chất liệu |
| average_rating | DOUBLE | Điểm đánh giá trung bình (0-5) |
| review_count | INTEGER | Số lượng đánh giá |
| images | TEXT | JSON array của URLs ảnh |
| category_id | BIGINT | Foreign key đến Category |
| created_at | TIMESTAMP | Ngày tạo |
| updated_at | TIMESTAMP | Ngày cập nhật |

**Mối quan hệ**:
- `OneToMany` với `ProductVariant` (variants)
- `OneToMany` với `ProductReview` (reviews)
- `OneToMany` với `ProductQuestion` (questions)
- `ManyToMany` với `Collection` (collections)
- `ManyToMany` với `Customer` (wishlistedBies)
- `OneToMany` với `ProductAttachment` (productAttachments)

#### 2. ProductVariant (Biến Thể Sản Phẩm)
**Bảng**: `product_variant`

| Trường | Kiểu | Mô Tả |
|--------|------|-------|
| id | BIGINT | Primary Key |
| sku | VARCHAR(64) | Mã SKU (unique) |
| name | VARCHAR(255) | Tên biến thể |
| price | DECIMAL(21,2) | Giá bán |
| compare_at_price | DECIMAL(21,2) | Giá so sánh |
| currency | VARCHAR(3) | Loại tiền tệ |
| stock_quantity | BIGINT | Tổng số lượng tồn kho |
| is_default | BOOLEAN | Biến thể mặc định |
| color | VARCHAR | Màu sắc |
| size | VARCHAR | Kích thước |
| url_image | VARCHAR(1000) | URL ảnh |
| product_id | BIGINT | Foreign key đến Product |

**Mối quan hệ**:
- `ManyToOne` với `Product` (product)
- `OneToMany` với `Inventory` (inventories)
- `OneToMany` với `OrderItem` (orderItems)
- `OneToMany` với `StockMovement` (stockMovements)
- `OneToMany` với `StockNotification` (stockNotifications)

#### 3. Orders (Đơn Hàng)
**Bảng**: `orders`

| Trường | Kiểu | Mô Tả |
|--------|------|-------|
| id | BIGINT | Primary Key |
| code | VARCHAR | Mã đơn hàng (unique) |
| status | ENUM | PENDING, CONFIRMED, PROCESSING, SHIPPING, DELIVERED, COMPLETED, CANCELLED, DRAFT |
| payment_status | ENUM | UNPAID, PAID, REFUNDED |
| total_amount | DECIMAL(21,2) | Tổng tiền |
| discount_amount | DECIMAL(21,2) | Số tiền giảm giá |
| note | VARCHAR(500) | Ghi chú |
| payment_method | VARCHAR | Phương thức thanh toán |
| placed_at | TIMESTAMP | Thời gian đặt hàng |
| redeemed_points | INTEGER | Điểm thưởng đã sử dụng |
| customer_id | BIGINT | Foreign key đến Customer |
| voucher_id | BIGINT | Foreign key đến Voucher |

**Mối quan hệ**:
- `ManyToOne` với `Customer` (customer)
- `ManyToOne` với `Voucher` (voucher)
- `OneToMany` với `OrderItem` (orderItems)
- `OneToMany` với `OrderStatusHistory` (orderStatusHistories)

#### 4. Customer (Khách Hàng)
**Bảng**: `customer`

| Trường | Kiểu | Mô Tả |
|--------|------|-------|
| id | BIGINT | Primary Key |
| first_name | VARCHAR | Tên |
| last_name | VARCHAR | Họ |
| phone | VARCHAR | Số điện thoại |
| tier | ENUM | BRONZE, SILVER, GOLD |
| loyalty_points | INTEGER | Điểm thưởng |
| birthday | DATE | Ngày sinh |
| user_id | BIGINT | Foreign key đến User (unique) |

**Mối quan hệ**:
- `OneToOne` với `User` (user)
- `OneToMany` với `Orders` (orders)
- `OneToMany` với `Address` (addresses)
- `OneToMany` với `LoyaltyTransaction` (loyaltyHistories)
- `OneToMany` với `Notification` (notifications)
- `ManyToMany` với `Product` (wishlists)

#### 5. Inventory (Tồn Kho)
**Bảng**: `inventory`

| Trường | Kiểu | Mô Tả |
|--------|------|-------|
| id | BIGINT | Primary Key |
| stock_quantity | BIGINT | Số lượng tồn kho |
| product_variant_id | BIGINT | Foreign key đến ProductVariant |
| warehouse_id | BIGINT | Foreign key đến Warehouse |

**Mối quan hệ**:
- `ManyToOne` với `ProductVariant` (productVariant)
- `ManyToOne` với `Warehouse` (warehouse)

#### 6. Voucher (Mã Giảm Giá)
**Bảng**: `voucher`

| Trường | Kiểu | Mô Tả |
|--------|------|-------|
| id | BIGINT | Primary Key |
| code | VARCHAR | Mã voucher (unique) |
| type | ENUM | PERCENTAGE, FIXED_AMOUNT |
| value | DECIMAL(21,2) | Giá trị giảm giá |
| status | ENUM | ACTIVE, INACTIVE, EXPIRED |
| start_date | TIMESTAMP | Ngày bắt đầu |
| end_date | TIMESTAMP | Ngày kết thúc |
| usage_limit | INTEGER | Giới hạn sử dụng |
| usage_count | INTEGER | Số lần đã sử dụng |

**Mối quan hệ**:
- `OneToMany` với `Orders` (orders)
- `OneToMany` với `CustomerVoucher` (customerVouchers)

---

## Use Cases

### 1. Quản Lý Sản Phẩm

#### UC-1.1: Tạo Sản Phẩm Mới
**Actor**: Admin
**Mô tả**: Admin tạo sản phẩm mới với thông tin cơ bản

**Luồng xử lý**:
1. Admin điền thông tin sản phẩm (tên, mô tả, danh mục, hình ảnh)
2. Hệ thống tạo slug tự động từ tên
3. Tạo Product với status = DRAFT
4. Admin có thể thêm ProductVariant sau

**Dữ liệu liên quan**:
- `Product` → tạo mới
- `ProductAttachment` → upload ảnh
- `Category` → liên kết danh mục

#### UC-1.2: Thêm Biến Thể Sản Phẩm
**Actor**: Admin
**Mô tả**: Thêm các biến thể (size, màu) cho sản phẩm

**Luồng xử lý**:
1. Chọn Product
2. Tạo ProductVariant với SKU, giá, size, màu
3. Tự động tạo Inventory record cho mỗi Warehouse
4. Cập nhật stock_quantity tổng trong ProductVariant

**Dữ liệu liên quan**:
- `ProductVariant` → tạo mới
- `Inventory` → tạo cho mỗi Warehouse
- `ProductVariant.stockQuantity` → tổng từ các Inventory

#### UC-1.3: Quản Lý Tồn Kho
**Actor**: Admin
**Mô tả**: Cập nhật số lượng tồn kho tại các kho

**Luồng xử lý**:
1. Chọn ProductVariant và Warehouse
2. Cập nhật Inventory.stockQuantity
3. Tạo StockMovement record để ghi lại thay đổi
4. Cập nhật ProductVariant.stockQuantity (tổng từ tất cả Inventory)

**Dữ liệu liên quan**:
- `Inventory` → cập nhật
- `StockMovement` → tạo mới (reason = ADJUSTMENT)
- `ProductVariant.stockQuantity` → tính lại

### 2. Quản Lý Đơn Hàng

#### UC-2.1: Tạo Đơn Hàng
**Actor**: Customer
**Mô tả**: Khách hàng tạo đơn hàng từ giỏ hàng

**Luồng xử lý**:
1. Customer chọn sản phẩm từ CartItem
2. Chọn địa chỉ giao hàng (Address)
3. Áp dụng Voucher (nếu có)
4. Sử dụng điểm thưởng (nếu có)
5. Tính tổng tiền (totalAmount)
6. Tạo Orders với status = PENDING
7. Tạo OrderItem cho mỗi sản phẩm
8. Tạo OrderStatusHistory đầu tiên
9. Trừ số lượng tồn kho (Inventory)
10. Tạo StockMovement (reason = SALE)
11. Nếu dùng điểm thưởng: tạo LoyaltyTransaction (type = REDEEMED)
12. Nếu dùng voucher: tăng Voucher.usageCount

**Dữ liệu liên quan**:
- `Orders` → tạo mới
- `OrderItem` → tạo nhiều records
- `OrderStatusHistory` → tạo mới
- `Inventory` → giảm stockQuantity
- `StockMovement` → tạo mới
- `LoyaltyTransaction` → tạo nếu dùng điểm
- `Voucher` → cập nhật usageCount
- `CartItem` → xóa sau khi tạo đơn

#### UC-2.2: Cập Nhật Trạng Thái Đơn Hàng
**Actor**: Admin
**Mô tả**: Admin cập nhật trạng thái đơn hàng

**Luồng xử lý**:
1. Admin chọn Orders
2. Cập nhật Orders.status
3. Tạo OrderStatusHistory mới
4. Nếu status = DELIVERED:
   - Cập nhật payment_status = PAID (nếu chưa)
   - Tính điểm thưởng (ví dụ: 1% tổng tiền)
   - Tạo LoyaltyTransaction (type = EARNED)
   - Cập nhật Customer.loyaltyPoints
   - Kiểm tra nâng hạng Customer.tier

**Dữ liệu liên quan**:
- `Orders.status` → cập nhật
- `OrderStatusHistory` → tạo mới
- `LoyaltyTransaction` → tạo nếu đã giao
- `Customer.loyaltyPoints` → cập nhật
- `Customer.tier` → có thể nâng hạng

#### UC-2.3: Hủy Đơn Hàng
**Actor**: Customer/Admin
**Mô tả**: Hủy đơn hàng và hoàn trả tồn kho

**Luồng xử lý**:
1. Cập nhật Orders.status = CANCELLED
2. Tạo OrderStatusHistory
3. Hoàn trả số lượng tồn kho:
   - Với mỗi OrderItem:
     - Tăng Inventory.stockQuantity
     - Tạo StockMovement (reason = RETURN)
4. Nếu đã dùng điểm: hoàn trả điểm
   - Tạo LoyaltyTransaction (type = ADJUSTMENT)
   - Tăng Customer.loyaltyPoints
5. Nếu đã thanh toán: cập nhật payment_status = REFUNDED

**Dữ liệu liên quan**:
- `Orders.status` → CANCELLED
- `OrderStatusHistory` → tạo mới
- `Inventory` → tăng stockQuantity
- `StockMovement` → tạo (reason = RETURN)
- `LoyaltyTransaction` → hoàn trả điểm
- `Customer.loyaltyPoints` → cập nhật

### 3. Quản Lý Khách Hàng

#### UC-3.1: Đăng Ký Tài Khoản
**Actor**: Customer
**Mô tả**: Khách hàng đăng ký tài khoản mới

**Luồng xử lý**:
1. Customer điền thông tin (email, password, tên, số điện thoại)
2. Hệ thống tạo User (JHipster)
3. Tự động tạo Customer record liên kết với User
4. Gửi email xác nhận
5. Customer.tier = BRONZE (mặc định)
6. Customer.loyaltyPoints = 0

**Dữ liệu liên quan**:
- `User` → tạo mới
- `Customer` → tạo mới (OneToOne với User)

#### UC-3.2: Thêm Sản Phẩm Vào Wishlist
**Actor**: Customer
**Mô tả**: Khách hàng thêm sản phẩm vào danh sách yêu thích

**Luồng xử lý**:
1. Customer chọn Product
2. Thêm vào quan hệ ManyToMany: Customer.wishlists ↔ Product.wishlistedBies
3. Tạo WishlistItem record (nếu dùng bảng trung gian)

**Dữ liệu liên quan**:
- `WishlistItem` → tạo mới
- Hoặc cập nhật bảng trung gian `rel_customer__wishlist`

### 4. Quản Lý Khuyến Mãi

#### UC-4.1: Tạo Flash Sale
**Actor**: Admin
**Mô tả**: Tạo sự kiện Flash Sale với các sản phẩm giảm giá

**Luồng xử lý**:
1. Admin tạo FlashSale (tên, thời gian bắt đầu/kết thúc)
2. Thêm FlashSaleProduct cho mỗi sản phẩm:
   - Chọn Product
   - Nhập salePrice (giá khuyến mãi)
   - Nhập quantity (số lượng bán)
   - sold = 0 (ban đầu)
3. Gửi Notification cho Customer (type = PROMOTION)

**Dữ liệu liên quan**:
- `FlashSale` → tạo mới
- `FlashSaleProduct` → tạo nhiều records
- `Notification` → gửi cho khách hàng

#### UC-4.2: Áp Dụng Voucher
**Actor**: Customer
**Mô tả**: Khách hàng sử dụng voucher khi đặt hàng

**Luồng xử lý**:
1. Customer nhập voucher code
2. Kiểm tra Voucher:
   - status = ACTIVE
   - startDate <= now <= endDate
   - usageCount < usageLimit (nếu có)
3. Tính discountAmount:
   - Nếu type = PERCENTAGE: discountAmount = totalAmount * value / 100
   - Nếu type = FIXED_AMOUNT: discountAmount = value
4. Cập nhật Orders.discountAmount
5. Liên kết Orders.voucher
6. Tăng Voucher.usageCount
7. Tạo CustomerVoucher (để theo dõi)

**Dữ liệu liên quan**:
- `Orders.voucher` → liên kết
- `Orders.discountAmount` → cập nhật
- `Voucher.usageCount` → tăng
- `CustomerVoucher` → tạo mới

### 5. Tương Tác Sản Phẩm

#### UC-5.1: Đánh Giá Sản Phẩm
**Actor**: Customer
**Mô tả**: Khách hàng đánh giá sản phẩm sau khi mua

**Luồng xử lý**:
1. Customer chọn Product (đã mua)
2. Tạo ProductReview:
   - rating (ONE đến FIVE)
   - comment
   - author = Customer.firstName + lastName
   - status = PENDING (chờ duyệt)
3. Admin duyệt: status = APPROVED
4. Cập nhật Product:
   - Tính lại averageRating
   - Tăng reviewCount

**Dữ liệu liên quan**:
- `ProductReview` → tạo mới
- `Product.averageRating` → tính lại
- `Product.reviewCount` → tăng

#### UC-5.2: Đặt Câu Hỏi Về Sản Phẩm
**Actor**: Customer
**Mô tả**: Khách hàng đặt câu hỏi về sản phẩm

**Luồng xử lý**:
1. Customer chọn Product
2. Tạo ProductQuestion:
   - questionText
   - author = Customer name
   - status = PENDING
3. Admin/User khác trả lời: tạo ProductAnswer
4. Cập nhật ProductQuestion.status = ANSWERED
5. Gửi Notification cho người hỏi (type = NEW_ANSWER)

**Dữ liệu liên quan**:
- `ProductQuestion` → tạo mới
- `ProductAnswer` → tạo mới
- `Notification` → gửi thông báo

### 6. Quản Lý Kho Hàng

#### UC-6.1: Nhập Kho
**Actor**: Admin
**Mô tả**: Nhập hàng vào kho

**Luồng xử lý**:
1. Chọn Warehouse và ProductVariant
2. Nhập số lượng nhập
3. Tăng Inventory.stockQuantity
4. Tạo StockMovement:
   - quantityChange = số lượng nhập (dương)
   - reason = INITIAL_STOCK hoặc ADJUSTMENT
5. Cập nhật ProductVariant.stockQuantity (tổng từ tất cả Inventory)

**Dữ liệu liên quan**:
- `Inventory` → tăng stockQuantity
- `StockMovement` → tạo mới
- `ProductVariant.stockQuantity` → cập nhật

#### UC-6.2: Đăng Ký Thông Báo Hết Hàng
**Actor**: Customer
**Mô tả**: Khách hàng đăng ký nhận thông báo khi có hàng

**Luồng xử lý**:
1. Customer chọn ProductVariant (đang hết hàng)
2. Tạo StockNotification:
   - email = Customer email
   - notified = false
3. Khi có hàng mới:
   - Kiểm tra StockNotification với notified = false
   - Gửi email thông báo
   - Cập nhật notified = true

**Dữ liệu liên quan**:
- `StockNotification` → tạo mới
- `StockNotification.notified` → cập nhật khi có hàng

---

## Đặc Tả Kỹ Thuật

### 1. Database Schema

#### Naming Convention
- Tên bảng: snake_case (ví dụ: `product`, `order_item`)
- Tên cột: snake_case (ví dụ: `created_at`, `product_id`)
- Foreign keys: `{entity_name}_id` (ví dụ: `customer_id`, `product_id`)

#### Constraints
- **Primary Keys**: Tất cả bảng đều có `id BIGINT AUTO_INCREMENT`
- **Foreign Keys**: Có ON DELETE và ON UPDATE rules
- **Unique Constraints**: 
  - `Product.code` - unique
  - `Product.slug` - unique
  - `ProductVariant.sku` - unique
  - `Orders.code` - unique
  - `Voucher.code` - unique
  - `User.login` - unique (JHipster)

#### Indexes
- Index trên các foreign keys
- Index trên các trường tìm kiếm thường xuyên:
  - `Product.name`
  - `Product.code`
  - `Product.slug`
  - `Orders.code`
  - `Customer.phone`

### 2. Business Rules

#### 2.1. Quản Lý Tồn Kho
- **Rule 1**: `ProductVariant.stockQuantity` = tổng của tất cả `Inventory.stockQuantity` cho variant đó
- **Rule 2**: Khi tạo đơn hàng, phải kiểm tra tồn kho đủ
- **Rule 3**: Mỗi thay đổi tồn kho phải có `StockMovement` record

#### 2.2. Quản Lý Đơn Hàng
- **Rule 1**: Đơn hàng chỉ có thể hủy khi status = PENDING hoặc CONFIRMED
- **Rule 2**: Khi đơn hàng được giao (DELIVERED), tự động tính điểm thưởng
- **Rule 3**: Mỗi thay đổi status phải có `OrderStatusHistory` record

#### 2.3. Quản Lý Điểm Thưởng
- **Rule 1**: Điểm thưởng chỉ có thể sử dụng khi `Customer.loyaltyPoints >= redeemedPoints`
- **Rule 2**: Mỗi giao dịch điểm phải có `LoyaltyTransaction` record
- **Rule 3**: Nâng hạng tự động dựa trên tổng điểm tích lũy:
  - BRONZE: 0-999 điểm
  - SILVER: 1000-4999 điểm
  - GOLD: >= 5000 điểm

#### 2.4. Quản Lý Voucher
- **Rule 1**: Voucher chỉ áp dụng được khi:
  - status = ACTIVE
  - startDate <= now <= endDate
  - usageCount < usageLimit (nếu có)
- **Rule 2**: Mỗi lần sử dụng voucher, tăng `usageCount`
- **Rule 3**: Khi `usageCount >= usageLimit`, tự động set status = INACTIVE

### 3. API Design

#### RESTful Conventions
- **GET** `/api/products` - Lấy danh sách sản phẩm
- **GET** `/api/products/{id}` - Lấy chi tiết sản phẩm
- **POST** `/api/products` - Tạo sản phẩm mới
- **PUT** `/api/products/{id}` - Cập nhật sản phẩm
- **DELETE** `/api/products/{id}` - Xóa sản phẩm

#### Pagination
- Tất cả API list đều hỗ trợ pagination:
  - `page`: số trang (bắt đầu từ 0)
  - `size`: số lượng mỗi trang
  - Response bao gồm: `content`, `totalElements`, `totalPages`

#### Filtering
- Các entity chính hỗ trợ filtering:
  - `Product`: filter theo name, code, status, category
  - `Orders`: filter theo status, paymentStatus, customer
  - `Customer`: filter theo firstName, lastName, phone, tier

### 4. Security

#### Authentication
- Sử dụng JWT (JSON Web Token)
- Token được lưu trong HTTP header: `Authorization: Bearer {token}`
- Token có thời gian hết hạn

#### Authorization
- Role-based access control (RBAC)
- Các role chính:
  - `ROLE_ADMIN`: Quản trị viên
  - `ROLE_USER`: Người dùng thường
  - `ROLE_CUSTOMER`: Khách hàng

### 5. Caching Strategy

#### Entity Cache
- Sử dụng Caffeine cache cho các entity thường đọc:
  - `Product`
  - `ProductVariant`
  - `Category`
  - `Voucher`
- Cache strategy: `READ_WRITE`

#### Query Cache
- Cache các query thường dùng:
  - Danh sách sản phẩm nổi bật
  - Danh sách danh mục
  - Thông tin khách hàng

---

## Tương Tác Giữa Các Bảng

### 1. Luồng Tạo Đơn Hàng (Order Flow)

```
Customer
  ↓ (1:Many)
Orders
  ├─→ OrderItem (1:Many)
  │     └─→ ProductVariant (Many:1)
  │           └─→ Product (Many:1)
  ├─→ OrderStatusHistory (1:Many)
  ├─→ Voucher (Many:1) [optional]
  └─→ Address (Many:1) [via Customer]

Khi tạo đơn hàng:
1. Orders được tạo với customer_id
2. OrderItem được tạo với order_id và product_variant_id
3. Inventory.stockQuantity giảm (cho mỗi OrderItem)
4. StockMovement được tạo (reason = SALE)
5. Nếu dùng voucher: Orders.voucher_id được set, Voucher.usageCount tăng
6. Nếu dùng điểm: LoyaltyTransaction được tạo (type = REDEEMED)
```

### 2. Luồng Quản Lý Tồn Kho (Inventory Flow)

```
Product
  ↓ (1:Many)
ProductVariant
  ├─→ Inventory (1:Many)
  │     └─→ Warehouse (Many:1)
  └─→ StockMovement (1:Many)
        └─→ Warehouse (Many:1)

Quan hệ:
- ProductVariant.stockQuantity = SUM(Inventory.stockQuantity) cho variant đó
- Mỗi thay đổi tồn kho tạo StockMovement record
- Inventory là tồn kho chi tiết tại từng kho
- ProductVariant.stockQuantity là tổng tồn kho
```

### 3. Luồng Đánh Giá Sản Phẩm (Review Flow)

```
Product
  ↓ (1:Many)
ProductReview
  └─→ Customer (Many:1) [via author field]

Khi đánh giá:
1. ProductReview được tạo với product_id
2. Product.averageRating được tính lại: 
   averageRating = SUM(rating) / COUNT(reviews)
3. Product.reviewCount tăng
```

### 4. Luồng Wishlist

```
Customer ←→ (Many:Many) ←→ Product
         (via rel_customer__wishlist)

Hoặc sử dụng bảng trung gian:
WishlistItem
  ├─→ Customer (Many:1)
  └─→ Product (Many:1)
```

### 5. Luồng Flash Sale

```
FlashSale
  ↓ (1:Many)
FlashSaleProduct
  ├─→ Product (Many:1)
  └─→ FlashSale (Many:1)

Khi khách hàng mua trong Flash Sale:
1. Tạo Orders như bình thường
2. Tăng FlashSaleProduct.sold
3. Kiểm tra FlashSaleProduct.sold < quantity
```

### 6. Luồng Loyalty Points

```
Customer
  ↓ (1:Many)
LoyaltyTransaction
  └─→ Customer (Many:1)

Khi tích điểm:
1. Orders được giao (DELIVERED)
2. Tính điểm: points = totalAmount * rate (ví dụ: 1%)
3. Tạo LoyaltyTransaction (type = EARNED)
4. Cập nhật Customer.loyaltyPoints += points
5. Kiểm tra nâng hạng Customer.tier

Khi sử dụng điểm:
1. Orders.redeemedPoints được set
2. Tạo LoyaltyTransaction (type = REDEEMED)
3. Cập nhật Customer.loyaltyPoints -= redeemedPoints
```

### 7. Luồng Câu Hỏi & Trả Lời (Q&A)

```
Product
  ↓ (1:Many)
ProductQuestion
  ↓ (1:Many)
ProductAnswer

Khi đặt câu hỏi:
1. ProductQuestion được tạo với product_id
2. status = PENDING

Khi trả lời:
1. ProductAnswer được tạo với question_id
2. ProductQuestion.status = ANSWERED
3. Notification được gửi cho người hỏi
```

### 8. Sơ Đồ Tương Tác Tổng Quan

```
┌─────────────┐
│   User      │
└──────┬──────┘
       │ 1:1
┌──────▼──────┐
│  Customer   │
└──────┬──────┘
       │
       ├─→ Orders (1:Many)
       │     ├─→ OrderItem (1:Many) → ProductVariant → Product
       │     ├─→ OrderStatusHistory (1:Many)
       │     └─→ Voucher (Many:1)
       │
       ├─→ Address (1:Many)
       ├─→ LoyaltyTransaction (1:Many)
       ├─→ Notification (1:Many)
       └─→ Wishlist (Many:Many) → Product

Product
  ├─→ ProductVariant (1:Many)
  │     ├─→ Inventory (1:Many) → Warehouse
  │     ├─→ StockMovement (1:Many) → Warehouse
  │     └─→ OrderItem (1:Many)
  │
  ├─→ ProductReview (1:Many)
  ├─→ ProductQuestion (1:Many)
  │     └─→ ProductAnswer (1:Many)
  └─→ Collection (Many:Many)
```

---

## API Endpoints

### Product APIs
- `GET /api/products` - Danh sách sản phẩm (có pagination, filter)
- `GET /api/products/{id}` - Chi tiết sản phẩm
- `POST /api/products` - Tạo sản phẩm mới
- `PUT /api/products/{id}` - Cập nhật sản phẩm
- `DELETE /api/products/{id}` - Xóa sản phẩm
- `GET /api/products/{id}/variants` - Lấy danh sách biến thể

### Order APIs
- `GET /api/orders` - Danh sách đơn hàng
- `GET /api/orders/{id}` - Chi tiết đơn hàng
- `POST /api/orders` - Tạo đơn hàng mới
- `PUT /api/orders/{id}` - Cập nhật đơn hàng
- `PUT /api/orders/{id}/status` - Cập nhật trạng thái đơn hàng
- `POST /api/orders/{id}/cancel` - Hủy đơn hàng

### Customer APIs
- `GET /api/customers` - Danh sách khách hàng
- `GET /api/customers/{id}` - Chi tiết khách hàng
- `GET /api/customers/{id}/orders` - Đơn hàng của khách hàng
- `GET /api/customers/{id}/wishlist` - Danh sách yêu thích
- `POST /api/customers/{id}/wishlist/{productId}` - Thêm vào wishlist

### Inventory APIs
- `GET /api/inventories` - Danh sách tồn kho
- `GET /api/inventories/{id}` - Chi tiết tồn kho
- `PUT /api/inventories/{id}` - Cập nhật tồn kho
- `POST /api/inventories/adjust` - Điều chỉnh tồn kho

### Voucher APIs
- `GET /api/vouchers` - Danh sách voucher
- `GET /api/vouchers/{id}` - Chi tiết voucher
- `POST /api/vouchers` - Tạo voucher mới
- `POST /api/vouchers/validate` - Kiểm tra voucher hợp lệ

### Review APIs
- `GET /api/products/{id}/reviews` - Đánh giá sản phẩm
- `POST /api/products/{id}/reviews` - Tạo đánh giá
- `PUT /api/reviews/{id}/approve` - Duyệt đánh giá

---

## Kết Luận

Tài liệu này mô tả toàn bộ hệ thống Lumiere Fashion Store, bao gồm:
- Kiến trúc và cấu trúc dự án
- Cơ sở dữ liệu và mối quan hệ giữa các bảng
- Các use case chính
- Đặc tả kỹ thuật
- Cách tương tác giữa các bảng
- API endpoints

Để biết thêm chi tiết, vui lòng tham khảo:
- Source code trong thư mục `src/main/java`
- JDL file: `lumiere.jdl`
- Liquibase changelogs: `src/main/resources/config/liquibase/changelog/`





