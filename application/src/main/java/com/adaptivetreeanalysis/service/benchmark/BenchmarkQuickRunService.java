package com.adaptivetreeanalysis.service.benchmark;

import com.adaptivetreeanalysis.domain.benchmark.enums.TreeType;
import com.adaptivetreeanalysis.domain.tree.AvlTree;
import com.adaptivetreeanalysis.domain.tree.IntTree;
import com.adaptivetreeanalysis.domain.tree.RedBlackTree;
import com.adaptivetreeanalysis.domain.tree.SplayTree;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkQuickRunRequest;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkQuickRunResponse;
import com.adaptivetreeanalysis.dto.benchmark.CreateDatasetRequest;
import com.adaptivetreeanalysis.dto.benchmark.CreateExperimentRequest;
import com.adaptivetreeanalysis.dto.benchmark.CreateExperimentStatRequest;
import com.adaptivetreeanalysis.dto.benchmark.DatasetResponse;
import com.adaptivetreeanalysis.dto.benchmark.ExperimentResponse;
import com.adaptivetreeanalysis.dto.benchmark.ExperimentStatResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BenchmarkQuickRunService {

    private static final Logger log = LoggerFactory.getLogger(BenchmarkQuickRunService.class);

    private final DatasetService datasetService;
    private final ExperimentService experimentService;
    private final ExperimentStatService experimentStatService;
    private final BenchmarkProgressService benchmarkProgressService;

    public BenchmarkQuickRunService(
            DatasetService datasetService,
            ExperimentService experimentService,
            ExperimentStatService experimentStatService,
            BenchmarkProgressService benchmarkProgressService
    ) {
        this.datasetService = datasetService;
        this.experimentService = experimentService;
        this.experimentStatService = experimentStatService;
        this.benchmarkProgressService = benchmarkProgressService;
    }

    /**
     * Creates dataset and experiment records, запускає справжній бенчмарк для всіх типів дерев
     * (AVL, Red-Black, Splay/Extensible) та зберігає метрики в experiment_stats.
     */
    @Transactional
    public BenchmarkQuickRunResponse run(BenchmarkQuickRunRequest request) {
        validatePercents(request);

        DatasetResponse dataset = datasetService.create(
                new CreateDatasetRequest(
                        buildDatasetName(request),
                        request.distributionType(),
                        request.datasetSize(),
                        null,
                        null
                )
        );

        ExperimentResponse experiment = experimentService.create(
                new CreateExperimentRequest(
                        dataset.id(),
                        buildExperimentTitle(request),
                        buildOperationsProfileJson(request),
                        null
                )
        );

        long totalOps = (long) request.datasetSize() * request.repeatCount() * 3L;
        long[] doneOps = new long[]{0L};

        log.info("[benchmark] quick-run started: experimentId={}, totalOps={}, datasetSize={}, repeatCount={}",
                experiment.id(), totalOps, request.datasetSize(), request.repeatCount());

        List<ExperimentStatResponse> stats = new ArrayList<>();
        for (TreeType treeType : new TreeType[]{TreeType.AVL, TreeType.RED_BLACK, TreeType.SPLAY}) {
            log.debug("[benchmark] running tree type={}", treeType);
            stats.add(runBenchmarkForTree(experiment.id(), treeType, request, doneOps, totalOps));
        }

        log.info("[benchmark] quick-run finished: experimentId={}, sending final progress 100%", experiment.id());
        benchmarkProgressService.sendProgress(experiment.id(), totalOps, totalOps);

        return new BenchmarkQuickRunResponse(experiment, stats);
    }

    private void validatePercents(BenchmarkQuickRunRequest request) {
        int total = request.totalPercent();
        if (total != 0 && total != 100) {
            throw new IllegalArgumentException("Sum of insert/search/delete percents must be 0 or 100, got: " + total);
        }
    }

    private String buildDatasetName(BenchmarkQuickRunRequest request) {
        return "size=" + request.datasetSize() + ", distribution=" + request.distributionType();
    }

    private String buildExperimentTitle(BenchmarkQuickRunRequest request) {
        return "Quick benchmark: AVL vs Red-Black vs Extensible, N=" + request.datasetSize()
                + ", distribution=" + request.distributionType();
    }

    private String buildOperationsProfileJson(BenchmarkQuickRunRequest request) {
        // Простий JSON без залежності від ObjectMapper, щоб зберегти параметри експерименту.
        return """
                {
                  "datasetSize": %d,
                  "distributionType": "%s",
                  "insertPercent": %d,
                  "searchPercent": %d,
                  "deletePercent": %d,
                  "repeatCount": %d,
                  "source": "quick-run-api"
                }
                """.formatted(
                request.datasetSize(),
                escapeJson(request.distributionType()),
                nullToZero(request.insertPercent()),
                nullToZero(request.searchPercent()),
                nullToZero(request.deletePercent()),
                request.repeatCount()
        );
    }

    private ExperimentStatResponse runBenchmarkForTree(
            UUID experimentId,
            TreeType treeType,
            BenchmarkQuickRunRequest request,
            long[] doneOps,
            long totalOps
    ) {
        IntTree tree = switch (treeType) {
            case AVL -> new AvlTree();
            case RED_BLACK -> new RedBlackTree();
            case SPLAY -> new SplayTree();
        };

        int datasetSize = request.datasetSize();
        int repeats = request.repeatCount();

        int[] keys = generateKeys(datasetSize, request.distributionType());
        OperationType[] operations = generateOperations(datasetSize, request);

        long start = System.nanoTime();
        long operationCount = 0L;
        long insertTimeNs = 0L;
        long searchTimeNs = 0L;
        long deleteTimeNs = 0L;

        long progressStep = Math.max(1_000L, totalOps / 100L);
        log.debug("[benchmark] runBenchmarkForTree treeType={}, progressStep={}", treeType, progressStep);

        for (int r = 0; r < repeats; r++) {
            for (int i = 0; i < datasetSize; i++) {
                int key = keys[i];
                OperationType op = operations[i];
                long operationStartedAt = System.nanoTime();
                switch (op) {
                    case INSERT -> {
                        tree.insert(key);
                        insertTimeNs += System.nanoTime() - operationStartedAt;
                    }
                    case SEARCH -> {
                        tree.search(key);
                        searchTimeNs += System.nanoTime() - operationStartedAt;
                    }
                    case DELETE -> {
                        tree.delete(key);
                        deleteTimeNs += System.nanoTime() - operationStartedAt;
                    }
                }
                operationCount++;
                doneOps[0]++;

                if (doneOps[0] % progressStep == 0 || doneOps[0] == totalOps) {
                    benchmarkProgressService.sendProgress(experimentId, doneOps[0], totalOps);
                }
            }
        }

        long executionTimeNs = System.nanoTime() - start;

        int treeHeight = tree.height();
        int rotationCount = tree.rotationCount();
        int maxDepth = tree.maxDepth();
        int nodeCount = tree.nodeCount();
        double avgDepth = tree.avgDepth();

        CreateExperimentStatRequest statRequest = new CreateExperimentStatRequest(
                treeType.value(),
                "quick-run-v1",
                executionTimeNs,
                insertTimeNs,
                searchTimeNs,
                deleteTimeNs,
                (int) operationCount,
                rotationCount,
                treeHeight,
                null,
                nodeCount > 0 ? BigDecimal.valueOf(avgDepth) : null,
                nodeCount > 0 ? maxDepth : null,
                null
        );

        return experimentStatService.create(experimentId, statRequest);
    }

    private int[] generateKeys(int size, String distributionTypeRaw) {
        int[] keys = new int[size];
        String dist = distributionTypeRaw == null ? "RANDOM" : distributionTypeRaw.toUpperCase();
        Random random = new Random(42L);

        switch (dist) {
            case "SORTED" -> {
                for (int i = 0; i < size; i++) {
                    keys[i] = i;
                }
            }
            case "REVERSE_SORTED" -> {
                for (int i = 0; i < size; i++) {
                    keys[i] = size - i;
                }
            }
            case "ALMOST_SORTED" -> {
                for (int i = 0; i < size; i++) {
                    keys[i] = i;
                }
                int swaps = Math.max(1, size / 10);
                for (int i = 0; i < swaps; i++) {
                    int a = random.nextInt(size);
                    int b = random.nextInt(size);
                    int tmp = keys[a];
                    keys[a] = keys[b];
                    keys[b] = tmp;
                }
            }
            case "ZIPF" -> {
                // Просте наближення: більше шансів для малих ключів.
                for (int i = 0; i < size; i++) {
                    int bucket = 1 + random.nextInt(100);
                    if (bucket <= 5) {
                        keys[i] = random.nextInt(size / 10 + 1);
                    } else if (bucket <= 10) {
                        keys[i] = random.nextInt(size / 5 + 1);
                    } else {
                        keys[i] = random.nextInt(size * 10 + 1);
                    }
                }
            }
            case "CUSTOM", "RANDOM" -> {
                for (int i = 0; i < size; i++) {
                    keys[i] = random.nextInt(size * 10 + 1);
                }
            }
            default -> {
                for (int i = 0; i < size; i++) {
                    keys[i] = random.nextInt(size * 10 + 1);
                }
            }
        }

        return keys;
    }

    private OperationType[] generateOperations(int size, BenchmarkQuickRunRequest request) {
        OperationType[] ops = new OperationType[size];
        int insert = nullToZero(request.insertPercent());
        int search = nullToZero(request.searchPercent());
        int delete = nullToZero(request.deletePercent());
        int total = insert + search + delete;

        if (total == 0) {
            // За замовчуванням: тільки вставки.
            for (int i = 0; i < size; i++) {
                ops[i] = OperationType.INSERT;
            }
            return ops;
        }

        int insertThreshold = insert;
        int searchThreshold = insert + search;
        Random random = new Random(123L);

        for (int i = 0; i < size; i++) {
            int v = random.nextInt(total);
            if (v < insertThreshold) {
                ops[i] = OperationType.INSERT;
            } else if (v < searchThreshold) {
                ops[i] = OperationType.SEARCH;
            } else {
                ops[i] = OperationType.DELETE;
            }
        }

        return ops;
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    private static int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    private enum OperationType {
        INSERT,
        SEARCH,
        DELETE
    }
}
