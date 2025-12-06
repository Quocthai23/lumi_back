package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.Orders;
import com.lumiere.app.domain.Voucher;
import com.lumiere.app.service.dto.CustomerDTO;
import com.lumiere.app.service.dto.OrdersDTO;
import com.lumiere.app.service.dto.VoucherDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Orders} and its DTO {@link OrdersDTO}.
 */
@Mapper(componentModel = "spring", uses = { VoucherMapper.class })
public interface OrdersMapper extends EntityMapper<OrdersDTO, Orders> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerFirstName")
    @Mapping(target = "voucher", source = "voucher", qualifiedByName = "voucherCode")
    @Mapping(target = "orderItems", ignore = true)
    @Mapping(target = "canReview", ignore = true)
    OrdersDTO toDto(Orders s);

    @Named("customerFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    CustomerDTO toDtoCustomerFirstName(Customer customer);

    @Named("voucherCode")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "code", source = "code")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "value", source = "value")
    VoucherDTO toDtoVoucherCode(Voucher voucher);
}
