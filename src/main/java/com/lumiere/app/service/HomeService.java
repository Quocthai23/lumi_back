package com.lumiere.app.service;

import com.lumiere.app.service.dto.CategoryWithProductsDTO;
import com.lumiere.app.service.dto.ProductDTO;

import java.util.List;

/**
 * Service Interface for managing Home page data.
 */
public interface HomeService {
    /**
     * Lấy danh sách sản phẩm bán chạy nhất.
     *
     * @param limit số lượng sản phẩm cần lấy
     * @return danh sách sản phẩm bán chạy
     */
    List<ProductDTO> getBestSellingProducts(int limit);

    /**
     * Lấy danh sách hàng mới về.
     *
     * @param limit số lượng sản phẩm cần lấy
     * @return danh sách hàng mới về
     */
    List<ProductDTO> getNewArrivals(int limit);

    /**
     * Lấy danh sách categories với sản phẩm mẫu để mua sắm theo danh mục.
     *
     * @param productsPerCategory số lượng sản phẩm mẫu mỗi category
     * @return danh sách categories với sản phẩm
     */
    List<CategoryWithProductsDTO> getShopByCategory(int productsPerCategory);
}

