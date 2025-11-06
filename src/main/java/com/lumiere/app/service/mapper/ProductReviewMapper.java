package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductReview;
import com.lumiere.app.service.dto.ProductDTO;
import com.lumiere.app.service.dto.ProductReviewDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductReview} and its DTO {@link ProductReviewDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductReviewMapper extends EntityMapper<ProductReviewDTO, ProductReview> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductReviewDTO toDto(ProductReview s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
