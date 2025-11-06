package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ChatSession;
import com.lumiere.app.repository.ChatSessionRepository;
import com.lumiere.app.service.ChatSessionService;
import com.lumiere.app.service.dto.ChatSessionDTO;
import com.lumiere.app.service.mapper.ChatSessionMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.ChatSession}.
 */
@Service
@Transactional
public class ChatSessionServiceImpl implements ChatSessionService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatSessionServiceImpl.class);

    private final ChatSessionRepository chatSessionRepository;

    private final ChatSessionMapper chatSessionMapper;

    public ChatSessionServiceImpl(ChatSessionRepository chatSessionRepository, ChatSessionMapper chatSessionMapper) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatSessionMapper = chatSessionMapper;
    }

    @Override
    public ChatSessionDTO save(ChatSessionDTO chatSessionDTO) {
        LOG.debug("Request to save ChatSession : {}", chatSessionDTO);
        ChatSession chatSession = chatSessionMapper.toEntity(chatSessionDTO);
        chatSession = chatSessionRepository.save(chatSession);
        return chatSessionMapper.toDto(chatSession);
    }

    @Override
    public ChatSessionDTO update(ChatSessionDTO chatSessionDTO) {
        LOG.debug("Request to update ChatSession : {}", chatSessionDTO);
        ChatSession chatSession = chatSessionMapper.toEntity(chatSessionDTO);
        chatSession = chatSessionRepository.save(chatSession);
        return chatSessionMapper.toDto(chatSession);
    }

    @Override
    public Optional<ChatSessionDTO> partialUpdate(ChatSessionDTO chatSessionDTO) {
        LOG.debug("Request to partially update ChatSession : {}", chatSessionDTO);

        return chatSessionRepository
            .findById(chatSessionDTO.getId())
            .map(existingChatSession -> {
                chatSessionMapper.partialUpdate(existingChatSession, chatSessionDTO);

                return existingChatSession;
            })
            .map(chatSessionRepository::save)
            .map(chatSessionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatSessionDTO> findAll() {
        LOG.debug("Request to get all ChatSessions");
        return chatSessionRepository.findAll().stream().map(chatSessionMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatSessionDTO> findOne(Long id) {
        LOG.debug("Request to get ChatSession : {}", id);
        return chatSessionRepository.findById(id).map(chatSessionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ChatSession : {}", id);
        chatSessionRepository.deleteById(id);
    }
}
