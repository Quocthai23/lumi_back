package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.User;
import com.lumiere.app.service.dto.CustomerDTO;
import com.lumiere.app.service.dto.ProductDTO;
import com.lumiere.app.service.dto.UserDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Customer} and its DTO {@link CustomerDTO}.
 */
@Mapper(componentModel = "spring")
public interface CustomerMapper extends EntityMapper<CustomerDTO, Customer> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userLogin")
    @Mapping(target = "wishlists", source = "wishlists", qualifiedByName = "productNameSet")
    CustomerDTO toDto(Customer s);

    @Mapping(target = "removeWishlist", ignore = true)
    Customer toEntity(CustomerDTO customerDTO);

    @Named("userLogin")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserDTO toDtoUserLogin(User user);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);

    @Named("productNameSet")
    default Set<ProductDTO> toDtoProductNameSet(Set<Product> product) {
        return product.stream().map(this::toDtoProductName).collect(Collectors.toSet());
    }
}
