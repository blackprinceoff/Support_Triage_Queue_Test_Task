package digital.orthoeye.triage.controller;

import digital.orthoeye.triage.dto.CreateTicketRequest;
import digital.orthoeye.triage.model.Ticket;
import digital.orthoeye.triage.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping("/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket createTicket(@Valid @RequestBody CreateTicketRequest request) {
        return ticketService.createTicket(request);
    }

    @GetMapping("/tickets/{id}")
    public Ticket getTicket(@PathVariable UUID id) {
        return ticketService.getTicket(id);
    }

    @GetMapping("/queue")
    public List<Ticket> getQueue(@RequestParam(defaultValue = "50") int limit) {
        return ticketService.getQueue(limit);
    }

    // Маленький бонус для "enterprise" вигляду:
    // Якщо тікет не знайдено по ID, повертаємо красиву помилку 404 (Not Found), а не 500 (Internal Server Error)
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}