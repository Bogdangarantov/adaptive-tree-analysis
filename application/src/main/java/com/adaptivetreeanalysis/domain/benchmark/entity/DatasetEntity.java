package com.adaptivetreeanalysis.domain.benchmark.entity;

import com.adaptivetreeanalysis.domain.benchmark.converter.DatasetDistributionTypeConverter;
import com.adaptivetreeanalysis.domain.benchmark.enums.DatasetDistributionType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "datasets")
public class DatasetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Convert(converter = DatasetDistributionTypeConverter.class)
    @Column(name = "distribution_type", nullable = false)
    private DatasetDistributionType distributionType;

    @Column(nullable = false)
    private Integer size;

    @Column
    private Long seed;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DatasetDistributionType getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(DatasetDistributionType distributionType) {
        this.distributionType = distributionType;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
