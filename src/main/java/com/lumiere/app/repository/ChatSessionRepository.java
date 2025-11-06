package com.lumiere.app.repository;

import com.lumiere.app.domain.ChatSession;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the ChatSession entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {}
