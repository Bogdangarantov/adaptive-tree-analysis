package com.adaptivetreeanalysis.controller.tree;

import com.adaptivetreeanalysis.dto.tree.PlaygroundOperationRequest;
import com.adaptivetreeanalysis.dto.tree.PlaygroundOperationResponse;
import com.adaptivetreeanalysis.service.tree.PlaygroundTreeOperationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trees/playground")
public class PlaygroundTreeController {

    private final PlaygroundTreeOperationService playgroundTreeOperationService;

    public PlaygroundTreeController(PlaygroundTreeOperationService playgroundTreeOperationService) {
        this.playgroundTreeOperationService = playgroundTreeOperationService;
    }

    @PostMapping("/operations")
    public PlaygroundOperationResponse execute(@Valid @RequestBody PlaygroundOperationRequest request) {
        return playgroundTreeOperationService.execute(request);
    }
}
