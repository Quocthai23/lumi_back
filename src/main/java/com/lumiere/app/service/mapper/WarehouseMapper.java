package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Warehouse;
import com.lumiere.app.service.dto.WarehouseDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Warehouse} and its DTO {@link WarehouseDTO}.
 */
@Mapper(componentModel = "spring")
public interface WarehouseMapper extends EntityMapper<WarehouseDTO, Warehouse> {}
