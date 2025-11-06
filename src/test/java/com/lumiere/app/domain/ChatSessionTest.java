package com.lumiere.app.domain;

import static com.lumiere.app.domain.ChatMessageTestSamples.*;
import static com.lumiere.app.domain.ChatSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ChatSessionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatSession.class);
        ChatSession chatSession1 = getChatSessionSample1();
        ChatSession chatSession2 = new ChatSession();
        assertThat(chatSession1).isNotEqualTo(chatSession2);

        chatSession2.setId(chatSession1.getId());
        assertThat(chatSession1).isEqualTo(chatSession2);

        chatSession2 = getChatSessionSample2();
        assertThat(chatSession1).isNotEqualTo(chatSession2);
    }

    @Test
    void messagesTest() {
        ChatSession chatSession = getChatSessionRandomSampleGenerator();
        ChatMessage chatMessageBack = getChatMessageRandomSampleGenerator();

        chatSession.addMessages(chatMessageBack);
        assertThat(chatSession.getMessages()).containsOnly(chatMessageBack);
        assertThat(chatMessageBack.getSession()).isEqualTo(chatSession);

        chatSession.removeMessages(chatMessageBack);
        assertThat(chatSession.getMessages()).doesNotContain(chatMessageBack);
        assertThat(chatMessageBack.getSession()).isNull();

        chatSession.messages(new HashSet<>(Set.of(chatMessageBack)));
        assertThat(chatSession.getMessages()).containsOnly(chatMessageBack);
        assertThat(chatMessageBack.getSession()).isEqualTo(chatSession);

        chatSession.setMessages(new HashSet<>());
        assertThat(chatSession.getMessages()).doesNotContain(chatMessageBack);
        assertThat(chatMessageBack.getSession()).isNull();
    }
}
