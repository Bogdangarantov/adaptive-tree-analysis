package com.adaptivetreeanalysis.domain.benchmark.entity;

import com.adaptivetreeanalysis.domain.benchmark.converter.TreeTypeConverter;
import com.adaptivetreeanalysis.domain.benchmark.enums.TreeType;
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
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "experiment_stats")
public class ExperimentStatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "experiment_id", nullable = false)
    private ExperimentEntity experiment;

    @Convert(converter = TreeTypeConverter.class)
    @Column(name = "tree_type", nullable = false)
    private TreeType treeType;

    @Column(name = "algorithm_version")
    private String algorithmVersion;

    @Column(name = "execution_time_ns", nullable = false)
    private Long executionTimeNs;

    @Column(name = "insert_time_ns")
    private Long insertTimeNs;

    @Column(name = "search_time_ns")
    private Long searchTimeNs;

    @Column(name = "delete_time_ns")
    private Long deleteTimeNs;

    @Column(name = "operation_count", nullable = false)
    private Integer operationCount;

    @Column(name = "rotation_count", nullable = false)
    private Integer rotationCount;

    @Column(name = "tree_height", nullable = false)
    private Integer treeHeight;

    @Column(name = "rebalances_count")
    private Integer rebalancesCount;

    @Column(name = "avg_node_depth", precision = 10, scale = 4)
    private BigDecimal avgNodeDepth;

    @Column(name = "max_node_depth")
    private Integer maxNodeDepth;

    @Column(name = "memory_bytes")
    private Long memoryBytes;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public ExperimentEntity getExperiment() {
        return experiment;
    }

    public void setExperiment(ExperimentEntity experiment) {
        this.experiment = experiment;
    }

    public TreeType getTreeType() {
        return treeType;
    }

    public void setTreeType(TreeType treeType) {
        this.treeType = treeType;
    }

    public String getAlgorithmVersion() {
        return algorithmVersion;
    }

    public void setAlgorithmVersion(String algorithmVersion) {
        this.algorithmVersion = algorithmVersion;
    }

    public Long getExecutionTimeNs() {
        return executionTimeNs;
    }

    public void setExecutionTimeNs(Long executionTimeNs) {
        this.executionTimeNs = executionTimeNs;
    }

    public Integer getOperationCount() {
        return operationCount;
    }

    public Long getInsertTimeNs() {
        return insertTimeNs;
    }

    public void setInsertTimeNs(Long insertTimeNs) {
        this.insertTimeNs = insertTimeNs;
    }

    public Long getSearchTimeNs() {
        return searchTimeNs;
    }

    public void setSearchTimeNs(Long searchTimeNs) {
        this.searchTimeNs = searchTimeNs;
    }

    public Long getDeleteTimeNs() {
        return deleteTimeNs;
    }

    public void setDeleteTimeNs(Long deleteTimeNs) {
        this.deleteTimeNs = deleteTimeNs;
    }

    public void setOperationCount(Integer operationCount) {
        this.operationCount = operationCount;
    }

    public Integer getRotationCount() {
        return rotationCount;
    }

    public void setRotationCount(Integer rotationCount) {
        this.rotationCount = rotationCount;
    }

    public Integer getTreeHeight() {
        return treeHeight;
    }

    public void setTreeHeight(Integer treeHeight) {
        this.treeHeight = treeHeight;
    }

    public Integer getRebalancesCount() {
        return rebalancesCount;
    }

    public void setRebalancesCount(Integer rebalancesCount) {
        this.rebalancesCount = rebalancesCount;
    }

    public BigDecimal getAvgNodeDepth() {
        return avgNodeDepth;
    }

    public void setAvgNodeDepth(BigDecimal avgNodeDepth) {
        this.avgNodeDepth = avgNodeDepth;
    }

    public Integer getMaxNodeDepth() {
        return maxNodeDepth;
    }

    public void setMaxNodeDepth(Integer maxNodeDepth) {
        this.maxNodeDepth = maxNodeDepth;
    }

    public Long getMemoryBytes() {
        return memoryBytes;
    }

    public void setMemoryBytes(Long memoryBytes) {
        this.memoryBytes = memoryBytes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
