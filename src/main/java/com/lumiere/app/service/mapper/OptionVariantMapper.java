// src/main/java/com/lumiere/app/service/mapper/OptionVariantMapper.java
package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.OptionSelect;
import com.lumiere.app.domain.OptionVariant;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.service.dto.OptionVariantDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OptionVariantMapper {
  @Mapping(target = "productVariantId", source = "productVariant.id")
  @Mapping(target = "optionSelectId", source = "optionSelect.id")
  OptionVariantDTO toDto(OptionVariant e);

  @Mapping(target = "productVariant", source = "productVariantId", qualifiedByName = "variantFromId")
  @Mapping(target = "optionSelect", source = "optionSelectId", qualifiedByName = "selectFromId")
  OptionVariant toEntity(OptionVariantDTO dto);

  @Named("variantFromId")
  default ProductVariant variant(Long id){
    if (id == null) return null;
    ProductVariant v = new ProductVariant();
    v.setId(id);
    return v;
  }

  @Named("selectFromId")
  default OptionSelect select(Long id){
    if (id == null) return null;
    OptionSelect s = new OptionSelect();
    s.setId(id);
    return s;
  }
}
