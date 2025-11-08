package com.lumiere.app.service.impl;

import com.lumiere.app.domain.Attachment;
import com.lumiere.app.domain.Product;
import com.lumiere.app.domain.ProductAttachment;
import com.lumiere.app.domain.ProductAttachmentId;
import com.lumiere.app.repository.AttachmentRepository;
import com.lumiere.app.repository.ProductAttachmentRepository;
import com.lumiere.app.repository.ProductRepository;
import com.lumiere.app.service.AttachmentService;
import com.lumiere.app.service.ProductService;
import com.lumiere.app.service.dto.AttachmentDTO;
import com.lumiere.app.service.dto.ProductDTO;
import com.lumiere.app.service.mapper.AttachmentMapper;
import com.lumiere.app.service.mapper.ProductMapper;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import com.lumiere.app.utils.CodeUtils;
import com.lumiere.app.utils.SlugUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.lumiere.app.domain.Product}.
 */
@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;
    private final SecureRandom random = new SecureRandom();
    private final AttachmentService attachmentService;
    private final AttachmentRepository attachmentRepository;
    private final ProductAttachmentRepository productAttachmentRepository;
    private final AttachmentMapper attachmentMapper;

    public ProductServiceImpl(ProductRepository productRepository, ProductMapper productMapper, AttachmentService attachmentService, AttachmentRepository attachmentRepository, ProductAttachmentRepository productAttachmentRepository, AttachmentMapper attachmentMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.attachmentService = attachmentService;
        this.attachmentRepository = attachmentRepository;
        this.productAttachmentRepository = productAttachmentRepository;
        this.attachmentMapper = attachmentMapper;
    }

    @Override
    public ProductDTO save(ProductDTO productDTO) {
        LOG.debug("Request to save Product : {}", productDTO);
        Product product = productMapper.toEntity(productDTO);
        product = productRepository.save(product);
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDTO update(ProductDTO productDTO) {
        LOG.debug("Request to update Product : {}", productDTO);

        // 1. Chuyển DTO -> entity (chỉ để update các field cơ bản)
        Product product = productMapper.toEntity(productDTO);

        // 2. Lưu product trước (đảm bảo có id)
        product = productRepository.save(product);

        Long productId = product.getId();
        // Lấy danh sách attachmentId từ list AttachmentDTO
        List<Long> desiredAttachmentIds = productDTO.getAttachmentDTOS() != null
            ? productDTO.getAttachmentDTOS().stream()
            .map(AttachmentDTO::getId)
            .filter(Objects::nonNull)
            .toList()
            : Collections.emptyList();

        // 3. Đọc các attachment hiện tại trong DB
        List<ProductAttachment> existingLinks = productAttachmentRepository.findAllByProductId(productId);
        Set<Long> currentAttachmentIds = existingLinks.stream()
            .map(pa -> pa.getId().getAttachmentId())
            .collect(Collectors.toSet());

        // 4. Tính các phần cần thêm và cần xóa
        Set<Long> toAdd = new HashSet<>(desiredAttachmentIds);
        toAdd.removeAll(currentAttachmentIds);

        Set<Long> toRemove = new HashSet<>(currentAttachmentIds);
        desiredAttachmentIds.forEach(toRemove::remove);

        // 5. Thêm các liên kết mới
        if (!toAdd.isEmpty()) {
            Product finalProduct = product;
            List<ProductAttachment> newLinks = toAdd.stream()
                .map(attId -> {
                    ProductAttachmentId id = new ProductAttachmentId();
                    id.setProductId(productId);
                    id.setAttachmentId(attId);
                    Attachment attachmentRef = attachmentRepository.getReferenceById(attId);
                    ProductAttachment pa = new ProductAttachment();
                    pa.setProduct(finalProduct);
                    pa.setAttachment(attachmentRef);
                    pa.setId(id);
                    return pa;
                })
                .toList();
            productAttachmentRepository.saveAll(newLinks);
        }

        // 6. Xóa các liên kết thừa
        if (!toRemove.isEmpty()) {
            productAttachmentRepository.deleteByProductIdAndAttachmentIds(productId, toRemove);
        }

        // 7. Trả lại DTO đã update
        return productMapper.toDto(product);
    }


    @Transactional
    @Override
    public ProductDTO createProductDTO(ProductDTO dto) {
        // 1) Lấy set attachment DTO người dùng gửi lên
        final Set<AttachmentDTO> incomingDtos =
            Optional.ofNullable(dto.getAttachmentDTOS()).orElseGet(HashSet::new);

        dto.setSlug(SlugUtils.toSlug(dto.getName()));
        dto.setCode(CodeUtils.randomAlphaNum(10,random));
        dto.setCreatedAt(Instant.now());

        // 2) Lưu product trước để có id (create/update)
        dto = this.save(dto); // <-- method bạn đã có, trả về DTO đã có id
        final Long productId = dto.getId();
        if (productId == null) {
            throw new IllegalStateException("Product ID is null after save()");
        }

        // 3) Lấy danh sách attachmentId yêu cầu (lọc null)
        //    Nếu có item chưa có id nhưng có url -> cố gắng resolve id từ url
        Set<Long> desiredIds = incomingDtos.stream()
            .map(a -> {
                if (a.getId() != null) return a.getId();
                if (a.getUrl() != null) {
                    Long resolved = attachmentRepository.findIdByUrl(a.getUrl()).orElse(null);
                    if (resolved != null) return resolved;
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new));

        // 4) Trạng thái hiện tại
        List<ProductAttachment> exists = productAttachmentRepository.findAllByProductId(productId);
        Set<Long> currentIds = exists.stream()
            .map(pa -> pa.getId().getAttachmentId())
            .collect(Collectors.toSet());

// 5) Tính toAdd / toRemove
        Set<Long> toAdd = new LinkedHashSet<>(desiredIds);
        toAdd.removeAll(currentIds);

        Set<Long> toRemove = new LinkedHashSet<>(currentIds);
        toRemove.removeAll(desiredIds);

// Lấy reference để dùng cho @MapsId (không hit DB full entity)
        Product productRef = productRepository.getReferenceById(productId);

// 6) Thêm liên kết mới (PHẢI set associations cho @MapsId)
        if (!toAdd.isEmpty()) {
            List<ProductAttachment> news = toAdd.stream()
                .map(attId -> {
                    Attachment attachmentRef = attachmentRepository.getReferenceById(attId);
                    ProductAttachment pa = new ProductAttachment();
                    pa.setProduct(productRef);
                    pa.setAttachment(attachmentRef);
                    return pa;
                })
                .toList();

            productAttachmentRepository.saveAll(news);
        }

// 7) Xoá liên kết thừa
        if (!toRemove.isEmpty()) {
            productAttachmentRepository.deleteByProductIdAndAttachmentIdIn(productId, toRemove);
        }

// 8) Nếu người dùng gửi rỗng → xoá hết
        if (desiredIds.isEmpty() && !currentIds.isEmpty()) {
            productAttachmentRepository.deleteByProductId(productId);
        }

// 9) Trả DTO cuối cùng
        List<Attachment> attached =  attachmentRepository.findAllByIds(desiredIds);
        dto.setAttachmentDTOS(
            attached.stream()
                .map(attachmentMapper::toDto)
                .collect(Collectors.toCollection(LinkedHashSet::new))
        );
        return dto;

    }

    @Override
    public Optional<ProductDTO> partialUpdate(ProductDTO productDTO) {
        LOG.debug("Request to partially update Product : {}", productDTO);

        return productRepository
            .findById(productDTO.getId())
            .map(existingProduct -> {
                productMapper.partialUpdate(existingProduct, productDTO);

                return existingProduct;
            })
            .map(productRepository::save)
            .map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findOne(Long id) {
        LOG.debug("Request to get Product : {}", id);
        Optional<ProductDTO> productDTO = productRepository.findById(id).map(productMapper::toDto);
        if(productDTO.isPresent()){
            List<ProductAttachment> productAttachments = productAttachmentRepository.findAllByProductId(id);
            List<AttachmentDTO> attachmentDTOS = attachmentService.findAllByIdIn(productAttachments.stream().map(productAttachment -> productAttachment.getId().getAttachmentId()).toList());
            productDTO.get().setAttachmentDTOS(new HashSet<>(attachmentDTOS));
        }
        return productDTO;
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Product : {}", id);
        productRepository.deleteById(id);
    }
}
