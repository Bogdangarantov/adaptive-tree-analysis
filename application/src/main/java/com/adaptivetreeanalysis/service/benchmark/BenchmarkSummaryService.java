package com.adaptivetreeanalysis.service.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentStatEntity;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkSummaryResponse;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkTreeSummaryResponse;
import com.adaptivetreeanalysis.repository.benchmark.ExperimentStatRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.ToLongFunction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BenchmarkSummaryService {

    private final ExperimentStatRepository experimentStatRepository;

    public BenchmarkSummaryService(ExperimentStatRepository experimentStatRepository) {
        this.experimentStatRepository = experimentStatRepository;
    }

    @Transactional(readOnly = true)
    public BenchmarkSummaryResponse getSummary() {
        List<ExperimentStatEntity> stats = experimentStatRepository.findAll();
        if (stats.isEmpty()) {
            return new BenchmarkSummaryResponse(0, 0, 0, 0, List.of());
        }

        List<BenchmarkTreeSummaryResponse> trees = stats.stream()
                .collect(java.util.stream.Collectors.groupingBy(entity -> entity.getTreeType().value()))
                .entrySet()
                .stream()
                .map(entry -> summarizeTree(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(BenchmarkTreeSummaryResponse::treeType))
                .toList();

        long totalRuns = stats.size();
        long totalOperations = stats.stream()
                .map(ExperimentStatEntity::getOperationCount)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();

        return new BenchmarkSummaryResponse(
                totalRuns,
                totalOperations,
                average(stats, ExperimentStatEntity::getExecutionTimeNs),
                min(stats, ExperimentStatEntity::getExecutionTimeNs),
                trees
        );
    }

    private BenchmarkTreeSummaryResponse summarizeTree(String treeType, List<ExperimentStatEntity> stats) {
        long totalOperations = stats.stream()
                .map(ExperimentStatEntity::getOperationCount)
                .filter(Objects::nonNull)
                .mapToLong(Integer::longValue)
                .sum();

        return new BenchmarkTreeSummaryResponse(
                treeType,
                stats.size(),
                totalOperations,
                average(stats, ExperimentStatEntity::getExecutionTimeNs),
                min(stats, ExperimentStatEntity::getExecutionTimeNs),
                averageDouble(stats, entity -> entity.getTreeHeight() == null ? 0 : entity.getTreeHeight()),
                averageNullable(stats, ExperimentStatEntity::getInsertTimeNs),
                averageNullable(stats, ExperimentStatEntity::getSearchTimeNs),
                averageNullable(stats, ExperimentStatEntity::getDeleteTimeNs)
        );
    }

    private long average(List<ExperimentStatEntity> stats, ToLongFunction<ExperimentStatEntity> extractor) {
        return Math.round(stats.stream().mapToLong(extractor).average().orElse(0));
    }

    private long min(List<ExperimentStatEntity> stats, ToLongFunction<ExperimentStatEntity> extractor) {
        return stats.stream().mapToLong(extractor).min().orElse(0);
    }

    private double averageDouble(List<ExperimentStatEntity> stats, ToLongFunction<ExperimentStatEntity> extractor) {
        return stats.stream().mapToLong(extractor).average().orElse(0);
    }

    private long averageNullable(List<ExperimentStatEntity> stats, Function<ExperimentStatEntity, Long> extractor) {
        return Math.round(stats.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .mapToLong(Long::longValue)
                .average()
                .orElse(0));
    }
}
