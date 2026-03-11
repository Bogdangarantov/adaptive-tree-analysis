package com.adaptivetreeanalysis.repository.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExperimentRepository extends JpaRepository<ExperimentEntity, UUID> {
}
