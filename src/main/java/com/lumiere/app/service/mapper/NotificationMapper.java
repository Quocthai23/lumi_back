package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.Notification;
import com.lumiere.app.service.dto.CustomerDTO;
import com.lumiere.app.service.dto.NotificationDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerFirstName")
    NotificationDTO toDto(Notification s);

    @Named("customerFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    CustomerDTO toDtoCustomerFirstName(Customer customer);
}
