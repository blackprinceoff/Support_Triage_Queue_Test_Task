package digital.orthoeye.triage.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import digital.orthoeye.triage.dto.CreateTicketRequest;
import digital.orthoeye.triage.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataLoaderConfig {

    private final TicketService ticketService;
    private final ObjectMapper objectMapper;

    @Bean
    public CommandLineRunner loadSampleData() {
        return args -> {
            // Читаємо файл з папки resources
            try (InputStream inputStream = getClass().getResourceAsStream("/tickets.sample.json")) {
                if (inputStream != null) {
                    // Парсимо JSON у список запитів (DTO)
                    List<CreateTicketRequest> requests = objectMapper.readValue(inputStream, new TypeReference<>() {});

                    // Створюємо тікети через наш сервіс (щоб відпрацював розрахунок SLA)
                    for (CreateTicketRequest request : requests) {
                        ticketService.createTicket(request);
                    }
                    System.out.println("✅ Successfully loaded " + requests.size() + " sample tickets into the queue.");
                } else {
                    System.out.println("⚠️ Sample tickets file not found.");
                }
            } catch (Exception e) {
                System.err.println("❌ Failed to load sample tickets: " + e.getMessage());
            }
        };
    }
}