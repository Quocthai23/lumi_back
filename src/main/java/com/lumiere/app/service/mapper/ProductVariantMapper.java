package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.service.dto.ProductDTO;
import com.lumiere.app.service.dto.ProductVariantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductVariant} and its DTO {@link ProductVariantDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductVariantMapper extends EntityMapper<ProductVariantDTO, ProductVariant> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductVariantDTO toDto(ProductVariant s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
