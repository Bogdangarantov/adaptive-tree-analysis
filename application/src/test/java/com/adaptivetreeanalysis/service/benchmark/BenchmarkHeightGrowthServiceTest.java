package com.adaptivetreeanalysis.service.benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.adaptivetreeanalysis.domain.benchmark.entity.DatasetEntity;
import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentEntity;
import com.adaptivetreeanalysis.domain.benchmark.entity.ExperimentStatEntity;
import com.adaptivetreeanalysis.domain.benchmark.enums.TreeType;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkHeightGrowthResponse;
import com.adaptivetreeanalysis.repository.benchmark.ExperimentStatRepository;
import java.util.List;
import org.junit.jupiter.api.Test;

class BenchmarkHeightGrowthServiceTest {

    private final ExperimentStatRepository experimentStatRepository = mock(ExperimentStatRepository.class);
    private final BenchmarkHeightGrowthService service = new BenchmarkHeightGrowthService(experimentStatRepository);

    @Test
    void shouldGroupHeightGrowthByTreeTypeAndDatasetSize() {
        when(experimentStatRepository.findAll()).thenReturn(List.of(
                stat(TreeType.AVL, 100, 7),
                stat(TreeType.AVL, 100, 9),
                stat(TreeType.AVL, 1000, 14),
                stat(TreeType.RED_BLACK, 100, 8),
                stat(TreeType.RED_BLACK, 1000, 15),
                stat(TreeType.SPLAY, 100, 11)
        ));

        BenchmarkHeightGrowthResponse response = service.getHeightGrowth();

        assertEquals(6, response.totalRuns());
        assertEquals(3, response.series().size());

        assertEquals("avl", response.series().get(0).treeType());
        assertEquals(2, response.series().get(0).points().size());
        assertEquals(100, response.series().get(0).points().get(0).datasetSize());
        assertEquals(8.0, response.series().get(0).points().get(0).averageTreeHeight());
        assertEquals(2, response.series().get(0).points().get(0).runCount());

        assertEquals("red-black", response.series().get(1).treeType());
        assertEquals("splay", response.series().get(2).treeType());
    }

    private ExperimentStatEntity stat(TreeType treeType, int datasetSize, int treeHeight) {
        DatasetEntity dataset = new DatasetEntity();
        dataset.setSize(datasetSize);

        ExperimentEntity experiment = new ExperimentEntity();
        experiment.setDataset(dataset);

        ExperimentStatEntity stat = new ExperimentStatEntity();
        stat.setExperiment(experiment);
        stat.setTreeType(treeType);
        stat.setTreeHeight(treeHeight);
        return stat;
    }
}
