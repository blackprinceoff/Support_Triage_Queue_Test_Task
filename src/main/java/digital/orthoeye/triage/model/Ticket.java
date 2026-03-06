package digital.orthoeye.triage.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class Ticket {
    private UUID id;
    private OffsetDateTime createdAt;
    private Integer severity; // від 1 до 5
    private CustomerTier customerTier;
    private Category category;
    private String summary;

    // Поля, які обчислює сервер:
    private OffsetDateTime dueAt;
}