package digital.orthoeye.triage.dto;

import digital.orthoeye.triage.model.Category;
import digital.orthoeye.triage.model.CustomerTier;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.OffsetDateTime;

public record CreateTicketRequest(
        @NotNull(message = "createdAt is required")
        OffsetDateTime createdAt,

        @Min(value = 1, message = "Severity must be between 1 and 5")
        @Max(value = 5, message = "Severity must be between 1 and 5")
        Integer severity,

        @NotNull(message = "Customer tier is required")
        CustomerTier customerTier,

        @NotNull(message = "Category is required")
        Category category,

        @NotBlank(message = "Summary cannot be blank")
        @Size(max = 200, message = "Summary must not exceed 200 characters")
        String summary
) {}