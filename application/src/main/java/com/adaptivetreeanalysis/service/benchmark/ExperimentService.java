package com.adaptivetreeanalysis.service.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.entity.DatasetEntity;
import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentEntity;
import com.adaptivetreeanalysis.domain.benchmark.enums.ExperimentStatus;
import com.adaptivetreeanalysis.dto.benchmark.CreateExperimentRequest;
import com.adaptivetreeanalysis.dto.benchmark.ExperimentResponse;
import com.adaptivetreeanalysis.repository.benchmark.ExperimentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ExperimentService {

    private final DatasetService datasetService;
    private final ExperimentRepository experimentRepository;

    public ExperimentService(DatasetService datasetService, ExperimentRepository experimentRepository) {
        this.datasetService = datasetService;
        this.experimentRepository = experimentRepository;
    }

    @Transactional
    public ExperimentResponse create(CreateExperimentRequest request) {
        DatasetEntity dataset = datasetService.getEntityOrThrow(request.datasetId());

        ExperimentEntity entity = new ExperimentEntity();
        entity.setDataset(dataset);
        entity.setTitle(request.title());
        entity.setOperationsProfile(request.operationsProfile());
        entity.setStatus(ExperimentStatus.PENDING);

        ExperimentEntity saved = experimentRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ExperimentResponse getById(UUID id) {
        ExperimentEntity entity = getEntityOrThrow(id);
        return toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ExperimentResponse> list() {
        return experimentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ExperimentEntity getEntityOrThrow(UUID id) {
        return experimentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Experiment not found: " + id));
    }

    private ExperimentResponse toResponse(ExperimentEntity entity) {
        return new ExperimentResponse(
                entity.getId(),
                entity.getDataset().getId(),
                entity.getTitle(),
                entity.getOperationsProfile(),
                entity.getStatus().value(),
                null,
                entity.getStartedAt(),
                entity.getFinishedAt(),
                entity.getCreatedAt()
        );
    }
}
