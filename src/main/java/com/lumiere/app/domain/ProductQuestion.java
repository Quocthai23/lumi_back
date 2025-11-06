package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumiere.app.domain.enumeration.QuestionStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Câu hỏi về sản phẩm.
 * Frontend: src/types/qa.ts (Question)
 */
@Entity
@Table(name = "product_question")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProductQuestion implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "author", nullable = false)
    private String author;

    @Lob
    @Column(name = "question_text", nullable = false)
    private String questionText;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private QuestionStatus status;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = { "variants", "reviews", "questions", "collections", "wishlistedBies" }, allowSetters = true)
    private Product product;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "question")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "question" }, allowSetters = true)
    private Set<ProductAnswer> answers = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ProductQuestion id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAuthor() {
        return this.author;
    }

    public ProductQuestion author(String author) {
        this.setAuthor(author);
        return this;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getQuestionText() {
        return this.questionText;
    }

    public ProductQuestion questionText(String questionText) {
        this.setQuestionText(questionText);
        return this;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public QuestionStatus getStatus() {
        return this.status;
    }

    public ProductQuestion status(QuestionStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public ProductQuestion createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Product getProduct() {
        return this.product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductQuestion product(Product product) {
        this.setProduct(product);
        return this;
    }

    public Set<ProductAnswer> getAnswers() {
        return this.answers;
    }

    public void setAnswers(Set<ProductAnswer> productAnswers) {
        if (this.answers != null) {
            this.answers.forEach(i -> i.setQuestion(null));
        }
        if (productAnswers != null) {
            productAnswers.forEach(i -> i.setQuestion(this));
        }
        this.answers = productAnswers;
    }

    public ProductQuestion answers(Set<ProductAnswer> productAnswers) {
        this.setAnswers(productAnswers);
        return this;
    }

    public ProductQuestion addAnswers(ProductAnswer productAnswer) {
        this.answers.add(productAnswer);
        productAnswer.setQuestion(this);
        return this;
    }

    public ProductQuestion removeAnswers(ProductAnswer productAnswer) {
        this.answers.remove(productAnswer);
        productAnswer.setQuestion(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductQuestion)) {
            return false;
        }
        return getId() != null && getId().equals(((ProductQuestion) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductQuestion{" +
            "id=" + getId() +
            ", author='" + getAuthor() + "'" +
            ", questionText='" + getQuestionText() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
