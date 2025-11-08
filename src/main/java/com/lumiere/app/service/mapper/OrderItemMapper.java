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
@Mapper(componentModel = "spring", uses = { ProductVariantMapper.class })
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {
    @Override
    @Mapping(target = "productVariant", source = "productVariant")
    OrderItemDTO toDto(OrderItem s);

    // Nếu vẫn cần bản "mỏng":
    // @Mapping(target = "order", source = "order", qualifiedByName = "ordersCode")
    // @Mapping(target = "productVariant", source = "productVariant", qualifiedByName = "productVariantSku")
    // OrderItemDTO toDtoSlim(OrderItem s);
    @Override
    OrderItem toEntity(OrderItemDTO dto);

    // Nếu muốn an toàn khi patch:
    @Override
    @BeanMapping(ignoreByDefault = false) // map theo tên đầy đủ
    void partialUpdate(@org.mapstruct.MappingTarget OrderItem entity, OrderItemDTO dto);

}
