package com.adaptivetreeanalysis.controller.benchmark;

import com.adaptivetreeanalysis.dto.benchmark.CreateDatasetRequest;
import com.adaptivetreeanalysis.dto.benchmark.DatasetResponse;
import com.adaptivetreeanalysis.service.benchmark.DatasetService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/datasets")
public class DatasetController {

    private final DatasetService datasetService;

    public DatasetController(DatasetService datasetService) {
        this.datasetService = datasetService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DatasetResponse create(@Valid @RequestBody CreateDatasetRequest request) {
        return datasetService.create(request);
    }

    @GetMapping
    public List<DatasetResponse> list() {
        return datasetService.list();
    }
}
