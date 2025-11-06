package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.OrderItem;
import com.lumiere.app.domain.Orders;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.service.dto.OrderItemDTO;
import com.lumiere.app.service.dto.OrdersDTO;
import com.lumiere.app.service.dto.ProductVariantDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderItem} and its DTO {@link OrderItemDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Mapping(target = "order", source = "order", qualifiedByName = "ordersCode")
    @Mapping(target = "productVariant", source = "productVariant", qualifiedByName = "productVariantSku")
    OrderItemDTO toDto(OrderItem s);

    @Named("ordersCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    OrdersDTO toDtoOrdersCode(Orders orders);

    @Named("productVariantSku")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sku", source = "sku")
    ProductVariantDTO toDtoProductVariantSku(ProductVariant productVariant);
}
