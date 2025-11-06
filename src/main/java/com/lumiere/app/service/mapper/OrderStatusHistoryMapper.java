package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.OrderStatusHistory;
import com.lumiere.app.domain.Orders;
import com.lumiere.app.service.dto.OrderStatusHistoryDTO;
import com.lumiere.app.service.dto.OrdersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OrderStatusHistory} and its DTO {@link OrderStatusHistoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrderStatusHistoryMapper extends EntityMapper<OrderStatusHistoryDTO, OrderStatusHistory> {
    @Mapping(target = "order", source = "order", qualifiedByName = "ordersCode")
    OrderStatusHistoryDTO toDto(OrderStatusHistory s);

    @Named("ordersCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    OrdersDTO toDtoOrdersCode(Orders orders);
}
