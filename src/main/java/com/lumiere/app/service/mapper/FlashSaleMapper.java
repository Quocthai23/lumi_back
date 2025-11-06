package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.FlashSale;
import com.lumiere.app.service.dto.FlashSaleDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link FlashSale} and its DTO {@link FlashSaleDTO}.
 */
@Mapper(componentModel = "spring")
public interface FlashSaleMapper extends EntityMapper<FlashSaleDTO, FlashSale> {}
