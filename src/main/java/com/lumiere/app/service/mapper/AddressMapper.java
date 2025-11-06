package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Address;
import com.lumiere.app.domain.Customer;
import com.lumiere.app.service.dto.AddressDTO;
import com.lumiere.app.service.dto.CustomerDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Address} and its DTO {@link AddressDTO}.
 */
@Mapper(componentModel = "spring")
public interface AddressMapper extends EntityMapper<AddressDTO, Address> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerFirstName")
    AddressDTO toDto(Address s);

    @Named("customerFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    CustomerDTO toDtoCustomerFirstName(Customer customer);
}
