package com.adaptivetreeanalysis.controller.tree;

import com.adaptivetreeanalysis.dto.tree.AvlOperationRequest;
import com.adaptivetreeanalysis.dto.tree.AvlOperationResponse;
import com.adaptivetreeanalysis.service.tree.AvlTreeOperationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trees/avl")
public class AvlTreeController {

    private final AvlTreeOperationService operationService;

    public AvlTreeController(AvlTreeOperationService operationService) {
        this.operationService = operationService;
    }

    @PostMapping("/operations")
    public AvlOperationResponse executeOperation(@Valid @RequestBody AvlOperationRequest request) {
        return operationService.execute(request);
    }
}
