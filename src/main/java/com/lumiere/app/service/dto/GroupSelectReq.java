// src/main/java/com/lumiere/app/service/dto/GroupSelectReq.java
package com.lumiere.app.service.dto;

import lombok.Data;
import java.util.List;

@Data
public class GroupSelectReq {
  private Long groupId;
  private List<Long> selectIds;
}
