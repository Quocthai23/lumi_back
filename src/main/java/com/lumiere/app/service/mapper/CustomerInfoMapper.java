package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.CustomerInfo;
import com.lumiere.app.service.dto.CustomerInfoDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { CustomerMapper.class })
public interface CustomerInfoMapper extends EntityMapper<CustomerInfoDTO, CustomerInfo> {

    @Override
    @Mapping(target = "customerId", source = "customer.id")
    CustomerInfoDTO toDto(CustomerInfo entity);

    @Override
    @Mapping(target = "customer", source = "customerId")
    CustomerInfo toEntity(CustomerInfoDTO dto);

    default CustomerInfo fromId(Long id) {
        if (id == null) {
            return null;
        }
        CustomerInfo ci = new CustomerInfo();
        ci.setId(id);
        return ci;
    }

    default Customer fromCustomerId(Long id) {
        if (id == null) {
            return null;
        }
        Customer c = new Customer();
        c.setId(id);
        return c;
    }
}
