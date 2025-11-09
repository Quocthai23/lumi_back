package com.lumiere.app.service.impl;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.cloud.StorageClient;

import com.lumiere.app.domain.Attachment;
import com.lumiere.app.repository.AttachmentRepository;
import com.lumiere.app.service.AttachmentService;
import com.lumiere.app.service.dto.AttachmentDTO;
import com.lumiere.app.service.mapper.AttachmentMapper;
import com.lumiere.app.utils.PublicUrlBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    private final AttachmentRepository attachmentRepository;
    private final Path mediaRoot;

    private final AttachmentMapper attachmentMapper;
    private final PublicUrlBuilder urlBuilder;


    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, Path mediaRoot, AttachmentMapper attachmentMapper, PublicUrlBuilder urlBuilder) {
        this.attachmentRepository = attachmentRepository;
        this.mediaRoot = mediaRoot;
        this.attachmentMapper = attachmentMapper;
        this.urlBuilder = urlBuilder;
    }

    @Override
    public AttachmentDTO save(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to save Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(attachment);
    }

    @Override
    public AttachmentDTO update(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to update Attachment : {}", attachmentDTO);
        Attachment attachment = attachmentMapper.toEntity(attachmentDTO);
        attachment = attachmentRepository.save(attachment);
        return attachmentMapper.toDto(attachment);
    }

    @Override
    public Optional<AttachmentDTO> partialUpdate(AttachmentDTO attachmentDTO) {
        LOG.debug("Request to partially update Attachment : {}", attachmentDTO);

        return attachmentRepository
            .findById(attachmentDTO.getId())
            .map(existingAttachment -> {
                attachmentMapper.partialUpdate(existingAttachment, attachmentDTO);

                return existingAttachment;
            })
            .map(attachmentRepository::save)
            .map(attachmentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttachmentDTO> findAll() {
        LOG.debug("Request to get all Attachments");
        return attachmentRepository.findAll().stream().map(attachmentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public List<AttachmentDTO> findAllByIdIn(List<Long> ids){
        return attachmentRepository.findAllByIds(ids).stream().map(attachmentMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AttachmentDTO> findOne(Long id) {
        LOG.debug("Request to get Attachment : {}", id);
        return attachmentRepository.findById(id).map(attachmentMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        attachmentRepository.findById(id).ifPresent(att -> {
            String name = att.getName();
            if (name != null && name.matches("^[a-fA-F0-9\\-]{8,}\\.\\w{1,10}$")) {
                try {
                    Files.deleteIfExists(mediaRoot.resolve("attachments").resolve(name).normalize());
                } catch (Exception ignored) {}
            }
            attachmentRepository.delete(att);
        });
    }


}
