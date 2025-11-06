package com.lumiere.app.service.dto;

import com.lumiere.app.domain.enumeration.VoucherStatus;
import com.lumiere.app.domain.enumeration.VoucherType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.Voucher} entity.
 */
@Schema(description = "Mã giảm giá.\nFrontend: src/types/voucher.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class VoucherDTO implements Serializable {

    private Long id;

    @NotNull
    private String code;

    @NotNull
    private VoucherType type;

    @NotNull
    @DecimalMin(value = "0")
    private BigDecimal value;

    @NotNull
    private VoucherStatus status;

    private Instant startDate;

    private Instant endDate;

    private Integer usageLimit;

    private Integer usageCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public VoucherType getType() {
        return type;
    }

    public void setType(VoucherType type) {
        this.type = type;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public VoucherStatus getStatus() {
        return status;
    }

    public void setStatus(VoucherStatus status) {
        this.status = status;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }

    public Instant getEndDate() {
        return endDate;
    }

    public void setEndDate(Instant endDate) {
        this.endDate = endDate;
    }

    public Integer getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(Integer usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Integer getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VoucherDTO)) {
            return false;
        }

        VoucherDTO voucherDTO = (VoucherDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, voucherDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VoucherDTO{" +
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
