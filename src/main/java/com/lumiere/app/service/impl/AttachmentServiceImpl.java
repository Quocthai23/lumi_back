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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class AttachmentServiceImpl implements AttachmentService {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentServiceImpl.class);

    private final AttachmentRepository attachmentRepository;

    private final AttachmentMapper attachmentMapper;
    private static final String BUCKET_NAME = "demofirebase-6e7a1.appspot.com";
    private final Storage storage;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository, AttachmentMapper attachmentMapper) {
        this.attachmentRepository = attachmentRepository;
        this.attachmentMapper = attachmentMapper;
        if(StorageClient.getInstance() != null){
            this.storage = StorageClient.getInstance().bucket().getStorage();
        }else{
            this.storage = null;
        }
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
        LOG.debug("Request to delete Attachment : {}", id);
        attachmentRepository.deleteById(id);
    }

    @Override
    public void uploadAttachment(MultipartFile file, AttachmentDTO attachmentDTO) throws IOException {
        String fileName = UUID.randomUUID()+".jpg";
        BlobId blobId = BlobId.of(BUCKET_NAME, fileName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
            .setContentType(file.getContentType())
            .build();
        storage.create(blobInfo, file.getBytes());
        attachmentDTO.setName(fileName);
    }
}
