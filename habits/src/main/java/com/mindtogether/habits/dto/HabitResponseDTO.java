package com.mindtogether.habits.dto;

import com.mindtogether.habits.model.Habit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String userId;
    private Habit.Frequency frequency;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime habitTime;
    private Habit.Location location;
    private Habit.HabitStatus status;
    private Long categoryId;
    private CategoryResponseDTO category; // Nueva: información completa de la categoría
    private Integer currentStreak;
    private Integer longestStreak;
    private Boolean completedToday;
    private Double progressPercentage; // Porcentaje de progreso calculado
    private List<CommentResponseDTO> comments;
}


