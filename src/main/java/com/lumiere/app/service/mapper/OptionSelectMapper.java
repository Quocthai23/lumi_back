// src/main/java/com/lumiere/app/service/mapper/OptionSelectMapper.java
package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.OptionGroup;
import com.lumiere.app.domain.OptionSelect;
import com.lumiere.app.service.dto.OptionSelectDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OptionSelectMapper {
  @Mapping(target = "optionGroupId", source = "optionGroup.id")
  OptionSelectDTO toDto(OptionSelect e);

  @Mapping(target = "optionGroup", source = "optionGroupId", qualifiedByName = "groupFromId")
  OptionSelect toEntity(OptionSelectDTO dto);

  @Named("groupFromId")
  default OptionGroup group(Long id){
    if (id == null) return null;
    OptionGroup g = new OptionGroup();
    g.setId(id);
    return g;
  }
}
