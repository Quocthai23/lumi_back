package com.lumiere.app.service.impl;

import com.lumiere.app.domain.ChatMessage;
import com.lumiere.app.domain.ContactMessage;
import com.lumiere.app.domain.enumeration.MessageSender;
import com.lumiere.app.repository.ChatMessageRepository;
import com.lumiere.app.repository.ContactMessageRepository;
import com.lumiere.app.service.ChatMessageService;
import com.lumiere.app.service.dto.ChatMessageDTO;
import com.lumiere.app.service.mapper.ChatMessageMapper;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.ChatMessage}.
 */
@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {

    private static final Logger LOG = LoggerFactory.getLogger(ChatMessageServiceImpl.class);

    private final ChatMessageRepository chatMessageRepository;

    private final ChatMessageMapper chatMessageMapper;

    private final ContactMessageRepository contactMessageRepository;

    public ChatMessageServiceImpl(
        ChatMessageRepository chatMessageRepository,
        ChatMessageMapper chatMessageMapper,
        ContactMessageRepository contactMessageRepository
    ) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatMessageMapper = chatMessageMapper;
        this.contactMessageRepository = contactMessageRepository;
    }

    @Override
    public ChatMessageDTO save(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to save ChatMessage : {}", chatMessageDTO);
        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageDTO);
        
        // Set contactMessage if contactMessageId is provided
        if (chatMessageDTO.getContactMessageId() != null) {
            ContactMessage contactMessage = contactMessageRepository
                .findById(chatMessageDTO.getContactMessageId())
                .orElseThrow(() -> new RuntimeException("ContactMessage not found with id: " + chatMessageDTO.getContactMessageId()));
            chatMessage.setContactMessage(contactMessage);
        }
        
        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toDto(chatMessage);
    }

    @Override
    public ChatMessageDTO update(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to update ChatMessage : {}", chatMessageDTO);
        ChatMessage chatMessage = chatMessageMapper.toEntity(chatMessageDTO);
        
        // Set contactMessage if contactMessageId is provided
        if (chatMessageDTO.getContactMessageId() != null) {
            ContactMessage contactMessage = contactMessageRepository
                .findById(chatMessageDTO.getContactMessageId())
                .orElseThrow(() -> new RuntimeException("ContactMessage not found with id: " + chatMessageDTO.getContactMessageId()));
            chatMessage.setContactMessage(contactMessage);
        }
        
        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toDto(chatMessage);
    }

    @Override
    public Optional<ChatMessageDTO> partialUpdate(ChatMessageDTO chatMessageDTO) {
        LOG.debug("Request to partially update ChatMessage : {}", chatMessageDTO);

        return chatMessageRepository
            .findById(chatMessageDTO.getId())
            .map(existingChatMessage -> {
                chatMessageMapper.partialUpdate(existingChatMessage, chatMessageDTO);

                return existingChatMessage;
            })
            .map(chatMessageRepository::save)
            .map(chatMessageMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> findAll() {
        LOG.debug("Request to get all ChatMessages");
        return chatMessageRepository.findAll().stream().map(chatMessageMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ChatMessageDTO> findOne(Long id) {
        LOG.debug("Request to get ChatMessage : {}", id);
        return chatMessageRepository.findById(id).map(chatMessageMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete ChatMessage : {}", id);
        chatMessageRepository.deleteById(id);
    }

    @Override
    public ChatMessageDTO saveMessageForContact(String message, Long contactMessageId, String sender) {
        LOG.debug("Request to save ChatMessage for ContactMessage : {}, sender: {}", contactMessageId, sender);
        ContactMessage contactMessage = contactMessageRepository
            .findById(contactMessageId)
            .orElseThrow(() -> new RuntimeException("ContactMessage not found with id: " + contactMessageId));

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setText(message);
        chatMessage.setTimestamp(Instant.now());
        chatMessage.setContactMessage(contactMessage);

        // Convert sender string to MessageSender enum
        MessageSender messageSender;
        if ("admin".equalsIgnoreCase(sender)) {
            messageSender = MessageSender.BOT; // Admin messages are treated as BOT
        } else {
            messageSender = MessageSender.USER;
        }
        chatMessage.setSender(messageSender);

        chatMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toDto(chatMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageDTO> findByContactMessageId(Long contactMessageId) {
        LOG.debug("Request to get all ChatMessages for ContactMessage : {}", contactMessageId);
        return chatMessageRepository
            .findByContactMessageIdOrderByTimestampAsc(contactMessageId)
            .stream()
            .map(chatMessageMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }
}
