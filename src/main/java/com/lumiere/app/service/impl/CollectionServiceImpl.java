package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Collection;
import com.lumiere.app.repository.CollectionRepository;
import com.lumiere.app.service.CollectionService;
import com.lumiere.app.service.dto.CollectionDTO;
import com.lumiere.app.service.mapper.CollectionMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Collection}.
 */
@Service
@Transactional
public class CollectionServiceImpl implements CollectionService {

    private static final Logger LOG = LoggerFactory.getLogger(CollectionServiceImpl.class);

    private final CollectionRepository collectionRepository;

    private final CollectionMapper collectionMapper;

    public CollectionServiceImpl(CollectionRepository collectionRepository, CollectionMapper collectionMapper) {
        this.collectionRepository = collectionRepository;
        this.collectionMapper = collectionMapper;
    }

    @Override
    public CollectionDTO save(CollectionDTO collectionDTO) {
        LOG.debug("Request to save Collection : {}", collectionDTO);
        Collection collection = collectionMapper.toEntity(collectionDTO);
        collection = collectionRepository.save(collection);
        return collectionMapper.toDto(collection);
    }

    @Override
    public CollectionDTO update(CollectionDTO collectionDTO) {
        LOG.debug("Request to update Collection : {}", collectionDTO);
        Collection collection = collectionMapper.toEntity(collectionDTO);
        collection = collectionRepository.save(collection);
        return collectionMapper.toDto(collection);
    }

    @Override
    public Optional<CollectionDTO> partialUpdate(CollectionDTO collectionDTO) {
        LOG.debug("Request to partially update Collection : {}", collectionDTO);

        return collectionRepository
            .findById(collectionDTO.getId())
            .map(existingCollection -> {
                collectionMapper.partialUpdate(existingCollection, collectionDTO);

                return existingCollection;
            })
            .map(collectionRepository::save)
            .map(collectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CollectionDTO> findAll(Pageable pageable) {
        LOG.debug("Request to get all Collections");
        return collectionRepository.findAll(pageable).map(collectionMapper::toDto);
    }

    public Page<CollectionDTO> findAllWithEagerRelationships(Pageable pageable) {
        return collectionRepository.findAllWithEagerRelationships(pageable).map(collectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CollectionDTO> findOne(Long id) {
        LOG.debug("Request to get Collection : {}", id);
        return collectionRepository.findOneWithEagerRelationships(id).map(collectionMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Collection : {}", id);
        collectionRepository.deleteById(id);
    }
}
