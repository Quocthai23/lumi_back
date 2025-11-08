package com.lumiere.app.service.mapper;

import com.lumiere.app.domain.Attachment;
import com.lumiere.app.service.dto.AttachmentDTO;
import org.mapstruct.Mapper;

/**
 */
@Mapper(componentModel = "spring")
public interface AttachmentMapper extends EntityMapper<AttachmentDTO, Attachment> {}
