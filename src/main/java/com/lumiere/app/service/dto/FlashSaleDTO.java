package com.lumiere.app.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.lumiere.app.domain.FlashSale} entity.
 */
@Schema(description = "Sự kiện Flash Sale.\nFrontend: src/types/flashSale.ts")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class FlashSaleDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    @NotNull
    private Instant startTime;

    @NotNull
    private Instant endTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FlashSaleDTO)) {
            return false;
        }

        FlashSaleDTO flashSaleDTO = (FlashSaleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, flashSaleDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "FlashSaleDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", startTime='" + getStartTime() + "'" +
            ", endTime='" + getEndTime() + "'" +
            "}";
    }
}
