package com.lumiere.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.lumiere.app.domain.enumeration.CustomerTier;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Hồ sơ khách hàng, liên kết với User của JHipster.
 * Frontend: src/types/customer.ts
 * @filter
 */
@Entity
@Table(name = "customer")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class  Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone")
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier")
    private CustomerTier tier;

    @Min(value = 0)
    @Column(name = "loyalty_points")
    private Integer loyaltyPoints;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(unique = true)
    private User user;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer", "orderItems", "orderStatusHistories" }, allowSetters = true)
    private Set<Orders> orders = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "rel_customer__wishlist",
        joinColumns = @JoinColumn(name = "customer_id"),
        inverseJoinColumns = @JoinColumn(name = "wishlist_id")
    )
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "variants", "reviews", "questions", "collections", "wishlistedBies" }, allowSetters = true)
    private Set<Product> wishlists = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private Set<Address> addresses = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private Set<LoyaltyTransaction> loyaltyHistories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "customer")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "customer" }, allowSetters = true)
    private Set<Notification> notifications = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Customer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public Customer firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public Customer lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return this.phone;
    }

    public Customer phone(String phone) {
        this.setPhone(phone);
        return this;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public CustomerTier getTier() {
        return this.tier;
    }

    public Customer tier(CustomerTier tier) {
        this.setTier(tier);
        return this;
    }

    public void setTier(CustomerTier tier) {
        this.tier = tier;
    }

    public Integer getLoyaltyPoints() {
        return this.loyaltyPoints;
    }

    public Customer loyaltyPoints(Integer loyaltyPoints) {
        this.setLoyaltyPoints(loyaltyPoints);
        return this;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Customer user(User user) {
        this.setUser(user);
        return this;
    }

    public Set<Orders> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<Orders> orders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setCustomer(null));
        }
        if (orders != null) {
            orders.forEach(i -> i.setCustomer(this));
        }
        this.orders = orders;
    }

    public Customer orders(Set<Orders> orders) {
        this.setOrders(orders);
        return this;
    }

    public Customer addOrders(Orders orders) {
        this.orders.add(orders);
        orders.setCustomer(this);
        return this;
    }

    public Customer removeOrders(Orders orders) {
        this.orders.remove(orders);
        orders.setCustomer(null);
        return this;
    }

    public Set<Product> getWishlists() {
        return this.wishlists;
    }

    public void setWishlists(Set<Product> products) {
        this.wishlists = products;
    }

    public Customer wishlists(Set<Product> products) {
        this.setWishlists(products);
        return this;
    }

    public Customer addWishlist(Product product) {
        this.wishlists.add(product);
        return this;
    }

    public Customer removeWishlist(Product product) {
        this.wishlists.remove(product);
        return this;
    }

    public Set<Address> getAddresses() {
        return this.addresses;
    }

    public void setAddresses(Set<Address> addresses) {
        if (this.addresses != null) {
            this.addresses.forEach(i -> i.setCustomer(null));
        }
        if (addresses != null) {
            addresses.forEach(i -> i.setCustomer(this));
        }
        this.addresses = addresses;
    }

    public Customer addresses(Set<Address> addresses) {
        this.setAddresses(addresses);
        return this;
    }

    public Customer addAddresses(Address address) {
        this.addresses.add(address);
        address.setCustomer(this);
        return this;
    }

    public Customer removeAddresses(Address address) {
        this.addresses.remove(address);
        address.setCustomer(null);
        return this;
    }

    public Set<LoyaltyTransaction> getLoyaltyHistories() {
        return this.loyaltyHistories;
    }

    public void setLoyaltyHistories(Set<LoyaltyTransaction> loyaltyTransactions) {
        if (this.loyaltyHistories != null) {
            this.loyaltyHistories.forEach(i -> i.setCustomer(null));
        }
        if (loyaltyTransactions != null) {
            loyaltyTransactions.forEach(i -> i.setCustomer(this));
        }
        this.loyaltyHistories = loyaltyTransactions;
    }

    public Customer loyaltyHistories(Set<LoyaltyTransaction> loyaltyTransactions) {
        this.setLoyaltyHistories(loyaltyTransactions);
        return this;
    }

    public Customer addLoyaltyHistory(LoyaltyTransaction loyaltyTransaction) {
        this.loyaltyHistories.add(loyaltyTransaction);
        loyaltyTransaction.setCustomer(this);
        return this;
    }

    public Customer removeLoyaltyHistory(LoyaltyTransaction loyaltyTransaction) {
        this.loyaltyHistories.remove(loyaltyTransaction);
        loyaltyTransaction.setCustomer(null);
        return this;
    }

    public Set<Notification> getNotifications() {
        return this.notifications;
    }

    public void setNotifications(Set<Notification> notifications) {
        if (this.notifications != null) {
            this.notifications.forEach(i -> i.setCustomer(null));
        }
        if (notifications != null) {
            notifications.forEach(i -> i.setCustomer(this));
        }
        this.notifications = notifications;
    }

    public Customer notifications(Set<Notification> notifications) {
        this.setNotifications(notifications);
        return this;
    }

    public Customer addNotifications(Notification notification) {
        this.notifications.add(notification);
        notification.setCustomer(this);
        return this;
    }

    public Customer removeNotifications(Notification notification) {
        this.notifications.remove(notification);
        notification.setCustomer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer)) {
            return false;
        }
        return getId() != null && getId().equals(((Customer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Customer{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", phone='" + getPhone() + "'" +
            ", tier='" + getTier() + "'" +
            ", loyaltyPoints=" + getLoyaltyPoints() +
            "}";
    }
}
