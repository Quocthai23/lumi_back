package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Address;
import com.lumiere.app.repository.AddressRepository;
import com.lumiere.app.service.AddressService;
import com.lumiere.app.service.dto.AddressDTO;
import com.lumiere.app.service.mapper.AddressMapper;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Address}.
 */
@Service
@Transactional
public class AddressServiceImpl implements AddressService {

    private static final Logger LOG = LoggerFactory.getLogger(AddressServiceImpl.class);

    private final AddressRepository addressRepository;

    private final AddressMapper addressMapper;

    public AddressServiceImpl(AddressRepository addressRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.addressMapper = addressMapper;
    }

    @Override
    public AddressDTO save(AddressDTO addressDTO) {
        LOG.debug("Request to save Address : {}", addressDTO);
        Address address = addressMapper.toEntity(addressDTO);
        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    public AddressDTO update(AddressDTO addressDTO) {
        LOG.debug("Request to update Address : {}", addressDTO);
        Address address = addressMapper.toEntity(addressDTO);
        address = addressRepository.save(address);
        return addressMapper.toDto(address);
    }

    @Override
    public Optional<AddressDTO> partialUpdate(AddressDTO addressDTO) {
        LOG.debug("Request to partially update Address : {}", addressDTO);

        return addressRepository
            .findById(addressDTO.getId())
            .map(existingAddress -> {
                addressMapper.partialUpdate(existingAddress, addressDTO);

                return existingAddress;
            })
            .map(addressRepository::save)
            .map(addressMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> findAll() {
        LOG.debug("Request to get all Addresses");
        return addressRepository.findAll().stream().map(addressMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    public Page<AddressDTO> findAllWithEagerRelationships(Pageable pageable) {
        return addressRepository.findAllWithEagerRelationships(pageable).map(addressMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AddressDTO> findOne(Long id) {
        LOG.debug("Request to get Address : {}", id);
        return addressRepository.findOneWithEagerRelationships(id).map(addressMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Address : {}", id);
        addressRepository.deleteById(id);
    }
}
