package es.udc.asi.habits.dto;

import es.udc.asi.habits.model.Habit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitCreateDTO {

    private String name;
    private String description;
    private Habit.Frequency frequency; // DAILY o WEEKLY
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime habitTime; // Hora opcional
    private Habit.Location location; // INTERIOR o EXTERIOR
    private Long categoryId; // Opcional
}

