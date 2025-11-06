package com.lumiere.app.domain;

import com.lumiere.app.domain.enumeration.VoucherStatus;
import com.lumiere.app.domain.enumeration.VoucherType;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Mã giảm giá.
 * Frontend: src/types/voucher.ts
 */
@Entity
@Table(name = "voucher")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private VoucherType type;

    @NotNull
    @DecimalMin(value = "0")
    @Column(name = "value", precision = 21, scale = 2, nullable = false)
    private BigDecimal value;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VoucherStatus status;

    @Column(name = "start_date")
    private Instant startDate;

    @Column(name = "end_date")
    private Instant endDate;

    @Column(name = "usage_limit")
    private Integer usageLimit;

    @Column(name = "usage_count")
    private Integer usageCount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Voucher id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return this.code;
    }

    public Voucher code(String code) {
        this.setCode(code);
        return this;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VoucherType getType() {
        return this.type;
    }

    public Voucher type(VoucherType type) {
        this.setType(type);
        return this;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public Voucher value(BigDecimal value) {
        this.setValue(value);
        return this;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public VoucherStatus getStatus() {
        return this.status;
    }

    public Voucher status(VoucherStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public Instant getStartDate() {
        return this.startDate;
    }

    public Voucher startDate(Instant startDate) {
        this.setStartDate(startDate);
        return this;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return this.endDate;
    }

    public Voucher endDate(Instant endDate) {
        this.setEndDate(endDate);
        return this;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Integer getUsageLimit() {
        return this.usageLimit;
    }

    public Voucher usageLimit(Integer usageLimit) {
        this.setUsageLimit(usageLimit);
        return this;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsageCount() {
        return this.usageCount;
    }

    public Voucher usageCount(Integer usageCount) {
        this.setUsageCount(usageCount);
        return this;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Voucher)) {
            return false;
        }
        return getId() != null && getId().equals(((Voucher) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Voucher{" +
            "id=" + getId() +
            ", code='" + getCode() + "'" +
            ", type='" + getType() + "'" +
            ", value=" + getValue() +
            ", status='" + getStatus() + "'" +
            ", startDate='" + getStartDate() + "'" +
            ", endDate='" + getEndDate() + "'" +
            ", usageLimit=" + getUsageLimit() +
            ", usageCount=" + getUsageCount() +
            "}";
    }
}
