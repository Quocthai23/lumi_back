package com.lumiere.app.repository;

import com.lumiere.app.domain.Collection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class CollectionRepositoryWithBagRelationshipsImpl implements CollectionRepositoryWithBagRelationships {

    private static final String ID_PARAMETER = "id";
    private static final String COLLECTIONS_PARAMETER = "collections";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Collection> fetchBagRelationships(Optional<Collection> collection) {
        return collection.map(this::fetchProducts);
    }

    @Override
    public Page<Collection> fetchBagRelationships(Page<Collection> collections) {
        return new PageImpl<>(fetchBagRelationships(collections.getContent()), collections.getPageable(), collections.getTotalElements());
    }

    @Override
    public List<Collection> fetchBagRelationships(List<Collection> collections) {
        return Optional.of(collections).map(this::fetchProducts).orElse(Collections.emptyList());
    }

    Collection fetchProducts(Collection result) {
        return entityManager
            .createQuery(
                "select collection from Collection collection left join fetch collection.products where collection.id = :id",
                Collection.class
            )
            .setParameter(ID_PARAMETER, result.getId())
            .getSingleResult();
    }

    List<Collection> fetchProducts(List<Collection> collections) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, collections.size()).forEach(index -> order.put(collections.get(index).getId(), index));
        List<Collection> result = entityManager
            .createQuery(
                "select collection from Collection collection left join fetch collection.products where collection in :collections",
                Collection.class
            )
            .setParameter(COLLECTIONS_PARAMETER, collections)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
