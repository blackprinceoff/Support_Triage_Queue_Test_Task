package digital.orthoeye.triage.service;

import digital.orthoeye.triage.dto.CreateTicketRequest;
import digital.orthoeye.triage.model.CustomerTier;
import digital.orthoeye.triage.model.Ticket;
import digital.orthoeye.triage.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final SlaCalculatorService slaCalculatorService;
    private final SlaPolicy slaPolicy;

    public Ticket createTicket(CreateTicketRequest request) {
        // Отримуємо кількість годин на вирішення
        int slaHours = slaPolicy.getSlaHours(request.customerTier(), request.severity());

        // Вираховуємо точний дедлайн
        OffsetDateTime dueAt = slaCalculatorService.calculateDueAt(request.createdAt(), slaHours);

        // Збираємо тікет (id згенерується в репозиторії або можемо тут)
        Ticket ticket = Ticket.builder()
                .id(UUID.randomUUID())
                .createdAt(request.createdAt())
                .severity(request.severity())
                .customerTier(request.customerTier())
                .category(request.category())
                .summary(request.summary())
                .dueAt(dueAt)
                .build();

        return ticketRepository.save(ticket);
    }

    public Ticket getTicket(UUID id) {
        return ticketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found with id: " + id));
    }

    public List<Ticket> getQueue(int limit) {
        return ticketRepository.findAll().stream()
                .sorted(getTicketComparator())
                .limit(limit)
                .toList(); // Використовуємо .toList() з Java 16+
    }

    /**
     * Правила сортування:
     * 1. earliest dueAt first
     * 2. higher severity first (1 перед 2)
     * 3. higher tier first (ENTERPRISE > PRO > FREE)
     * 4. older createdAt first
     */
    private Comparator<Ticket> getTicketComparator() {
        return Comparator
                .comparing(Ticket::getDueAt)
                .thenComparing(Ticket::getSeverity)
                .thenComparing(t -> getTierPriority(t.getCustomerTier()))
                .thenComparing(Ticket::getCreatedAt);
    }

    // Допоміжний метод для зручного сортування CustomerTier
    // Чим менше число, тим вище в черзі
    private int getTierPriority(CustomerTier tier) {
        return switch (tier) {
            case ENTERPRISE -> 1;
            case PRO -> 2;
            case FREE -> 3;
        };
    }
}