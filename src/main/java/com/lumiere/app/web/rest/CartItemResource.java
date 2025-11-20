package com.lumiere.app.web.rest;

import com.lumiere.app.repository.CartItemRepository;
import com.lumiere.app.security.SecurityUtils;
import com.lumiere.app.service.CartItemService;
import com.lumiere.app.service.dto.CartItemDTO;
import com.lumiere.app.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CartItemResource {

    private final Logger log = LoggerFactory.getLogger(CartItemResource.class);

    private static final String ENTITY_NAME = "cartItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CartItemService cartItemService;

    private final CartItemRepository cartItemRepository;

    public CartItemResource(CartItemService cartItemService, CartItemRepository cartItemRepository) {
        this.cartItemService = cartItemService;
        this.cartItemRepository = cartItemRepository;
    }

    /**
     * {@code POST  /cart-items} : Create a new cartItem.
     */
    @PostMapping("/cart-items")
    public ResponseEntity<CartItemDTO> createCartItem(
        @RequestBody CartItemDTO cartItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to save CartItem : {}", cartItemDTO);
        if (cartItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new cartItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Long userId = SecurityUtils.getCurrentUserId().get();
        CartItemDTO result = cartItemService.createCartItem(cartItemDTO,userId);
        return ResponseEntity
            .created(new URI("/api/cart-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /cart-items/:id} : Updates an existing cartItem.
     */
    @PutMapping("/cart-items/{id}")
    public ResponseEntity<CartItemDTO> updateCartItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CartItemDTO cartItemDTO
    ) {
        log.debug("REST request to update CartItem : {}, {}", id, cartItemDTO);
        if (cartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!id.equals(cartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        CartItemDTO result = cartItemService.update(cartItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /cart-items/:id} : Partial updates given fields of an existing cartItem.
     */
    @PatchMapping(value = "/cart-items/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<CartItemDTO> partialUpdateCartItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody CartItemDTO cartItemDTO
    ) {
        log.debug("REST request to partial update CartItem partially : {}, {}", id, cartItemDTO);
        if (cartItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!id.equals(cartItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!cartItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<CartItemDTO> result = cartItemService.partialUpdate(cartItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, cartItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /cart-items} : get all the cartItems.
     */
    @GetMapping("/cart-items")
    public ResponseEntity<List<CartItemDTO>> getAllCartItems(Pageable pageable) {
        log.debug("REST request to get a page of CartItems");
        Page<CartItemDTO> page = cartItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
            ServletUriComponentsBuilder.fromCurrentRequest(),
            page
        );
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /cart-items/:id} : get the "id" cartItem.
     */
    @GetMapping("/cart-items/{id}")
    public ResponseEntity<CartItemDTO> getCartItem(@PathVariable Long id) {
        log.debug("REST request to get CartItem : {}", id);
        Optional<CartItemDTO> cartItemDTO = cartItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(cartItemDTO);
    }

    /**
     * {@code DELETE  /cart-items/:id} : delete the "id" cartItem.
     */
    @DeleteMapping("/cart-items/{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long id) {
        log.debug("REST request to delete CartItem : {}", id);
        cartItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
