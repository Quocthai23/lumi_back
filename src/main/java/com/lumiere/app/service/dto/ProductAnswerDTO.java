package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.ProductAnswer} entity.
 */
@Schema(description = "Trả lời cho câu hỏi.\nFrontend: src/types/qa.ts (Answer)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductAnswerDTO implements Serializable {

    private Long id;

    @NotNull
    private String author;

    @Lob
    private String answerText;

    @NotNull
    private Instant createdAt;

    private ProductQuestionDTO question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ProductQuestionDTO getQuestion() {
        return question;
    }

    public void setQuestion(ProductQuestionDTO question) {
        this.question = question;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductAnswerDTO)) {
            return false;
        }

        ProductAnswerDTO productAnswerDTO = (ProductAnswerDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productAnswerDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductAnswerDTO{" +
            "id=" + getId() +
            ", author='" + getAuthor() + "'" +
            ", answerText='" + getAnswerText() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", question=" + getQuestion() +
            "}";
    }
}
