# Sơ Đồ Cơ Sở Dữ Liệu - Lumiere Fashion Store

## Sơ Đồ ER (Entity Relationship Diagram)

### 1. Nhóm Quản Lý Sản Phẩm

```
┌─────────────────────────────────────────────────────────────┐
│                        PRODUCT                               │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ code (UNIQUE)                                                │
│ name                                                         │
│ slug (UNIQUE)                                                │
│ description                                                  │
│ status (ENUM: ACTIVE, INACTIVE, DRAFT)                      │
│ category                                                     │
│ material                                                     │
│ average_rating                                               │
│ review_count                                                 │
│ images                                                       │
│ category_id (FK → Category)                                 │
│ created_at                                                   │
│ updated_at                                                   │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:Many
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│                    PRODUCT_VARIANT                           │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ sku (UNIQUE)                                                 │
│ name                                                         │
│ price                                                        │
│ compare_at_price                                             │
│ currency                                                     │
│ stock_quantity (TỔNG từ Inventory)                           │
│ is_default                                                   │
│ color                                                        │
│ size                                                         │
│ url_image                                                    │
│ product_id (FK → Product)                                    │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:Many
         │
         ├─────────────────┐
         │                 │
         ▼                 ▼
┌──────────────┐  ┌──────────────────────┐
│  INVENTORY   │  │   ORDER_ITEM         │
│              │  │                      │
│ id (PK)      │  │ id (PK)              │
│ stock_qty    │  │ quantity             │
│ variant_id   │  │ unit_price           │
│ warehouse_id │  │ total_price          │
└──────────────┘  │ order_id (FK)        │
                  │ variant_id (FK)       │
                  └──────────────────────┘
```

### 2. Nhóm Quản Lý Đơn Hàng

```
┌─────────────────────────────────────────────────────────────┐
│                        ORDERS                                │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ code (UNIQUE)                                                │
│ status (ENUM)                                                │
│ payment_status (ENUM)                                        │
│ total_amount                                                 │
│ discount_amount                                              │
│ note                                                         │
│ payment_method                                               │
│ placed_at                                                    │
│ redeemed_points                                              │
│ customer_id (FK → Customer)                                  │
│ voucher_id (FK → Voucher, nullable)                          │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:Many
         │
         ├──────────────────────┐
         │                      │
         ▼                      ▼
┌──────────────────────┐  ┌──────────────────────────────┐
│    ORDER_ITEM         │  │   ORDER_STATUS_HISTORY       │
│                      │  │                              │
│ id (PK)              │  │ id (PK)                      │
│ quantity             │  │ status                       │
│ unit_price           │  │ description                  │
│ total_price          │  │ timestamp                    │
│ order_id (FK)        │  │ order_id (FK)                │
│ variant_id (FK)      │  └──────────────────────────────┘
└──────────────────────┘
```

### 3. Nhóm Quản Lý Khách Hàng

```
┌─────────────────────────────────────────────────────────────┐
│                         USER                                 │
│  (JHipster built-in entity)                                  │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ login (UNIQUE)                                               │
│ email                                                        │
│ password_hash                                                │
│ ...                                                          │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:1
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│                       CUSTOMER                               │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ first_name                                                   │
│ last_name                                                    │
│ phone                                                        │
│ tier (ENUM: BRONZE, SILVER, GOLD)                           │
│ loyalty_points                                               │
│ birthday                                                     │
│ user_id (FK → User, UNIQUE)                                  │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:Many
         │
         ├─────────────────────────────────────┐
         │                                     │
         ▼                                     ▼
┌──────────────────────┐  ┌──────────────────────────────┐
│      ADDRESS          │  │   LOYALTY_TRANSACTION       │
│                      │  │                              │
│ id (PK)              │  │ id (PK)                      │
│ full_name            │  │ type (ENUM)                   │
│ phone                │  │ points                       │
│ street               │  │ description                   │
│ city                 │  │ created_at                   │
│ is_default           │  │ customer_id (FK)              │
│ customer_id (FK)     │  └──────────────────────────────┘
└──────────────────────┘
```

