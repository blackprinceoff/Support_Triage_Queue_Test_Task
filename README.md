# Support Triage Queue

A Spring Boot service that ingests Support tickets and exposes a prioritized queue with computed SLA due times based on business hours.

## Setup & Run Steps

**Prerequisites:** Java 21

**1. Clone the repository and navigate to the root directory.**

**2. Run tests:**
```bash
./mvnw test
```

**3. Start the application:**
```bash
./mvnw spring-boot:run
```

**4. API Documentation (Swagger UI):** Once the application is running, open your browser and navigate to: `http://localhost:8080/swagger-ui.html`

> **Note:** The application automatically loads 10 sample tickets from `tickets.sample.json` on startup.

---

## Key Decisions & Trade-offs

- **Persistence:** I used a `ConcurrentHashMap` for in-memory storage. It ensures thread-safe operations out-of-the-box without the overhead of setting up an H2 database or Spring Data JPA. Given the assignment constraints ("in-memory is fine"), this was the most efficient choice to focus on core business logic.

- **SLA Calculation Logic:** Instead of hardcoding conditions, I implemented a minute-by-minute carry-over algorithm (`SlaCalculatorService`). It calculates remaining minutes, subtracts the time until the end of the current business day, and shifts the rest to the next business morning, skipping weekends.

- **Queue Sorting:** I used a custom `Comparator` with `.thenComparing()` to ensure deterministic sorting based on the 4 provided rules. I explicitly mapped `CustomerTier` to integer priorities to avoid relying on `Enum.ordinal()` which can break if the enum order changes.

---

## Assumptions

- **Business Hours** strictly end at 17:00: If a ticket's SLA expires exactly at 17:00, it is considered due on that day. If a ticket is created at 17:00 or later, its SLA calculation starts at 09:00 the next business day.

- **Public Holidays:** Only standard weekends (Saturday and Sunday) are excluded from business hours. Public holidays in Poland (`Europe/Warsaw`) are not excluded in this implementation.

- **Time Zones:** The system accepts `OffsetDateTime` in any timezone but always normalizes it to `Europe/Warsaw` for SLA calculation, returning the `dueAt` in the same format.

---

## AI Usage Notes

- **What I used AI for:** I used Gemini as a pair-programming partner to brainstorm the edge cases of the `java.time` API (specifically transitioning across weekends in the `Europe/Warsaw` zone), generate boilerplate for Swagger/DataLoader, and draft initial unit test structures. I also plan to use Gemini Code Assist for a final code review and syntax validation.

- **AI output rejected/corrected:** The AI initially suggested adding hours directly using `plusHours()` inside a loop. I rejected this because it fails to accurately account for tickets created at uneven times (e.g., 16:30). I corrected it to use a minute-based subtraction approach to ensure precision.

- **How I verified correctness:** I wrote explicit JUnit tests verifying the three SLA calculation examples provided in the requirements. I verified timezone transitions manually by running the tests and executing API requests via Swagger.
