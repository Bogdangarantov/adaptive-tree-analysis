package com.adaptivetreeanalysis.service.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.entity.DatasetEntity;
import com.adaptivetreeanalysis.domain.benchmark.enums.DatasetDistributionType;
import com.adaptivetreeanalysis.dto.benchmark.CreateDatasetRequest;
import com.adaptivetreeanalysis.dto.benchmark.DatasetResponse;
import com.adaptivetreeanalysis.repository.benchmark.DatasetRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class DatasetService {

    private final DatasetRepository datasetRepository;

    public DatasetService(DatasetRepository datasetRepository) {
        this.datasetRepository = datasetRepository;
    }

    @Transactional
    public DatasetResponse create(CreateDatasetRequest request) {
        DatasetEntity entity = new DatasetEntity();
        entity.setName(request.name());
        entity.setDistributionType(parseDistributionType(request.distributionType()));
        entity.setSize(request.size());
        entity.setSeed(request.seed());

        DatasetEntity saved = datasetRepository.save(entity);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DatasetResponse> list() {
        return datasetRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public DatasetEntity getEntityOrThrow(UUID id) {
        return datasetRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Dataset not found: " + id));
    }

    private DatasetDistributionType parseDistributionType(String value) {
        try {
            return DatasetDistributionType.fromValue(value);
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage());
        }
    }

    private DatasetResponse toResponse(DatasetEntity entity) {
        return new DatasetResponse(
                entity.getId(),
                entity.getName(),
                entity.getDistributionType().value(),
                entity.getSize(),
                entity.getSeed(),
                null,
                entity.getCreatedAt()
        );
    }
}