### 4. Nhóm Quản Lý Kho Hàng

```
┌─────────────────────────────────────────────────────────────┐
│                      WAREHOUSE                               │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ name (UNIQUE)                                                │
│ address                                                      │
│ is_active                                                    │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:Many
         │
         ├──────────────────────┐
         │                      │
         ▼                      ▼
┌──────────────────────┐  ┌──────────────────────────────┐
│     INVENTORY         │  │    STOCK_MOVEMENT           │
│                      │  │                              │
│ id (PK)              │  │ id (PK)                      │
│ stock_quantity       │  │ quantity_change              │
│ variant_id (FK)      │  │ note                         │
│ warehouse_id (FK)    │  │ reason (ENUM)                │
└──────────────────────┘  │ created_at                   │
                          │ variant_id (FK)               │
                          │ warehouse_id (FK)             │
                          └──────────────────────────────┘
```

### 5. Nhóm Khuyến Mãi

```
┌─────────────────────────────────────────────────────────────┐
│                       VOUCHER                                │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ code (UNIQUE)                                                │
│ type (ENUM: PERCENTAGE, FIXED_AMOUNT)                        │
│ value                                                        │
│ status (ENUM: ACTIVE, INACTIVE, EXPIRED)                     │
│ start_date                                                   │
│ end_date                                                     │
│ usage_limit                                                  │
│ usage_count                                                  │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:Many
         │
         ├──────────────────────┐
         │                      │
         ▼                      ▼
┌──────────────────────┐  ┌──────────────────────────────┐
│       ORDERS         │  │   CUSTOMER_VOUCHER          │
│  (voucher_id FK)      │  │                              │
│                      │  │ id (PK)                      │
│                      │  │ customer_id (FK)             │
│                      │  │ voucher_id (FK)              │
│                      │  │ used_at                      │
└──────────────────────┘  └──────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                     FLASH_SALE                               │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ name                                                         │
│ start_time                                                   │
│ end_time                                                     │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:Many
         │
         ▼
┌─────────────────────────────────────────────────────────────┐
│                  FLASH_SALE_PRODUCT                           │
│ ─────────────────────────────────────────────────────────── │
│ id (PK)                                                      │
│ sale_price                                                   │
│ quantity                                                     │
│ sold                                                         │
│ flash_sale_id (FK → FlashSale)                               │
│ product_id (FK → Product)                                   │
└─────────────────────────────────────────────────────────────┘
```

### 6. Nhóm Tương Tác

```
┌─────────────────────────────────────────────────────────────┐
│                       PRODUCT                                │
│ ─────────────────────────────────────────────────────────── │
└─────────────────────────────────────────────────────────────┘
         │
         │ 1:Many
         │
         ├─────────────────────────────────────┐
         │                                     │
         ▼                                     ▼
┌──────────────────────┐  ┌──────────────────────────────┐
│   PRODUCT_REVIEW      │  │   PRODUCT_QUESTION            │
│                      │  │                              │
│ id (PK)              │  │ id (PK)                      │
│ rating (ENUM)        │  │ author                       │
│ author               │  │ question_text                 │
│ comment              │  │ status (ENUM)                 │
│ status (ENUM)        │  │ created_at                   │
│ created_at           │  │ product_id (FK)               │
│ product_id (FK)      │  └──────────────────────────────┘
└──────────────────────┘              │
                                       │ 1:Many
                                       │
                                       ▼
                              ┌──────────────────────────────┐
                              │    PRODUCT_ANSWER             │
                              │                              │
                              │ id (PK)                      │
                              │ author                       │
                              │ answer_text                 │
                              │ created_at                   │
                              │ question_id (FK)             │
                              └──────────────────────────────┘
```

### 7. Sơ Đồ Tổng Quan - Tất Cả Mối Quan Hệ

