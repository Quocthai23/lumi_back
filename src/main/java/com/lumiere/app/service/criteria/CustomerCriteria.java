package com.lumiere.app.service.criteria;

import com.lumiere.app.domain.enumeration.CustomerTier;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.lumiere.app.domain.Customer} entity. This class is used
 * in {@link com.lumiere.app.web.rest.CustomerResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customers?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CustomerCriteria implements Serializable, Criteria {

    /**
     * Class for filtering CustomerTier
     */
    public static class CustomerTierFilter extends Filter<CustomerTier> {

        public CustomerTierFilter() {}

        public CustomerTierFilter(CustomerTierFilter filter) {
            super(filter);
        }

        @Override
        public CustomerTierFilter copy() {
            return new CustomerTierFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter firstName;

    private StringFilter lastName;

    private StringFilter phone;

    private CustomerTierFilter tier;

    private IntegerFilter loyaltyPoints;

    private LongFilter userId;

    private LongFilter ordersId;

    private LongFilter wishlistId;

    private LongFilter addressesId;

    private LongFilter loyaltyHistoryId;

    private LongFilter notificationsId;

    private Boolean distinct;

    public CustomerCriteria() {}

    public CustomerCriteria(CustomerCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.firstName = other.optionalFirstName().map(StringFilter::copy).orElse(null);
        this.lastName = other.optionalLastName().map(StringFilter::copy).orElse(null);
        this.phone = other.optionalPhone().map(StringFilter::copy).orElse(null);
        this.tier = other.optionalTier().map(CustomerTierFilter::copy).orElse(null);
        this.loyaltyPoints = other.optionalLoyaltyPoints().map(IntegerFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(LongFilter::copy).orElse(null);
        this.ordersId = other.optionalOrdersId().map(LongFilter::copy).orElse(null);
        this.wishlistId = other.optionalWishlistId().map(LongFilter::copy).orElse(null);
        this.addressesId = other.optionalAddressesId().map(LongFilter::copy).orElse(null);
        this.loyaltyHistoryId = other.optionalLoyaltyHistoryId().map(LongFilter::copy).orElse(null);
        this.notificationsId = other.optionalNotificationsId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public CustomerCriteria copy() {
        return new CustomerCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public Optional<StringFilter> optionalFirstName() {
        return Optional.ofNullable(firstName);
    }

    public StringFilter firstName() {
        if (firstName == null) {
            setFirstName(new StringFilter());
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public Optional<StringFilter> optionalLastName() {
        return Optional.ofNullable(lastName);
    }

    public StringFilter lastName() {
        if (lastName == null) {
            setLastName(new StringFilter());
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public StringFilter getPhone() {
        return phone;
    }

    public Optional<StringFilter> optionalPhone() {
        return Optional.ofNullable(phone);
    }

    public StringFilter phone() {
        if (phone == null) {
            setPhone(new StringFilter());
        }
        return phone;
    }

    public void setPhone(StringFilter phone) {
        this.phone = phone;
    }

    public CustomerTierFilter getTier() {
        return tier;
    }

    public Optional<CustomerTierFilter> optionalTier() {
        return Optional.ofNullable(tier);
    }

    public CustomerTierFilter tier() {
        if (tier == null) {
            setTier(new CustomerTierFilter());
        }
        return tier;
    }

    public void setTier(CustomerTierFilter tier) {
        this.tier = tier;
    }

    public IntegerFilter getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public Optional<IntegerFilter> optionalLoyaltyPoints() {
        return Optional.ofNullable(loyaltyPoints);
    }

    public IntegerFilter loyaltyPoints() {
        if (loyaltyPoints == null) {
            setLoyaltyPoints(new IntegerFilter());
        }
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(IntegerFilter loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public LongFilter getUserId() {
        return userId;
    }

    public Optional<LongFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public LongFilter userId() {
        if (userId == null) {
            setUserId(new LongFilter());
        }
        return userId;
    }

    public void setUserId(LongFilter userId) {
        this.userId = userId;
    }

    public LongFilter getOrdersId() {
        return ordersId;
    }

    public Optional<LongFilter> optionalOrdersId() {
        return Optional.ofNullable(ordersId);
    }

    public LongFilter ordersId() {
        if (ordersId == null) {
            setOrdersId(new LongFilter());
        }
        return ordersId;
    }

    public void setOrdersId(LongFilter ordersId) {
        this.ordersId = ordersId;
    }

    public LongFilter getWishlistId() {
        return wishlistId;
    }

    public Optional<LongFilter> optionalWishlistId() {
        return Optional.ofNullable(wishlistId);
    }

    public LongFilter wishlistId() {
        if (wishlistId == null) {
            setWishlistId(new LongFilter());
        }
        return wishlistId;
    }

    public void setWishlistId(LongFilter wishlistId) {
        this.wishlistId = wishlistId;
    }

    public LongFilter getAddressesId() {
        return addressesId;
    }

    public Optional<LongFilter> optionalAddressesId() {
        return Optional.ofNullable(addressesId);
    }

    public LongFilter addressesId() {
        if (addressesId == null) {
            setAddressesId(new LongFilter());
        }
        return addressesId;
    }

    public void setAddressesId(LongFilter addressesId) {
        this.addressesId = addressesId;
    }

    public LongFilter getLoyaltyHistoryId() {
        return loyaltyHistoryId;
    }

    public Optional<LongFilter> optionalLoyaltyHistoryId() {
        return Optional.ofNullable(loyaltyHistoryId);
    }

    public LongFilter loyaltyHistoryId() {
        if (loyaltyHistoryId == null) {
            setLoyaltyHistoryId(new LongFilter());
        }
        return loyaltyHistoryId;
    }

    public void setLoyaltyHistoryId(LongFilter loyaltyHistoryId) {
        this.loyaltyHistoryId = loyaltyHistoryId;
    }

    public LongFilter getNotificationsId() {
        return notificationsId;
    }

    public Optional<LongFilter> optionalNotificationsId() {
        return Optional.ofNullable(notificationsId);
    }

    public LongFilter notificationsId() {
        if (notificationsId == null) {
            setNotificationsId(new LongFilter());
        }
        return notificationsId;
    }

    public void setNotificationsId(LongFilter notificationsId) {
        this.notificationsId = notificationsId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CustomerCriteria that = (CustomerCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(phone, that.phone) &&
            Objects.equals(tier, that.tier) &&
            Objects.equals(loyaltyPoints, that.loyaltyPoints) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(ordersId, that.ordersId) &&
            Objects.equals(wishlistId, that.wishlistId) &&
            Objects.equals(addressesId, that.addressesId) &&
            Objects.equals(loyaltyHistoryId, that.loyaltyHistoryId) &&
            Objects.equals(notificationsId, that.notificationsId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            firstName,
            lastName,
            phone,
            tier,
            loyaltyPoints,
            userId,
            ordersId,
            wishlistId,
            addressesId,
            loyaltyHistoryId,
            notificationsId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalFirstName().map(f -> "firstName=" + f + ", ").orElse("") +
            optionalLastName().map(f -> "lastName=" + f + ", ").orElse("") +
            optionalPhone().map(f -> "phone=" + f + ", ").orElse("") +
            optionalTier().map(f -> "tier=" + f + ", ").orElse("") +
            optionalLoyaltyPoints().map(f -> "loyaltyPoints=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalOrdersId().map(f -> "ordersId=" + f + ", ").orElse("") +
            optionalWishlistId().map(f -> "wishlistId=" + f + ", ").orElse("") +
            optionalAddressesId().map(f -> "addressesId=" + f + ", ").orElse("") +
            optionalLoyaltyHistoryId().map(f -> "loyaltyHistoryId=" + f + ", ").orElse("") +
            optionalNotificationsId().map(f -> "notificationsId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
