package com.lumiere.app.service;

import com.lumiere.app.service.dto.ChatMessageDTO;
import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.lumiere.app.domain.ChatMessage}.
 */
public interface ChatMessageService {
    /**
     * Save a chatMessage.
     *
     * @param chatMessageDTO the entity to save.
     * @return the persisted entity.
     */
    ChatMessageDTO save(ChatMessageDTO chatMessageDTO);

    /**
     * Updates a chatMessage.
     *
     * @param chatMessageDTO the entity to update.
     * @return the persisted entity.
     */
    ChatMessageDTO update(ChatMessageDTO chatMessageDTO);

    /**
     * Partially updates a chatMessage.
     *
     * @param chatMessageDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<ChatMessageDTO> partialUpdate(ChatMessageDTO chatMessageDTO);

    /**
     * Get all the chatMessages.
     *
     * @return the list of entities.
     */
    List<ChatMessageDTO> findAll();

    /**
     * Get the "id" chatMessage.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<ChatMessageDTO> findOne(Long id);

    /**
     * Delete the "id" chatMessage.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
