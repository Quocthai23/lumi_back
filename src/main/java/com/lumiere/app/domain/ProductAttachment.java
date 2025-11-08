package com.lumiere.app.domain;

import jakarta.persistence.*;

import java.io.Serializable;

// ProductAttachment.java
@Entity
@Table(name = "product_attachment")
public class ProductAttachment implements Serializable {

    @EmbeddedId
    private ProductAttachmentId id = new ProductAttachmentId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("attachmentId")
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "caption", length = 255)
    private String caption;

    public ProductAttachment() {}

    public ProductAttachment(Product product, Attachment attachment, Integer sortOrder, String caption) {
        this.product = product;
        this.attachment = attachment;
        this.id = new ProductAttachmentId(product.getId(), attachment.getId());
        this.sortOrder = sortOrder;
        this.caption = caption;
    }

    public ProductAttachmentId getId() {
        return id;
    }

    public void setId(ProductAttachmentId id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Attachment getAttachment() {
        return attachment;
    }

    public void setAttachment(Attachment attachment) {
        this.attachment = attachment;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }
}
