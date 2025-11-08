package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Notification;
import com.lumiere.app.repository.NotificationRepository;
import com.lumiere.app.service.NotificationService;
import com.lumiere.app.service.dto.NotificationDTO;
import com.lumiere.app.service.mapper.NotificationMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(NotificationRepository notificationRepository, NotificationMapper notificationMapper) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) {
        LOG.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public NotificationDTO update(NotificationDTO notificationDTO) {
        LOG.debug("Request to update Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        return notificationMapper.toDto(notification);
    }

    @Override
    public Optional<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        LOG.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .map(notificationRepository::save)
            .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDTO> findAll() {
        LOG.debug("Request to get all Notifications");
        return notificationRepository.findAll().stream().map(notificationMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<NotificationDTO> findAllWithEagerRelationships(Pageable pageable) {
        return notificationRepository.findAllWithEagerRelationships(pageable).map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        LOG.debug("Request to get Notification : {}", id);
        return notificationRepository.findOneWithEagerRelationships(id).map(notificationMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
    }

    /** Page-based (chuẩn JHipster) */
    @Override
    public Page<NotificationDTO> getAdminNotifications(Pageable pageable) {
        // Gợi ý sort mặc định: createdDate desc nếu client không truyền
        Pageable p = pageable.getSort().isSorted()
            ? pageable
            : PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdAt"));

        return notificationRepository.findAllByCustomerIdIsNull(p)
            .map(notificationMapper::toDto);
    }

    /** Infinite scroll (keyset) */
    @Override
    public Slice<NotificationDTO> scrollAdminNotifications(Long lastId, int size) {
        PageRequest pr = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "id"));

        Slice<Notification> slice = (lastId == null)
            ? notificationRepository.findAllByCustomerIdIsNullOrderByIdDesc(pr)
            : notificationRepository.findAllByCustomerIdIsNullAndIdLessThanOrderByIdDesc(lastId, pr);

        return slice.map(notificationMapper::toDto);
    }}
