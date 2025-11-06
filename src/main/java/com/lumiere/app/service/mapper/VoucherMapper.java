package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Voucher;
import com.lumiere.app.service.dto.VoucherDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Voucher} and its DTO {@link VoucherDTO}.
 */
@Mapper(componentModel = "spring")
public interface VoucherMapper extends EntityMapper<VoucherDTO, Voucher> {}
