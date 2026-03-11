package com.adaptivetreeanalysis.service.benchmark;

import com.adaptivetreeanalysis.dto.benchmark.BenchmarkProgressDto;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BenchmarkProgressService {

    private static final Logger log = LoggerFactory.getLogger(BenchmarkProgressService.class);

    private final SimpMessagingTemplate messagingTemplate;

    public BenchmarkProgressService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendProgress(UUID experimentId, long doneOperations, long totalOperations) {
        log.info("[benchmark-progress] experimentId={}, done={}, total={}, topic=/topic/benchmark-progress",
                experimentId, doneOperations, totalOperations);
        BenchmarkProgressDto dto = new BenchmarkProgressDto(experimentId, doneOperations, totalOperations);
        messagingTemplate.convertAndSend("/topic/benchmark-progress", dto);
    }
}

