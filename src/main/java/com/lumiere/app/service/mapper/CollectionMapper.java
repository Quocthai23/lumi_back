package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Collection;
import com.lumiere.app.domain.Product;
import com.lumiere.app.service.dto.CollectionDTO;
import com.lumiere.app.service.dto.ProductDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Collection} and its DTO {@link CollectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface CollectionMapper extends EntityMapper<CollectionDTO, Collection> {
    @Mapping(target = "products", source = "products", qualifiedByName = "productNameSet")
    CollectionDTO toDto(Collection s);

    @Mapping(target = "removeProducts", ignore = true)
    Collection toEntity(CollectionDTO collectionDTO);

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
