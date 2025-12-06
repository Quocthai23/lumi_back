package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Category;
import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.enumeration.OrderStatus;
import com.lumiere.app.domain.enumeration.ProductStatus;
import com.lumiere.app.repository.CategoryRepository;
import com.lumiere.app.repository.OrderItemRepository;
import com.lumiere.app.repository.ProductRepository;
import com.lumiere.app.service.HomeService;
import com.lumiere.app.service.dto.CategoryWithProductsDTO;
import com.lumiere.app.service.dto.ProductDTO;
import com.lumiere.app.service.mapper.ProductMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing Home page data.
 */
@Service
@Transactional
public class HomeServiceImpl implements HomeService {

    private static final Logger LOG = LoggerFactory.getLogger(HomeServiceImpl.class);

    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public HomeServiceImpl(
        OrderItemRepository orderItemRepository,
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        ProductMapper productMapper
    ) {
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.productMapper = productMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getBestSellingProducts(int limit) {
        LOG.debug("Request to get best selling products with limit: {}", limit);

        // Lấy các order status hợp lệ (đã xác nhận, đang xử lý, đã giao, hoàn thành)
        List<OrderStatus> validStatuses = Arrays.asList(
            OrderStatus.CONFIRMED,
            OrderStatus.PROCESSING,
            OrderStatus.SHIPPING,
            OrderStatus.DELIVERED,
            OrderStatus.COMPLETED
        );

        Pageable pageable = PageRequest.of(0, limit);
        
        // Lấy top product IDs bán chạy
        List<Object[]> results = orderItemRepository.getTopProductIdsByQuantity(
            validStatuses,
            ProductStatus.ACTIVE,
            pageable
        );

        if (results.isEmpty()) {
            return Collections.emptyList();
        }

        // Extract product IDs từ kết quả [productId, totalQuantity]
        List<Long> productIds = results.stream()
            .map(row -> ((Long) row[0]))
            .toList();

        // Lấy products với attachments
        List<Product> products = productRepository.findWithAttachmentsByIdIn(productIds);
        
        // Sắp xếp lại theo thứ tự trong productIds
        Map<Long, Integer> orderMap = new HashMap<>();
        for (int i = 0; i < productIds.size(); i++) {
            orderMap.put(productIds.get(i), i);
        }
        products.sort(Comparator.comparing(p -> orderMap.getOrDefault(p.getId(), Integer.MAX_VALUE)));

        return products.stream()
            .map(productMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getNewArrivals(int limit) {
        LOG.debug("Request to get new arrivals with limit: {}", limit);

        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        
        // Lấy products mới nhất với status ACTIVE
        List<Product> products = productRepository.findAll(
            (root, query, cb) -> cb.equal(root.get("status"), ProductStatus.ACTIVE),
            pageable
        ).getContent();

        if (products.isEmpty()) {
            return Collections.emptyList();
        }

        // Lấy IDs để fetch với attachments
        List<Long> productIds = products.stream().map(Product::getId).toList();
        List<Product> productsWithAttachments = productRepository.findWithAttachmentsByIdIn(productIds);

        // Sắp xếp lại theo thứ tự createdAt
        Map<Long, Product> productMap = productsWithAttachments.stream()
            .collect(Collectors.toMap(Product::getId, p -> p));
        
        List<Product> sortedProducts = productIds.stream()
            .map(productMap::get)
            .filter(Objects::nonNull)
            .toList();

        return sortedProducts.stream()
            .map(productMapper::toDto)
            .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryWithProductsDTO> getShopByCategory(int productsPerCategory) {
        LOG.debug("Request to get shop by category with {} products per category", productsPerCategory);

        // Lấy tất cả categories (chỉ lấy categories cha, không lấy con)
        List<Category> categories = categoryRepository.findAll().stream()
            .filter(c -> c.getFatherId() == null) // Chỉ lấy categories cha
            .toList();

        if (categories.isEmpty()) {
            return Collections.emptyList();
        }

        List<CategoryWithProductsDTO> result = new ArrayList<>();

        for (Category category : categories) {
            CategoryWithProductsDTO dto = new CategoryWithProductsDTO(
                category.getId(),
                category.getName(),
                category.getFatherId()
            );

            // Lấy sản phẩm mẫu cho category này
            Pageable pageable = PageRequest.of(0, productsPerCategory, Sort.by(Sort.Direction.DESC, "createdAt"));
            List<Product> products = productRepository.findAll(
                (root, query, cb) -> cb.and(
                    cb.equal(root.get("categoryId"), category.getId()),
                    cb.equal(root.get("status"), ProductStatus.ACTIVE)
                ),
                pageable
            ).getContent();

            if (!products.isEmpty()) {
                List<Long> productIds = products.stream().map(Product::getId).toList();
                List<Product> productsWithAttachments = productRepository.findWithAttachmentsByIdIn(productIds);
                
                // Sắp xếp lại theo thứ tự
                Map<Long, Product> productMap = productsWithAttachments.stream()
                    .collect(Collectors.toMap(Product::getId, p -> p));
                
                List<ProductDTO> productDTOs = productIds.stream()
                    .map(productMap::get)
                    .filter(Objects::nonNull)
                    .map(productMapper::toDto)
                    .toList();

                dto.setProducts(productDTOs);
            }

            result.add(dto);
        }

        return result;
    }
}

