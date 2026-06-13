package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.FlashSale;
import com.lumiere.app.domain.FlashSaleProduct;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.service.dto.FlashSaleDTO;
import com.lumiere.app.service.dto.FlashSaleProductDTO;
import com.lumiere.app.service.dto.ProductVariantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FlashSaleProduct} and its DTO {@link FlashSaleProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface FlashSaleProductMapper extends EntityMapper<FlashSaleProductDTO, FlashSaleProduct> {
    @Mapping(target = "flashSale", source = "flashSale", qualifiedByName = "flashSaleName")
    @Mapping(target = "productVariant", source = "productVariant", qualifiedByName = "productVariantName")
    FlashSaleProductDTO toDto(FlashSaleProduct s);

    @Named("flashSaleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FlashSaleDTO toDtoFlashSaleName(FlashSale flashSale);

    @Named("productVariantName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "price", source = "price")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "isDefault", source = "isDefault")
    @Mapping(target = "color", source = "color")
    @Mapping(target = "size", source = "size")
    @Mapping(target = "stockQuantity", source = "stockQuantity")
    @Mapping(target = "urlImage", source = "urlImage")
    ProductVariantDTO toDtoProductVariantName(ProductVariant productVariant);
}
