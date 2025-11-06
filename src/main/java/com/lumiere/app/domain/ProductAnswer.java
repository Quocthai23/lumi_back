package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Trả lời cho câu hỏi.
 * Frontend: src/types/qa.ts (Answer)
 */
@Entity
@Table(name = "product_answer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductAnswer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "author", nullable = false)
    private String author;

    @Lob
    @Column(name = "answer_text", nullable = false)
    private String answerText;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "product", "answers" }, allowSetters = true)
    private ProductQuestion question;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductAnswer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return this.author;
    }

    public ProductAnswer author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAnswerText() {
        return this.answerText;
    }

    public ProductAnswer answerText(String answerText) {
        this.setAnswerText(answerText);
        return this;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ProductAnswer createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public ProductQuestion getQuestion() {
        return this.question;
    }

    public void setQuestion(ProductQuestion productQuestion) {
        this.question = productQuestion;
    }

    public ProductAnswer question(ProductQuestion productQuestion) {
        this.setQuestion(productQuestion);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductAnswer)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductAnswer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductAnswer{" +
            "id=" + getId() +
            ", author='" + getAuthor() + "'" +
            ", answerText='" + getAnswerText() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
