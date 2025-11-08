package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Inventory;
import com.lumiere.app.domain.ProductVariant;
import com.lumiere.app.domain.Warehouse;
import com.lumiere.app.service.dto.InventoryDTO;
import com.lumiere.app.service.dto.ProductVariantDTO;
import com.lumiere.app.service.dto.WarehouseDTO;
import org.mapstruct.*;

import java.util.List;

/**
 * Mapper for the entity {@link Inventory} and its DTO {@link InventoryDTO}.
 */
@Mapper(componentModel = "spring")
public interface InventoryMapper extends EntityMapper<InventoryDTO, Inventory> {
    @Mapping(target = "productVariant", source = "productVariant", qualifiedByName = "productVariantSku")
    InventoryDTO toDto(Inventory s);

    @Named("productVariantSku")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "sku", source = "sku")
    ProductVariantDTO toDtoProductVariantSku(ProductVariant productVariant);

}
