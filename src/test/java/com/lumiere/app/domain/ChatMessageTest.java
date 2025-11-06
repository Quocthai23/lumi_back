package com.lumiere.app.domain;

import static com.lumiere.app.domain.ChatMessageTestSamples.*;
import static com.lumiere.app.domain.ChatSessionTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.lumiere.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChatMessageTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChatMessage.class);
        ChatMessage chatMessage1 = getChatMessageSample1();
        ChatMessage chatMessage2 = new ChatMessage();
        assertThat(chatMessage1).isNotEqualTo(chatMessage2);

        chatMessage2.setId(chatMessage1.getId());
        assertThat(chatMessage1).isEqualTo(chatMessage2);

        chatMessage2 = getChatMessageSample2();
        assertThat(chatMessage1).isNotEqualTo(chatMessage2);
    }

    @Test
    void sessionTest() {
        ChatMessage chatMessage = getChatMessageRandomSampleGenerator();
        ChatSession chatSessionBack = getChatSessionRandomSampleGenerator();

        chatMessage.setSession(chatSessionBack);
        assertThat(chatMessage.getSession()).isEqualTo(chatSessionBack);

        chatMessage.session(null);
        assertThat(chatMessage.getSession()).isNull();
    }
}
