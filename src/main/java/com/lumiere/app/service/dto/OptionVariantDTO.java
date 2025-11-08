// src/main/java/com/lumiere/app/service/dto/OptionVariantDTO.java
package com.lumiere.app.service.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OptionVariantDTO {
  private Long id;
  private Long productVariantId;
  private Long optionSelectId;
}
