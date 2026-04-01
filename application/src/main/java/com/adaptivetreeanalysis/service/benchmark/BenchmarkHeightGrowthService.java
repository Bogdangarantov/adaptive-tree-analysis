package com.adaptivetreeanalysis.service.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentStatEntity;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkHeightGrowthPointResponse;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkHeightGrowthResponse;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkHeightGrowthSeriesResponse;
import com.adaptivetreeanalysis.repository.benchmark.ExperimentStatRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BenchmarkHeightGrowthService {

    private final ExperimentStatRepository experimentStatRepository;

    public BenchmarkHeightGrowthService(ExperimentStatRepository experimentStatRepository) {
        this.experimentStatRepository = experimentStatRepository;
    }

    @Transactional(readOnly = true)
    public BenchmarkHeightGrowthResponse getHeightGrowth() {
        List<ExperimentStatEntity> stats = experimentStatRepository.findAll();
        if (stats.isEmpty()) {
            return new BenchmarkHeightGrowthResponse(0, List.of());
        }

        List<BenchmarkHeightGrowthSeriesResponse> series = stats.stream()
                .filter(entity -> entity.getExperiment() != null && entity.getExperiment().getDataset() != null)
                .filter(entity -> entity.getExperiment().getDataset().getSize() != null)
                .filter(entity -> entity.getTreeHeight() != null)
                .collect(java.util.stream.Collectors.groupingBy(entity -> entity.getTreeType().value()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new BenchmarkHeightGrowthSeriesResponse(
                        entry.getKey(),
                        buildPoints(entry.getValue())
                ))
                .toList();

        return new BenchmarkHeightGrowthResponse(stats.size(), series);
    }

    private List<BenchmarkHeightGrowthPointResponse> buildPoints(List<ExperimentStatEntity> stats) {
        return stats.stream()
                .collect(java.util.stream.Collectors.groupingBy(entity -> entity.getExperiment().getDataset().getSize()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new BenchmarkHeightGrowthPointResponse(
                        entry.getKey(),
                        averageHeight(entry.getValue()),
                        entry.getValue().size()
                ))
                .filter(point -> !Double.isNaN(point.averageTreeHeight()))
                .sorted(Comparator.comparingInt(BenchmarkHeightGrowthPointResponse::datasetSize))
                .toList();
    }

    private double averageHeight(List<ExperimentStatEntity> stats) {
        return stats.stream()
                .map(ExperimentStatEntity::getTreeHeight)
                .filter(Objects::nonNull)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(Double.NaN);
    }
}
