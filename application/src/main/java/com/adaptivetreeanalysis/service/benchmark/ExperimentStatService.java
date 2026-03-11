package com.adaptivetreeanalysis.service.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentEntity;
import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentStatEntity;
import com.adaptivetreeanalysis.domain.benchmark.enums.TreeType;
import com.adaptivetreeanalysis.dto.benchmark.CreateExperimentStatRequest;
import com.adaptivetreeanalysis.dto.benchmark.ExperimentStatResponse;
import com.adaptivetreeanalysis.repository.benchmark.ExperimentStatRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExperimentStatService {

    private final ExperimentService experimentService;
    private final ExperimentStatRepository experimentStatRepository;

    public ExperimentStatService(ExperimentService experimentService, ExperimentStatRepository experimentStatRepository) {
        this.experimentService = experimentService;
        this.experimentStatRepository = experimentStatRepository;
    }

    @Transactional
    public ExperimentStatResponse create(UUID experimentId, CreateExperimentStatRequest request) {
        ExperimentEntity experiment = experimentService.getEntityOrThrow(experimentId);

        ExperimentStatEntity entity = new ExperimentStatEntity();
        entity.setExperiment(experiment);
        entity.setTreeType(parseTreeType(request.treeType()));
        entity.setAlgorithmVersion(request.algorithmVersion());
        entity.setExecutionTimeNs(request.executionTimeNs());
        entity.setOperationCount(request.operationCount());
        entity.setRotationCount(request.rotationCount());
        entity.setTreeHeight(request.treeHeight());
        entity.setRebalancesCount(request.rebalancesCount());
        entity.setAvgNodeDepth(request.avgNodeDepth());
        entity.setMaxNodeDepth(request.maxNodeDepth());
        entity.setMemoryBytes(request.memoryBytes());

        try {
            ExperimentStatEntity saved = experimentStatRepository.save(entity);
            return toResponse(saved);
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stats for this experiment and treeType already exist or violate DB constraints"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<ExperimentStatResponse> listByExperimentId(UUID experimentId) {
        experimentService.getEntityOrThrow(experimentId);
        return experimentStatRepository.findByExperimentIdOrderByCreatedAtAsc(experimentId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private TreeType parseTreeType(String value) {
        try {
            return TreeType.fromValue(value);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    private ExperimentStatResponse toResponse(ExperimentStatEntity entity) {
        return new ExperimentStatResponse(
                entity.getId(),
                entity.getExperiment().getId(),
                entity.getTreeType().value(),
                entity.getAlgorithmVersion(),
                entity.getExecutionTimeNs(),
                entity.getOperationCount(),
                entity.getRotationCount(),
                entity.getTreeHeight(),
                entity.getRebalancesCount(),
                entity.getAvgNodeDepth(),
                entity.getMaxNodeDepth(),
                entity.getMemoryBytes(),
                entity.getCreatedAt()
        );
    }
}
