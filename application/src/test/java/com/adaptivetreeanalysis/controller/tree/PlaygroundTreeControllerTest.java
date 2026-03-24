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
class PlaygroundTreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldApplyInsertToSnapshotWithoutDatabasePersistence() throws Exception {
        String payload = """
                {
                  "treeType": "avl",
                  "operation": "INSERT",
                  "key": 30,
                  "root": {
                    "key": 20,
                    "height": 2,
                    "left": {
                      "key": 10,
                      "height": 1,
                      "left": null,
                      "right": null
                    },
                    "right": null
                  }
                }
                """;

        mockMvc.perform(post("/api/v1/trees/playground/operations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.treeType").value("avl"))
                .andExpect(jsonPath("$.operation").value("INSERT"))
                .andExpect(jsonPath("$.inputKey").value(30))
                .andExpect(jsonPath("$.changed").value(true))
                .andExpect(jsonPath("$.snapshot.root.key").value(20))
                .andExpect(jsonPath("$.snapshot.root.right.key").value(30));
    }
}
