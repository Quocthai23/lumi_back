// src/main/java/com/lumi/app/domain/OptionVariant.java
package com.lumiere.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "option_variant",
       uniqueConstraints = @UniqueConstraint(name = "uk_option_variant", columnNames = {"product_variant_id","option_select_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionVariant implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Biến thể sản phẩm */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_option_variant_variant"))
    private ProductVariant productVariant;

    /** Lựa chọn thuộc tính đã chọn */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_select_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_option_variant_select"))
    private OptionSelect optionSelect;
}
