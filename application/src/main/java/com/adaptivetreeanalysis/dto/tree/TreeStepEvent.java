package com.adaptivetreeanalysis.dto.tree;

import java.util.Map;

public record TreeStepEvent(
        String type,
        Integer nodeKey,
        Map<String, Object> metadata
) {
}
