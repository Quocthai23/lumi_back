package com.lumiere.app.service.impl;

import com.lumiere.app.domain.FlashSale;
import com.lumiere.app.domain.enumeration.NotificationType;
import com.lumiere.app.repository.FlashSaleRepository;
import com.lumiere.app.service.FlashSaleService;
import com.lumiere.app.service.dto.FlashSaleDTO;
import com.lumiere.app.service.kafka.NotificationProducerService;
import com.lumiere.app.service.mapper.FlashSaleMapper;
import java.time.Instant;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.FlashSale}.
 */
@Service
@Transactional
public class FlashSaleServiceImpl implements FlashSaleService {

    private static final Logger LOG = LoggerFactory.getLogger(FlashSaleServiceImpl.class);

    private final FlashSaleRepository flashSaleRepository;

    private final FlashSaleMapper flashSaleMapper;

    private final NotificationProducerService notificationProducerService;

    public FlashSaleServiceImpl(
        FlashSaleRepository flashSaleRepository, 
        FlashSaleMapper flashSaleMapper,
        NotificationProducerService notificationProducerService
    ) {
        this.flashSaleRepository = flashSaleRepository;
        this.flashSaleMapper = flashSaleMapper;
        this.notificationProducerService = notificationProducerService;
    }

    @Override
    public FlashSaleDTO save(FlashSaleDTO flashSaleDTO) {
        LOG.debug("Request to save FlashSale : {}", flashSaleDTO);
        validateFlashSale(flashSaleDTO);
        FlashSale flashSale = flashSaleMapper.toEntity(flashSaleDTO);
        boolean isNew = flashSale.getId() == null;
        flashSale = flashSaleRepository.save(flashSale);
        
        // Gửi notification cho tất cả customers về flashsale mới
        if (isNew && flashSale.getStartTime() != null && flashSale.getStartTime().isAfter(java.time.Instant.now())) {
            // Note: Trong thực tế, có thể cần gửi cho từng customer hoặc broadcast
            // Ở đây tôi sẽ gửi notification cho admin để admin biết có flashsale mới
            // và có thể gửi email cho customers
            String adminMessage = String.format("FlashSale mới đã được tạo: %s (Bắt đầu: %s)", 
                flashSale.getName(), flashSale.getStartTime());
            notificationProducerService.sendAdminNotification(
                NotificationType.NEW_FLASHSALE,
                adminMessage,
                "/admin/flash-sales/" + flashSale.getId()
            );
        }
        
        return flashSaleMapper.toDto(flashSale);
    }

    private void validateFlashSale(FlashSaleDTO flashSaleDTO) {
        if (flashSaleDTO.getName() == null || flashSaleDTO.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên flash sale không được để trống");
        }

        if (flashSaleDTO.getStartTime() == null) {
            throw new IllegalArgumentException("Thời gian bắt đầu không được để trống");
        }

        if (flashSaleDTO.getEndTime() == null) {
            throw new IllegalArgumentException("Thời gian kết thúc không được để trống");
        }

        if (flashSaleDTO.getEndTime().isBefore(flashSaleDTO.getStartTime())) {
            throw new IllegalArgumentException("Thời gian kết thúc phải sau thời gian bắt đầu");
        }

        if (flashSaleDTO.getStartTime().isBefore(Instant.now()) && flashSaleDTO.getEndTime().isBefore(Instant.now())) {
            LOG.warn("Flash sale đã kết thúc: {}", flashSaleDTO.getName());
        }
    }

    @Override
    public FlashSaleDTO update(FlashSaleDTO flashSaleDTO) {
        LOG.debug("Request to update FlashSale : {}", flashSaleDTO);
        validateFlashSale(flashSaleDTO);
        FlashSale flashSale = flashSaleMapper.toEntity(flashSaleDTO);
        flashSale = flashSaleRepository.save(flashSale);
        return flashSaleMapper.toDto(flashSale);
    }

    @Override
    public Optional<FlashSaleDTO> partialUpdate(FlashSaleDTO flashSaleDTO) {
        LOG.debug("Request to partially update FlashSale : {}", flashSaleDTO);

        return flashSaleRepository
            .findById(flashSaleDTO.getId())
            .map(existingFlashSale -> {
                flashSaleMapper.partialUpdate(existingFlashSale, flashSaleDTO);

                return existingFlashSale;
            })
            .map(flashSaleRepository::save)
            .map(flashSaleMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleDTO> findAll() {
        LOG.debug("Request to get all FlashSales");
        return flashSaleRepository.findAll().stream().map(flashSaleMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashSaleDTO> findOne(Long id) {
        LOG.debug("Request to get FlashSale : {}", id);
        return flashSaleRepository.findById(id).map(flashSaleMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete FlashSale : {}", id);
        flashSaleRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleDTO> findActiveFlashSales() {
        LOG.debug("Request to get all active FlashSales");
        Instant now = Instant.now();
        return flashSaleRepository.findActiveFlashSales(now).stream().map(flashSaleMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleDTO> findUpcomingFlashSales() {
        LOG.debug("Request to get all upcoming FlashSales");
        Instant now = Instant.now();
        return flashSaleRepository.findUpcomingFlashSales(now).stream().map(flashSaleMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashSaleDTO> findEndedFlashSales() {
        LOG.debug("Request to get all ended FlashSales");
        Instant now = Instant.now();
        return flashSaleRepository.findEndedFlashSales(now).stream().map(flashSaleMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FlashSaleDTO> findCurrentFlashSale() {
        LOG.debug("Request to get current FlashSale");
        Instant now = Instant.now();
        List<FlashSale> currentFlashSales = flashSaleRepository.findCurrentFlashSale(now);
        if (currentFlashSales.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(flashSaleMapper.toDto(currentFlashSales.get(0)));
    }
}
