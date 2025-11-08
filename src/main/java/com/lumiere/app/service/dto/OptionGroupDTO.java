// src/main/java/com/lumiere/app/service/dto/OptionGroupDTO.java
package com.lumiere.app.service.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionGroupDTO {
  private Long id;
  private Long productId;
  private String name;
  private String code;
  private Integer position;
}
