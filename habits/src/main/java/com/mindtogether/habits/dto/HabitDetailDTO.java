package com.mindtogether.habits.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitDetailDTO {
    private HabitResponseDTO habit;
    private List<CommentResponseDTO> comments;
    private List<GoalSummaryDTO> goals;
    private WeatherRecommendationDTO weatherRecommendation;
}

