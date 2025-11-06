package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.FlashSale;
import com.lumiere.app.domain.FlashSaleProduct;
import com.lumiere.app.domain.Product;
import com.lumiere.app.service.dto.FlashSaleDTO;
import com.lumiere.app.service.dto.FlashSaleProductDTO;
import com.lumiere.app.service.dto.ProductDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FlashSaleProduct} and its DTO {@link FlashSaleProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface FlashSaleProductMapper extends EntityMapper<FlashSaleProductDTO, FlashSaleProduct> {
    @Mapping(target = "flashSale", source = "flashSale", qualifiedByName = "flashSaleName")
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    FlashSaleProductDTO toDto(FlashSaleProduct s);

    @Named("flashSaleName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    FlashSaleDTO toDtoFlashSaleName(FlashSale flashSale);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
