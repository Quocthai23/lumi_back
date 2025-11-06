package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Collection;
import com.lumiere.app.domain.Customer;
import com.lumiere.app.domain.Product;
import com.lumiere.app.service.dto.CollectionDTO;
import com.lumiere.app.service.dto.CustomerDTO;
import com.lumiere.app.service.dto.ProductDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Product} and its DTO {@link ProductDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductMapper extends EntityMapper<ProductDTO, Product> {
    @Mapping(target = "collections", source = "collections", qualifiedByName = "collectionNameSet")
    @Mapping(target = "wishlistedBies", source = "wishlistedBies", qualifiedByName = "customerFirstNameSet")
    ProductDTO toDto(Product s);

    @Mapping(target = "collections", ignore = true)
    @Mapping(target = "removeCollections", ignore = true)
    @Mapping(target = "wishlistedBies", ignore = true)
    @Mapping(target = "removeWishlistedBy", ignore = true)
    Product toEntity(ProductDTO productDTO);

    @Named("collectionName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    CollectionDTO toDtoCollectionName(Collection collection);

    @Named("collectionNameSet")
    default Set<CollectionDTO> toDtoCollectionNameSet(Set<Collection> collection) {
        return collection.stream().map(this::toDtoCollectionName).collect(Collectors.toSet());
    }

    @Named("customerFirstName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "firstName", source = "firstName")
    CustomerDTO toDtoCustomerFirstName(Customer customer);

    @Named("customerFirstNameSet")
    default Set<CustomerDTO> toDtoCustomerFirstNameSet(Set<Customer> customer) {
        return customer.stream().map(this::toDtoCustomerFirstName).collect(Collectors.toSet());
    }
}
