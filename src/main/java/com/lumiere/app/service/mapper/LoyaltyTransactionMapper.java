package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.LoyaltyTransaction;
import com.lumiere.app.service.dto.CustomerDTO;
import com.lumiere.app.service.dto.LoyaltyTransactionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link LoyaltyTransaction} and its DTO {@link LoyaltyTransactionDTO}.
 */
@Mapper(componentModel = "spring")
public interface LoyaltyTransactionMapper extends EntityMapper<LoyaltyTransactionDTO, LoyaltyTransaction> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerFirstName")
    LoyaltyTransactionDTO toDto(LoyaltyTransaction s);

    @Named("customerFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    CustomerDTO toDtoCustomerFirstName(Customer customer);
}
