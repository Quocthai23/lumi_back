package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.Orders;
import com.lumiere.app.service.dto.CustomerDTO;
import com.lumiere.app.service.dto.OrdersDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Orders} and its DTO {@link OrdersDTO}.
 */
@Mapper(componentModel = "spring")
public interface OrdersMapper extends EntityMapper<OrdersDTO, Orders> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerFirstName")
    OrdersDTO toDto(Orders s);

    @Named("customerFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    CustomerDTO toDtoCustomerFirstName(Customer customer);
}
