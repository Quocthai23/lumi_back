package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Voucher;
import com.lumiere.app.repository.VoucherRepository;
import com.lumiere.app.service.VoucherService;
import com.lumiere.app.service.dto.VoucherDTO;
import com.lumiere.app.service.mapper.VoucherMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Voucher}.
 */
@Service
@Transactional
public class VoucherServiceImpl implements VoucherService {

    private static final Logger LOG = LoggerFactory.getLogger(VoucherServiceImpl.class);

    private final VoucherRepository voucherRepository;

    private final VoucherMapper voucherMapper;

    public VoucherServiceImpl(VoucherRepository voucherRepository, VoucherMapper voucherMapper) {
        this.voucherRepository = voucherRepository;
        this.voucherMapper = voucherMapper;
    }

    @Override
    public VoucherDTO save(VoucherDTO voucherDTO) {
        LOG.debug("Request to save Voucher : {}", voucherDTO);
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        voucher = voucherRepository.save(voucher);
        return voucherMapper.toDto(voucher);
    }

    @Override
    public VoucherDTO update(VoucherDTO voucherDTO) {
        LOG.debug("Request to update Voucher : {}", voucherDTO);
        Voucher voucher = voucherMapper.toEntity(voucherDTO);
        voucher = voucherRepository.save(voucher);
        return voucherMapper.toDto(voucher);
    }

    @Override
    public Optional<VoucherDTO> partialUpdate(VoucherDTO voucherDTO) {
        LOG.debug("Request to partially update Voucher : {}", voucherDTO);

        return voucherRepository
            .findById(voucherDTO.getId())
            .map(existingVoucher -> {
                voucherMapper.partialUpdate(existingVoucher, voucherDTO);

                return existingVoucher;
            })
            .map(voucherRepository::save)
            .map(voucherMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VoucherDTO> findAll() {
        LOG.debug("Request to get all Vouchers");
        return voucherRepository.findAll().stream().map(voucherMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VoucherDTO> findOne(Long id) {
        LOG.debug("Request to get Voucher : {}", id);
        return voucherRepository.findById(id).map(voucherMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Voucher : {}", id);
        voucherRepository.deleteById(id);
    }
}
