package com.adaptivetreeanalysis.service.tree;

import com.adaptivetreeanalysis.dto.tree.AvlOperationRequest;
import com.adaptivetreeanalysis.dto.tree.AvlOperationResponse;
import com.adaptivetreeanalysis.dto.tree.TreeMetrics;
import com.adaptivetreeanalysis.dto.tree.TreeOperationType;
import com.adaptivetreeanalysis.dto.tree.TreeStepEvent;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class AvlTreeOperationService {

    public AvlOperationResponse execute(AvlOperationRequest request) {
        TreeOperationType operation = request.operation();
        int key = request.key();

        List<TreeStepEvent> steps = switch (operation) {
            case INSERT -> List.of(
                    new TreeStepEvent("visit_node", key, Map.of("phase", "search_insert_position")),
                    new TreeStepEvent("insert_node", key, Map.of("parentKey", key)),
                    new TreeStepEvent("rebalance_check", key, Map.of("balanceFactor", 0))
            );
            case DELETE -> List.of(
                    new TreeStepEvent("visit_node", key, Map.of("phase", "search_delete_target")),
                    new TreeStepEvent("delete_node", key, Map.of("removed", true)),
                    new TreeStepEvent("rebalance_check", key, Map.of("balanceFactor", 0))
            );
            case SEARCH -> List.of(
                    new TreeStepEvent("visit_node", key, Map.of("phase", "search")),
                    new TreeStepEvent("search_result", key, Map.of("found", false))
            );
        };

        TreeMetrics metrics = new TreeMetrics(
                computeExecutionTimeNs(operation),
                computeRotationCount(operation),
                computeTreeHeight(operation),
                1
        );

        return new AvlOperationResponse("avl", operation, key, steps, metrics);
    }

    private long computeExecutionTimeNs(TreeOperationType operation) {
        return switch (operation) {
            case INSERT -> 120_000L;
            case DELETE -> 130_000L;
            case SEARCH -> 90_000L;
        };
    }

    private int computeRotationCount(TreeOperationType operation) {
        return switch (operation) {
            case INSERT, DELETE -> 0;
            case SEARCH -> 0;
        };
    }

    private int computeTreeHeight(TreeOperationType operation) {
        return switch (operation) {
            case INSERT, DELETE, SEARCH -> 1;
        };
    }
}
