package com.adaptivetreeanalysis.controller.benchmark;

import com.adaptivetreeanalysis.dto.benchmark.CreateExperimentRequest;
import com.adaptivetreeanalysis.dto.benchmark.ExperimentResponse;
import com.adaptivetreeanalysis.service.benchmark.ExperimentService;
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
@RequestMapping("/api/v1/experiments")
public class ExperimentController {

    private final ExperimentService experimentService;

    public ExperimentController(ExperimentService experimentService) {
        this.experimentService = experimentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExperimentResponse create(@Valid @RequestBody CreateExperimentRequest request) {
        return experimentService.create(request);
    }

    @GetMapping
    public List<ExperimentResponse> list() {
        return experimentService.list();
    }

    @GetMapping("/{experimentId}")
    public ExperimentResponse getById(@PathVariable UUID experimentId) {
        return experimentService.getById(experimentId);
    }
}
