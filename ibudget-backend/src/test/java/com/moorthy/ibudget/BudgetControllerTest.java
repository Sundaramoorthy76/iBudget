package com.moorthy.ibudget;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAcceptBudgetRequestAndReturnTheExistingResponseShape() throws Exception {
        mockMvc.perform(post("/api/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Monthly salary",
                                  "amount": 2500.00,
                                  "type": "INCOME",
                                  "category": "Salary",
                                  "description": "June pay",
                                  "entryDate": "2026-06-01"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Monthly salary"))
                .andExpect(jsonPath("$.amount").value(2500.00))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.entryDate").value("2026-06-01"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    void shouldRejectInvalidBudgetRequest() throws Exception {
        mockMvc.perform(post("/api/budget")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "",
                                  "amount": 0,
                                  "type": "INCOME",
                                  "category": "",
                                  "entryDate": "2026-06-01"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }
}
