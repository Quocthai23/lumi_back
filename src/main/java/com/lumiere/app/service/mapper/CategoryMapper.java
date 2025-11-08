package com.lumiere.app.service.mapper;


import com.lumiere.app.domain.Category;
import com.lumiere.app.service.dto.CategoryDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    Category toEntity(CategoryDTO dto);
    CategoryDTO toDto(Category entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget Category entity, CategoryDTO dto);

    default Category fromId(Long id) {
        if (id == null) return null;
        Category c = new Category();
        c.setId(id);
        return c;
        }
}
