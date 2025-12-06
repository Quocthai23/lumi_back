package com.lumiere.app.repository;

import com.lumiere.app.domain.ChatMessage;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChatMessage entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    /**
     * Tìm tất cả messages của một session, sắp xếp theo timestamp.
     *
     * @param sessionId ID của session
     * @return danh sách messages
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.session.id = :sessionId ORDER BY m.timestamp ASC")
    List<ChatMessage> findBySessionIdOrderByTimestampAsc(@Param("sessionId") Long sessionId);

    /**
     * Tìm tất cả messages của một contactMessage, sắp xếp theo timestamp.
     *
     * @param contactMessageId ID của contactMessage
     * @return danh sách messages
     */
    @Query("SELECT m FROM ChatMessage m WHERE m.contactMessage.id = :contactMessageId ORDER BY m.timestamp ASC")
    List<ChatMessage> findByContactMessageIdOrderByTimestampAsc(@Param("contactMessageId") Long contactMessageId);
}
