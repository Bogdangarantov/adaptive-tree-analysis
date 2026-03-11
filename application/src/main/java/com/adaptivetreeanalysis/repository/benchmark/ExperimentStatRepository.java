package com.adaptivetreeanalysis.repository.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentStatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ExperimentStatRepository extends JpaRepository<ExperimentStatEntity, UUID> {
    List<ExperimentStatEntity> findByExperimentIdOrderByCreatedAtAsc(UUID experimentId);
}
