package es.udc.asi.goals.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO para recibir información de hábitos desde Habits Service
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitDTO {
    private Long id;
    private String name;
    private String description;
    private String userId;
    private String frequency;
    private LocalDate startDate;
    private Integer estimatedDuration;
    private String status;
    private Long categoryId;
    private Integer currentStreak;
    private Integer longestStreak;
    private Integer completionCount;
}
