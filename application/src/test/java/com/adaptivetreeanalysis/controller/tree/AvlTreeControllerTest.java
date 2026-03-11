package com.adaptivetreeanalysis.controller.tree;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AvlTreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnDeterministicContractForInsertOperation() throws Exception {
        String payload = """
                {
                  "operation": "INSERT",
                  "key": 42
                }
                """;

        mockMvc.perform(post("/api/v1/trees/avl/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.treeType").value("avl"))
                .andExpect(jsonPath("$.operation").value("INSERT"))
                .andExpect(jsonPath("$.inputKey").value(42))
                .andExpect(jsonPath("$.steps[0].type").value("visit_node"))
                .andExpect(jsonPath("$.steps[1].type").value("insert_node"))
                .andExpect(jsonPath("$.metrics.operationCount").value(1));
    }

    @Test
    void shouldReturnBadRequestWhenOperationIsMissing() throws Exception {
        String payload = """
                {
                  "key": 42
                }
                """;

        mockMvc.perform(post("/api/v1/trees/avl/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest());
    }
}