```
                    ┌──────────┐
                    │   USER   │
                    └────┬─────┘
                         │ 1:1
                    ┌────▼─────┐
                    │ CUSTOMER │
                    └────┬─────┘
                         │
         ┌───────────────┼───────────────┐
         │               │               │
   1:Many│         1:Many│         1:Many│
         │               │               │
    ┌────▼────┐    ┌─────▼─────┐    ┌────▼──────────┐
    │ ORDERS  │    │  ADDRESS  │    │ LOYALTY_TRANS │
    └────┬────┘    └───────────┘    └──────────────┘
         │
   1:Many│
         │
    ┌────▼────────┐
    │ ORDER_ITEM  │
    └────┬────────┘
         │ Many:1
    ┌────▼──────────────┐
    │ PRODUCT_VARIANT   │
    └────┬──────────────┘
         │ Many:1
    ┌────▼──────┐
    │  PRODUCT  │
    └────┬──────┘
         │
    ┌────┼────┬──────────┬──────────┐
    │    │    │          │          │
1:Many│1:Many│Many:Many │1:Many    │1:Many
    │    │    │          │          │
┌───▼──┐┌─▼───┐┌───────▼──┐┌───────▼──┐┌───────▼──────┐
│REVIEW││QUEST││COLLECTION││WISHLIST  ││ATTACHMENT   │
└──────┘│     │└──────────┘│(via join)│└─────────────┘
        └─┬───┘            │          │
          │1:Many         │          │
      ┌───▼──────┐       │          │
      │  ANSWER  │       │          │
      └──────────┘       │          │
                         │          │
                    ┌────▼──────┐  │
                    │ CUSTOMER  │──┘
                    └───────────┘

PRODUCT_VARIANT
    │
    ├─→ INVENTORY (1:Many) → WAREHOUSE (Many:1)
    │
    └─→ STOCK_MOVEMENT (1:Many) → WAREHOUSE (Many:1)

ORDERS
    │
    ├─→ VOUCHER (Many:1)
    │
    └─→ ORDER_STATUS_HISTORY (1:Many)
```

## Mối Quan Hệ Chi Tiết

### One-to-One (1:1)
- `User` ↔ `Customer` (mỗi User có một Customer profile)

### One-to-Many (1:Many)
- `Product` → `ProductVariant` (một sản phẩm có nhiều biến thể)
- `Product` → `ProductReview` (một sản phẩm có nhiều đánh giá)
- `Product` → `ProductQuestion` (một sản phẩm có nhiều câu hỏi)
- `ProductVariant` → `Inventory` (một biến thể có tồn kho tại nhiều kho)
- `ProductVariant` → `OrderItem` (một biến thể có trong nhiều đơn hàng)
- `ProductVariant` → `StockMovement` (một biến thể có nhiều lịch sử thay đổi)
- `Orders` → `OrderItem` (một đơn hàng có nhiều mặt hàng)
- `Orders` → `OrderStatusHistory` (một đơn hàng có nhiều lịch sử trạng thái)
- `Customer` → `Orders` (một khách hàng có nhiều đơn hàng)
- `Customer` → `Address` (một khách hàng có nhiều địa chỉ)
- `Customer` → `LoyaltyTransaction` (một khách hàng có nhiều giao dịch điểm)
- `Customer` → `Notification` (một khách hàng có nhiều thông báo)
- `ProductQuestion` → `ProductAnswer` (một câu hỏi có nhiều câu trả lời)
- `Warehouse` → `Inventory` (một kho có nhiều tồn kho)
- `Warehouse` → `StockMovement` (một kho có nhiều lịch sử thay đổi)
- `Voucher` → `Orders` (một voucher có thể dùng cho nhiều đơn hàng)
- `FlashSale` → `FlashSaleProduct` (một flash sale có nhiều sản phẩm)

