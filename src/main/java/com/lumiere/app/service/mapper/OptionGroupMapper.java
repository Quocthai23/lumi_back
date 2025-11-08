// src/main/java/com/lumiere/app/service/mapper/OptionGroupMapper.java
package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.OptionGroup;
import com.lumiere.app.domain.Product;
import com.lumiere.app.service.dto.OptionGroupDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OptionGroupMapper {
  @Mapping(target = "productId", source = "product.id")
  OptionGroupDTO toDto(OptionGroup e);

  @Mapping(target = "product", source = "productId", qualifiedByName = "productFromId")
  OptionGroup toEntity(OptionGroupDTO dto);

  @Named("productFromId")
  default Product mapProduct(Long id){
    if (id == null) return null;
    Product p = new Product();
    p.setId(id);
    return p;
  }
}
