package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.domain.StockMovement;
import com.lumiere.app.domain.Warehouse;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.dto.StockMovementDTO;
import com.lumiere.app.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link StockMovement} and its DTO {@link StockMovementDTO}.
 */
@Mapper(componentModel = "spring")
public interface StockMovementMapper extends EntityMapper<StockMovementDTO, StockMovement> {
    @Mapping(target = "productVariant", source = "productVariant", qualifiedByName = "productVariantSku")
    @Mapping(target = "warehouse", source = "warehouse", qualifiedByName = "warehouseName")
    StockMovementDTO toDto(StockMovement s);

    @Named("productVariantSku")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sku", source = "sku")
    ProductVariantDTO toDtoProductVariantSku(ProductVariant productVariant);

    @Named("warehouseName")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    WarehouseDTO toDtoWarehouseName(Warehouse warehouse);
}
