package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.WishlistItem;
import com.lumiere.app.service.dto.WishlistItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface WishlistItemMapper {

    WishlistItemDTO toDto(WishlistItem entity);

    WishlistItem toEntity(WishlistItemDTO dto);
}
