package com.mindtogether.habits.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalSummaryDTO {
    private Long id;
    private String name;
    private String description;
    private Integer targetValue;
    private LocalDate targetDate;
    private Integer currentProgress;
    private String status;
    private String personalReward;
}

