package com.adaptivetreeanalysis.repository.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.entity.DatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DatasetRepository extends JpaRepository<DatasetEntity, UUID> {
}
