package server.messageFilters;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public class GoodMorningMessageFilter implements MessageFilter {
    @Override
    public Optional<String> validate(String message) {
        if (!message.toLowerCase().contains("good morning")){
            return Optional.empty();
        }

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        LocalTime startMorning = LocalTime.of(6, 0);
        LocalTime endMorning = LocalTime.of(12, 0);
        LocalTime now = LocalTime.now();

        boolean isMorning = now.isAfter(startMorning) && now.isBefore(endMorning);

        return isMorning && dayOfWeek == DayOfWeek.MONDAY
                ? Optional.of("Mornings cannot be Good before 12 PM on Mondays!")
                : Optional.empty();
    }
}
