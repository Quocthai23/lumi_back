package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.ProductAnswer;
import com.lumiere.app.domain.ProductQuestion;
import com.lumiere.app.service.dto.ProductAnswerDTO;
import com.lumiere.app.service.dto.ProductQuestionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProductAnswer} and its DTO {@link ProductAnswerDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProductAnswerMapper extends EntityMapper<ProductAnswerDTO, ProductAnswer> {
    @Mapping(target = "question", source = "question", qualifiedByName = "productQuestionQuestionText")
    ProductAnswerDTO toDto(ProductAnswer s);

    @Named("productQuestionQuestionText")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "questionText", source = "questionText")
    ProductQuestionDTO toDtoProductQuestionQuestionText(ProductQuestion productQuestion);

    default String map(byte[] value) {
        return new String(value);
    }
}
