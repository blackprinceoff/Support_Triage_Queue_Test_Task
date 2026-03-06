package digital.orthoeye.triage.service;

import digital.orthoeye.triage.model.CustomerTier;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SlaPolicy {

    // Матриця SLA у годинах: Map<CustomerTier, Map<Severity, Hours>>
    private final Map<CustomerTier, Map<Integer, Integer>> slaHoursMatrix = Map.of(
            CustomerTier.ENTERPRISE, Map.of(
                    1, 4,
                    2, 8,
                    3, 24,
                    4, 72,
                    5, 120
            ),
            CustomerTier.PRO, Map.of(
                    1, 8,
                    2, 16,
                    3, 48,
                    4, 96,
                    5, 168
            ),
            CustomerTier.FREE, Map.of(
                    1, 24,
                    2, 48,
                    3, 72,
                    4, 120,
                    5, 240
            )
    );

    public int getSlaHours(CustomerTier tier, int severity) {
        return slaHoursMatrix.getOrDefault(tier, Map.of()).getOrDefault(severity, 240); // 240 як фолбек
    }
}