package com.adaptivetreeanalysis.domain.benchmark.entity;

import com.adaptivetreeanalysis.domain.benchmark.converter.ExperimentStatusConverter;
import com.adaptivetreeanalysis.domain.benchmark.enums.ExperimentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "experiments")
public class ExperimentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "dataset_id", nullable = false)
    private DatasetEntity dataset;

    @Column(nullable = false)
    private String title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "operations_profile", nullable = false, columnDefinition = "jsonb")
    private String operationsProfile;

    @Convert(converter = ExperimentStatusConverter.class)
    @Column(nullable = false)
    private ExperimentStatus status;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public DatasetEntity getDataset() {
        return dataset;
    }

    public void setDataset(DatasetEntity dataset) {
        this.dataset = dataset;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOperationsProfile() {
        return operationsProfile;
    }

    public void setOperationsProfile(String operationsProfile) {
        this.operationsProfile = operationsProfile;
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public void setStatus(ExperimentStatus status) {
        this.status = status;
    }

    public OffsetDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(OffsetDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public OffsetDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(OffsetDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
