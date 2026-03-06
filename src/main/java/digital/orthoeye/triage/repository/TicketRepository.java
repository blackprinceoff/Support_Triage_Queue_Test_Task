package digital.orthoeye.triage.repository;

import digital.orthoeye.triage.model.Ticket;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class TicketRepository {

    // Використовуємо ConcurrentHashMap для потокобезпечності
    private final ConcurrentMap<UUID, Ticket> storage = new ConcurrentHashMap<>();

    public Ticket save(Ticket ticket) {
        // Якщо ID ще немає (створення), генеруємо його
        if (ticket.getId() == null) {
            ticket.setId(UUID.randomUUID());
        }
        storage.put(ticket.getId(), ticket);
        return ticket;
    }

    public Optional<Ticket> findById(UUID id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Ticket> findAll() {
        return new ArrayList<>(storage.values());
    }

    // Метод для очищення (знадобиться нам пізніше для тестів)
    public void deleteAll() {
        storage.clear();
    }
}