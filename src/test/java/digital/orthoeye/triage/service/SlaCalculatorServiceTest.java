package digital.orthoeye.triage.service;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlaCalculatorServiceTest {

    private final SlaCalculatorService calculator = new SlaCalculatorService();

    @Test
    void testExample1_MidDayTicket() {
        // Умова: createdAt = Wed 10:00, SLA = 4h => dueAt = Wed 14:00
        // Використовуємо середу 11 лютого 2026 року
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-02-11T10:00:00+01:00");
        int slaHours = 4;

        OffsetDateTime expectedDueAt = OffsetDateTime.parse("2026-02-11T14:00:00+01:00");
        OffsetDateTime actualDueAt = calculator.calculateDueAt(createdAt, slaHours);

        assertEquals(expectedDueAt, actualDueAt, "SLA should exactly match 4 hours later same day");
    }

    @Test
    void testExample2_AfternoonTicketCarriedOver() {
        // Умова: createdAt = Wed 16:30, SLA = 2h => dueAt = Thu 10:30
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-02-11T16:30:00+01:00");
        int slaHours = 2;

        // Перевіряємо, що 30 хв відпрацювали в середу, а 1.5 години перенеслись на четвер (09:00 + 1:30 = 10:30)
        OffsetDateTime expectedDueAt = OffsetDateTime.parse("2026-02-12T10:30:00+01:00");
        OffsetDateTime actualDueAt = calculator.calculateDueAt(createdAt, slaHours);

        assertEquals(expectedDueAt, actualDueAt, "SLA should carry over to the next morning");
    }

    @Test
    void testExample3_WeekendTicket() {
        // Умова: createdAt = Sat 11:00, SLA = 8h => dueAt = Mon 17:00
        // Використовуємо суботу 14 лютого 2026 року
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-02-14T11:00:00+01:00");
        int slaHours = 8;

        // Перевіряємо, що старт перенісся на понеділок 09:00, і 8 годин дали рівно 17:00 понеділка
        OffsetDateTime expectedDueAt = OffsetDateTime.parse("2026-02-16T17:00:00+01:00");
        OffsetDateTime actualDueAt = calculator.calculateDueAt(createdAt, slaHours);

        assertEquals(expectedDueAt, actualDueAt, "SLA should skip weekend and resolve end of Monday");
    }
}