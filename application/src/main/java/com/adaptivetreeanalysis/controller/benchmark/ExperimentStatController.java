package com.adaptivetreeanalysis.controller.benchmark;

import com.adaptivetreeanalysis.dto.benchmark.CreateExperimentStatRequest;
import com.adaptivetreeanalysis.dto.benchmark.ExperimentStatResponse;
import com.adaptivetreeanalysis.service.benchmark.ExperimentStatService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/experiments/{experimentId}/stats")
public class ExperimentStatController {

    private final ExperimentStatService experimentStatService;

    public ExperimentStatController(ExperimentStatService experimentStatService) {
        this.experimentStatService = experimentStatService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperimentStatResponse create(
            @PathVariable UUID experimentId,
            @Valid @RequestBody CreateExperimentStatRequest request
    ) {
        return experimentStatService.create(experimentId, request);
    }

    @GetMapping
    public List<ExperimentStatResponse> list(@PathVariable UUID experimentId) {
        return experimentStatService.listByExperimentId(experimentId);
    }
}
