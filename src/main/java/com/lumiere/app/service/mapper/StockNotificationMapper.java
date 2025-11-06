package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.domain.StockNotification;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.dto.StockNotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockNotification} and its DTO {@link StockNotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockNotificationMapper extends EntityMapper<StockNotificationDTO, StockNotification> {
    @Mapping(target = "productVariant", source = "productVariant", qualifiedByName = "productVariantSku")
    StockNotificationDTO toDto(StockNotification s);

    @Named("productVariantSku")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sku", source = "sku")
    ProductVariantDTO toDtoProductVariantSku(ProductVariant productVariant);
}
