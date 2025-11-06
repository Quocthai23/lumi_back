package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.QuestionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Lob;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.ProductQuestion} entity.
 */
@Schema(description = "Câu hỏi về sản phẩm.\nFrontend: src/types/qa.ts (Question)")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductQuestionDTO implements Serializable {

    private Long id;

    @NotNull
    private String author;

    @Lob
    private String questionText;

    @NotNull
    private QuestionStatus status;

    @NotNull
    private Instant createdAt;

    private ProductDTO product;

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

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductQuestionDTO)) {
            return false;
        }

        ProductQuestionDTO productQuestionDTO = (ProductQuestionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productQuestionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductQuestionDTO{" +
            "id=" + getId() +
            ", author='" + getAuthor() + "'" +
            ", questionText='" + getQuestionText() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", product=" + getProduct() +
            "}";
    }
}
