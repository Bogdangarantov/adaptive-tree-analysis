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
import java.util.stream.Collectors;
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
        List<ExperimentStatEntity> stats = experimentStatRepository.findAll().stream()
                .filter(entity -> entity.getTreeHeight() != null)
                .filter(entity -> entity.getExperiment() != null)
                .filter(entity -> entity.getExperiment().getDataset() != null)
                .filter(entity -> entity.getExperiment().getDataset().getSize() != null)
                .toList();

        if (stats.isEmpty()) {
            return new BenchmarkHeightGrowthResponse(0, List.of());
        }

        Map<String, List<ExperimentStatEntity>> byTreeType = stats.stream()
                .collect(Collectors.groupingBy(entity -> entity.getTreeType().value()));

        List<BenchmarkHeightGrowthSeriesResponse> series = byTreeType.entrySet().stream()
                .map(entry -> buildSeries(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(BenchmarkHeightGrowthSeriesResponse::treeType))
                .toList();

        return new BenchmarkHeightGrowthResponse(stats.size(), series);
    }

    private BenchmarkHeightGrowthSeriesResponse buildSeries(String treeType, List<ExperimentStatEntity> stats) {
        Map<Integer, List<ExperimentStatEntity>> bySize = stats.stream()
                .collect(Collectors.groupingBy(entity -> entity.getExperiment().getDataset().getSize()));

        List<BenchmarkHeightGrowthPointResponse> points = bySize.entrySet().stream()
                .map(entry -> {
                    double averageHeight = entry.getValue().stream()
                            .map(ExperimentStatEntity::getTreeHeight)
                            .filter(Objects::nonNull)
                            .mapToInt(Integer::intValue)
                            .average()
                            .orElse(0);
                    return new BenchmarkHeightGrowthPointResponse(
                            entry.getKey(),
                            averageHeight,
                            entry.getValue().size()
                    );
                })
                .sorted(Comparator.comparing(BenchmarkHeightGrowthPointResponse::datasetSize))
                .toList();

        return new BenchmarkHeightGrowthSeriesResponse(treeType, points);
    }
}
