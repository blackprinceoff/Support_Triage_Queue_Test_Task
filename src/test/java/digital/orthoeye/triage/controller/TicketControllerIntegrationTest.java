package digital.orthoeye.triage.controller;

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
class TicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateTicketAndReturnWithDueAt() throws Exception {
        // Беремо перший приклад з файлу tickets.sample.json, який дав рекрутер
        String requestJson = """
                {
                    "createdAt": "2026-02-11T10:00:00+01:00",
                    "severity": 1,
                    "customerTier": "ENTERPRISE",
                    "category": "PAYMENTS",
                    "summary": "Checkout fails intermittently with 500 on /authorize"
                }
                """;

        // Імітуємо POST запит на /tickets
        mockMvc.perform(post("/tickets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated()) // Перевіряємо, що статус 201 Created
                .andExpect(jsonPath("$.id").exists()) // Перевіряємо, що згенерувався UUID
                .andExpect(jsonPath("$.dueAt").exists()) // Перевіряємо, що SLA порахувалося
                .andExpect(jsonPath("$.severity").value(1))
                .andExpect(jsonPath("$.customerTier").value("ENTERPRISE"));
    }
}