### Many-to-Many (Many:Many)
- `Product` ↔ `Collection` (một sản phẩm có thể thuộc nhiều bộ sưu tập, một bộ sưu tập có nhiều sản phẩm)
- `Customer` ↔ `Product` (Wishlist: một khách hàng có nhiều sản phẩm yêu thích, một sản phẩm được yêu thích bởi nhiều khách hàng)

## Các Bảng Trung Gian (Join Tables)

### rel_customer__wishlist
Bảng trung gian cho quan hệ Many-to-Many giữa Customer và Product (Wishlist)

| Trường | Kiểu | Mô Tả |
|--------|------|-------|
| customer_id | BIGINT | Foreign key đến Customer |
| wishlist_id | BIGINT | Foreign key đến Product |

### rel_collection__product
Bảng trung gian cho quan hệ Many-to-Many giữa Collection và Product

| Trường | Kiểu | Mô Tả |
|--------|------|-------|
| collection_id | BIGINT | Foreign key đến Collection |
| product_id | BIGINT | Foreign key đến Product |

## Constraints và Rules

### Foreign Key Constraints
- Tất cả foreign keys đều có ON DELETE và ON UPDATE rules
- Các quan hệ quan trọng:
  - `ProductVariant.product_id`: ON DELETE CASCADE (xóa sản phẩm → xóa biến thể)
  - `OrderItem.order_id`: ON DELETE CASCADE (xóa đơn hàng → xóa chi tiết)
  - `Inventory.variant_id`: ON DELETE RESTRICT (không cho xóa nếu còn tồn kho)

### Unique Constraints
- `Product.code` - Mã sản phẩm phải duy nhất
- `Product.slug` - Slug phải duy nhất
- `ProductVariant.sku` - SKU phải duy nhất
- `Orders.code` - Mã đơn hàng phải duy nhất
- `Voucher.code` - Mã voucher phải duy nhất
- `User.login` - Tên đăng nhập phải duy nhất

### Check Constraints
- `Product.average_rating`: 0 <= average_rating <= 5
- `ProductVariant.price`: price >= 0
- `Inventory.stock_quantity`: stock_quantity >= 0
- `Orders.total_amount`: total_amount >= 0
- `Customer.loyalty_points`: loyalty_points >= 0

## Indexes

### Primary Indexes
- Tất cả bảng đều có PRIMARY KEY trên `id`

### Foreign Key Indexes
- Tất cả foreign keys đều có index để tối ưu JOIN queries

### Unique Indexes
- `Product.code`
- `Product.slug`
- `ProductVariant.sku`
- `Orders.code`
- `Voucher.code`

### Composite Indexes
- `(Orders.customer_id, Orders.placed_at)` - Để tìm đơn hàng của khách hàng theo thời gian
- `(Inventory.variant_id, Inventory.warehouse_id)` - Để tìm tồn kho nhanh
- `(ProductReview.product_id, ProductReview.status)` - Để lọc đánh giá đã duyệt

## Triggers và Stored Procedures

### Trigger: Update ProductVariant.stockQuantity
Khi `Inventory.stockQuantity` thay đổi, tự động cập nhật `ProductVariant.stockQuantity` = SUM(Inventory.stockQuantity) cho variant đó.

### Trigger: Update Product.averageRating
Khi `ProductReview` được thêm/duyệt, tự động tính lại `Product.averageRating` và `Product.reviewCount`.

### Trigger: Update Voucher.usageCount
Khi `Orders` được tạo với `voucher_id`, tự động tăng `Voucher.usageCount`.

## Data Integrity Rules

1. **Tồn kho không được âm**: `Inventory.stockQuantity >= 0`
2. **Giá không được âm**: `ProductVariant.price >= 0`
3. **Điểm thưởng không được âm**: `Customer.loyaltyPoints >= 0`
4. **Đơn hàng phải có ít nhất 1 OrderItem**
5. **ProductVariant phải thuộc về một Product**
6. **OrderItem phải thuộc về một Orders và một ProductVariant**







