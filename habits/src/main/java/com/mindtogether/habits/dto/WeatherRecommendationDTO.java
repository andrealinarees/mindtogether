package com.mindtogether.habits.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherRecommendationDTO {
    private String message;
    private String weatherCondition;
    private Double temperature;
    private String recommendation;
    private String icon;
}

