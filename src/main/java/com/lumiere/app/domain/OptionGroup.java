// src/main/java/com/lumi/app/domain/OptionGroup.java
package com.lumiere.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "option_group",
       uniqueConstraints = @UniqueConstraint(name = "uk_option_group_product_code", columnNames = {"product_id","code"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionGroup implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Thuộc product nào */
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_option_group_product"))
    private Product product;

    @NotBlank
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /** Mã nhóm (duy nhất trong 1 product) */
    @NotBlank
    @Column(name = "code", nullable = false, length = 100)
    private String code;

    /** Thứ tự hiển thị */
    @Column(name = "position")
    private Integer position;

    @OneToMany(mappedBy = "optionGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC, id ASC")
    private Set<OptionSelect> selects = new LinkedHashSet<>();
}
