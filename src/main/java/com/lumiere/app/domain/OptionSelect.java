// src/main/java/com/lumi/app/domain/OptionSelect.java
package com.lumiere.app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "option_select",
       uniqueConstraints = @UniqueConstraint(name = "uk_option_select_group_code", columnNames = {"option_group_id","code"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionSelect implements Serializable {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_group_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_option_select_group"))
    private OptionGroup optionGroup;

    @NotBlank
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    /** CODE duy nhất trong 1 group (dùng build SKU) */
    @NotBlank
    @Column(name = "code", nullable = false, length = 100)
    private String code;

    @Column(name = "position")
    private Integer position;

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;
}
