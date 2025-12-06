package com.lumiere.app.web.rest;

import com.lumiere.app.service.HomeService;
import com.lumiere.app.service.dto.CategoryWithProductsDTO;
import com.lumiere.app.service.dto.ProductDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing Home page data.
 */
@RestController
@RequestMapping("/api/home")
public class HomeResource {

    private static final Logger LOG = LoggerFactory.getLogger(HomeResource.class);

    private final HomeService homeService;

    public HomeResource(HomeService homeService) {
        this.homeService = homeService;
    }

    /**
     * {@code GET  /home/best-selling} : Lấy danh sách sản phẩm bán chạy.
     *
     * @param limit số lượng sản phẩm cần lấy (mặc định: 10)
     * @return danh sách sản phẩm bán chạy
     */
    @GetMapping("/best-selling")
    public ResponseEntity<List<ProductDTO>> getBestSellingProducts(
        @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        LOG.debug("REST request to get best selling products with limit: {}", limit);
        List<ProductDTO> products = homeService.getBestSellingProducts(limit);
        return ResponseEntity.ok().body(products);
    }

    /**
     * {@code GET  /home/new-arrivals} : Lấy danh sách hàng mới về.
     *
     * @param limit số lượng sản phẩm cần lấy (mặc định: 10)
     * @return danh sách hàng mới về
     */
    @GetMapping("/new-arrivals")
    public ResponseEntity<List<ProductDTO>> getNewArrivals(
        @RequestParam(value = "limit", defaultValue = "10") int limit
    ) {
        LOG.debug("REST request to get new arrivals with limit: {}", limit);
        List<ProductDTO> products = homeService.getNewArrivals(limit);
        return ResponseEntity.ok().body(products);
    }

    /**
     * {@code GET  /home/shop-by-category} : Lấy danh sách categories với sản phẩm mẫu để mua sắm theo danh mục.
     *
     * @param productsPerCategory số lượng sản phẩm mẫu mỗi category (mặc định: 4)
     * @return danh sách categories với sản phẩm
     */
    @GetMapping("/shop-by-category")
    public ResponseEntity<List<CategoryWithProductsDTO>> getShopByCategory(
        @RequestParam(value = "productsPerCategory", defaultValue = "4") int productsPerCategory
    ) {
        LOG.debug("REST request to get shop by category with {} products per category", productsPerCategory);
        List<CategoryWithProductsDTO> categories = homeService.getShopByCategory(productsPerCategory);
        return ResponseEntity.ok().body(categories);
    }
}

