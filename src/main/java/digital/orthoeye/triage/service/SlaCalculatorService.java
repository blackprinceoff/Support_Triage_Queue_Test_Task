package digital.orthoeye.triage.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class SlaCalculatorService {

    private static final ZoneId WARSAW_ZONE = ZoneId.of("Europe/Warsaw");
    private static final int BUSINESS_HOUR_START = 9;
    private static final int BUSINESS_HOUR_END = 17;

    public OffsetDateTime calculateDueAt(OffsetDateTime createdAt, int slaHours) {
        // 1. Переводимо вхідний час у часовий пояс Варшави
        ZonedDateTime time = createdAt.atZoneSameInstant(WARSAW_ZONE);

        // 2. Якщо тікет створено в неробочий час, переносимо старт на найближчий робочий ранок
        time = adjustToBusinessHours(time);

        // 3. Додаємо час SLA
        int remainingMinutes = slaHours * 60;

        while (remainingMinutes > 0) {
            ZonedDateTime endOfDay = time.withHour(BUSINESS_HOUR_END).withMinute(0).withSecond(0).withNano(0);
            long minutesUntilEnd = ChronoUnit.MINUTES.between(time, endOfDay);

            if (remainingMinutes <= minutesUntilEnd) {
                // Якщо залишку часу вистачає до кінця поточного робочого дня
                time = time.plusMinutes(remainingMinutes);
                remainingMinutes = 0;
            } else {
                // Якщо часу більше, ніж залишилось до кінця дня, переносимо залишок на наступний день
                remainingMinutes -= (int) minutesUntilEnd;
                time = nextBusinessDayStart(time);
            }
        }

        // Повертаємо результат у форматі OffsetDateTime
        return time.toOffsetDateTime();
    }

    private ZonedDateTime adjustToBusinessHours(ZonedDateTime time) {
        if (isWeekend(time)) {
            return nextBusinessDayStart(time);
        }
        if (time.getHour() >= BUSINESS_HOUR_END) {
            return nextBusinessDayStart(time);
        }
        if (time.getHour() < BUSINESS_HOUR_START) {
            return time.withHour(BUSINESS_HOUR_START).withMinute(0).withSecond(0).withNano(0);
        }
        return time;
    }

    private ZonedDateTime nextBusinessDayStart(ZonedDateTime time) {
        ZonedDateTime nextDay = time.plusDays(1).withHour(BUSINESS_HOUR_START).withMinute(0).withSecond(0).withNano(0);
        while (isWeekend(nextDay)) {
            nextDay = nextDay.plusDays(1);
        }
        return nextDay;
    }

    private boolean isWeekend(ZonedDateTime time) {
        DayOfWeek day = time.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }
}