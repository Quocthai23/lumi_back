package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.ContactMessage;
import com.lumiere.app.service.dto.ContactMessageDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ContactMessage} and its DTO {@link ContactMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ContactMessageMapper extends EntityMapper<ContactMessageDTO, ContactMessage> {}

