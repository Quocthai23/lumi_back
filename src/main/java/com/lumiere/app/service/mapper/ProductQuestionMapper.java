package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductQuestion;
import com.lumiere.app.service.dto.ProductDTO;
import com.lumiere.app.service.dto.ProductQuestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductQuestion} and its DTO {@link ProductQuestionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductQuestionMapper extends EntityMapper<ProductQuestionDTO, ProductQuestion> {
    @Mapping(target = "product", source = "product", qualifiedByName = "productName")
    ProductQuestionDTO toDto(ProductQuestion s);

    @Named("productName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    ProductDTO toDtoProductName(Product product);
}
