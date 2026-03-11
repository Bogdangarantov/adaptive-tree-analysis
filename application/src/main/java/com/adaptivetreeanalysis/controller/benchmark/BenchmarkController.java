package com.adaptivetreeanalysis.controller.benchmark;

import com.adaptivetreeanalysis.dto.benchmark.BenchmarkQuickRunRequest;
import com.adaptivetreeanalysis.dto.benchmark.BenchmarkQuickRunResponse;
import com.adaptivetreeanalysis.service.benchmark.BenchmarkQuickRunService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/benchmark")
public class BenchmarkController {

    private final BenchmarkQuickRunService benchmarkQuickRunService;

    public BenchmarkController(BenchmarkQuickRunService benchmarkQuickRunService) {
        this.benchmarkQuickRunService = benchmarkQuickRunService;
    }

    /**
     * Runs a quick synthetic benchmark for all supported tree types (AVL, Red-Black, Extensible/Splay analogue)
     * using a generated dataset and persists the results into benchmark tables.
     */
    @PostMapping("/quick-run")
    public BenchmarkQuickRunResponse quickRun(@Valid @RequestBody BenchmarkQuickRunRequest request) {
        return benchmarkQuickRunService.run(request);
    }
}

