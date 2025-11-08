package com.lumiere.app.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

// ProductAttachmentId.java (khóa tổng hợp)
@Embeddable
public class ProductAttachmentId implements Serializable {
    @Column(name = "product_id")
    private Long productId;

    @Column(name = "attachment_id")
    private Long attachmentId;

    // constructors, getters/setters, equals/hashCode

    public ProductAttachmentId() {}

    public ProductAttachmentId(Long productId, Long attachmentId) {
        this.productId = productId;
        this.attachmentId = attachmentId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getAttachmentId() {
        return attachmentId;
    }

    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
}
