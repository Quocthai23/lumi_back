package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.ChatSession;
import com.lumiere.app.service.dto.ChatSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChatSession} and its DTO {@link ChatSessionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChatSessionMapper extends EntityMapper<ChatSessionDTO, ChatSession> {}
