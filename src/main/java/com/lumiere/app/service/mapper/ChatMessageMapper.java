package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.ChatMessage;
import com.lumiere.app.domain.ChatSession;
import com.lumiere.app.domain.ContactMessage;
import com.lumiere.app.service.dto.ChatMessageDTO;
import com.lumiere.app.service.dto.ChatSessionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ChatMessage} and its DTO {@link ChatMessageDTO}.
 */
@Mapper(componentModel = "spring")
public interface ChatMessageMapper extends EntityMapper<ChatMessageDTO, ChatMessage> {
    @Mapping(target = "session", source = "session", qualifiedByName = "chatSessionId")
    @Mapping(target = "contactMessageId", source = "contactMessage.id")
    ChatMessageDTO toDto(ChatMessage s);

    @Mapping(target = "session", ignore = true)
    @Mapping(target = "contactMessage", ignore = true)
    ChatMessage toEntity(ChatMessageDTO chatMessageDTO);

    @Named("chatSessionId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ChatSessionDTO toDtoChatSessionId(ChatSession chatSession);
}
