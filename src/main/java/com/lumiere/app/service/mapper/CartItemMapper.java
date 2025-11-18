package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.CartItem;
import com.lumiere.app.service.dto.CartItemDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartItemMapper extends EntityMapper<CartItemDTO, CartItem> {
}
