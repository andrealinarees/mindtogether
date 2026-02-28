package com.mindtogether.habits.service;

import com.mindtogether.habits.dto.WeatherRecommendationDTO;
import com.mindtogether.habits.model.Habit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherRecommendationService {

    private final RestTemplate restTemplate;
    
    @Value("${weather.api.url:https://api.open-meteo.com/v1/forecast}")
    private String weatherApiUrl;

    public WeatherRecommendationDTO getRecommendation(Habit habit, Double latitude, Double longitude) {
        try {
            // Usar coordenadas por defecto si no se proporcionan (A Coruña)
            double lat = latitude != null ? latitude : 43.3623;
            double lon = longitude != null ? longitude : -8.4115;
            
            // Llamar a Open-Meteo API
            String url = String.format("%s?latitude=%.4f&longitude=%.4f&current_weather=true&timezone=Europe/Madrid",
                weatherApiUrl, lat, lon);
            log.info("Fetching weather data from: {}", url);
            
            Map<String, Object> weatherData = restTemplate.getForObject(url, Map.class);
            log.info("Weather data received: {}", weatherData);
            
            if (weatherData == null) {
                log.warn("Weather data is null");
                return getDefaultRecommendation(habit);
            }

            Double temperature = getTemperature(weatherData);
            String condition = getCondition(weatherData);
            
            log.info("Parsed temperature: {}°C, condition: {}", temperature, condition);
            
            return generateRecommendation(habit, temperature, condition);
            
        } catch (Exception e) {
            log.error("Error fetching weather data: {} - {}", e.getClass().getName(), e.getMessage(), e);
            return getDefaultRecommendation(habit);
        }
    }

    private Double getTemperature(Map<String, Object> weatherData) {
        try {
            // Open-Meteo devuelve la estructura: { current_weather: { temperature: X } }
            Object currentWeather = weatherData.get("current_weather");
            if (currentWeather instanceof Map) {
                Map<String, Object> currentWeatherMap = (Map<String, Object>) currentWeather;
                Object temp = currentWeatherMap.get("temperature");
                if (temp instanceof Number) {
                    return ((Number) temp).doubleValue();
                }
            }
            
            // Fallback: intentar obtener directamente del root
            Object temp = weatherData.get("temperature");
            if (temp instanceof Number) {
                return ((Number) temp).doubleValue();
            }
        } catch (Exception e) {
            log.warn("Could not parse temperature: {}", e.getMessage());
        }
        return 20.0; // Valor por defecto
    }

    private String getCondition(Map<String, Object> weatherData) {
        try {
            // Open-Meteo devuelve: { current_weather: { weathercode: X } }
            Object currentWeather = weatherData.get("current_weather");
            if (currentWeather instanceof Map) {
                Map<String, Object> currentWeatherMap = (Map<String, Object>) currentWeather;
                Object weathercode = currentWeatherMap.get("weathercode");
                if (weathercode instanceof Number) {
                    return mapWeatherCodeToCondition(((Number) weathercode).intValue());
                }
            }
            
            // Fallback: intentar obtener condition directamente
            Object cond = weatherData.get("condition");
            if (cond != null) {
                return cond.toString();
            }
        } catch (Exception e) {
            log.warn("Could not parse condition: {}", e.getMessage());
        }
        return "desconocido";
    }

    private String mapWeatherCodeToCondition(int code) {
        // Códigos WMO Weather interpretation
        if (code == 0) return "despejado";
        if (code >= 1 && code <= 3) return "parcialmente nublado";
        if (code >= 45 && code <= 48) return "niebla";
        if (code >= 51 && code <= 67) return "lluvia";
        if (code >= 71 && code <= 77) return "nieve";
        if (code >= 80) return "tormenta";
        return "nublado";
    }

    private WeatherRecommendationDTO generateRecommendation(Habit habit, Double temperature, String condition) {
        boolean isGoodWeather = isGoodWeather(temperature, condition);
        boolean isIndoor = habit.getLocation() == Habit.Location.INTERIOR;
        
        String message;
        String recommendation;
        String icon;

        if (isIndoor && isGoodWeather) {
            message = "¡Hace buen tiempo afuera!";
            recommendation = "Considera hacer una actividad al aire libre hoy. El clima está perfecto para salir.";
            icon = "bi-sun";
        } else if (!isIndoor && !isGoodWeather) {
            message = "El clima no es favorable";
            recommendation = "Puede ser mejor hacer una actividad interior hoy. Considera alternativas bajo techo.";
            icon = "bi-cloud-rain";
        } else if (!isIndoor && isGoodWeather) {
            message = "¡Clima perfecto para tu hábito!";
            recommendation = "Las condiciones son ideales para realizar tu hábito al aire libre. ¡Disfrútalo!";
            icon = "bi-brightness-high";
        } else {
            message = "Condiciones adecuadas";
            recommendation = "El clima actual es apropiado para tu hábito interior.";
            icon = "bi-house-check";
        }

        return WeatherRecommendationDTO.builder()
                .message(message)
                .weatherCondition(condition)
                .temperature(temperature)
                .recommendation(recommendation)
                .icon(icon)
                .build();
    }

    private boolean isGoodWeather(Double temperature, String condition) {
        // Temperatura entre 15 y 28 grados es ideal
        boolean goodTemp = temperature >= 15 && temperature <= 28;
        
        // Condiciones favorables
        String condLower = condition.toLowerCase();
        boolean goodCondition = !condLower.contains("rain") && 
                                !condLower.contains("storm") && 
                                !condLower.contains("snow");
        
        return goodTemp && goodCondition;
    }

    private WeatherRecommendationDTO getDefaultRecommendation(Habit habit) {
        return WeatherRecommendationDTO.builder()
                .message("Información del clima no disponible")
                .weatherCondition("desconocido")
                .temperature(20.0)
                .recommendation("Realiza tu hábito según tu preferencia.")
                .icon("bi-question-circle")
                .build();
    }

    private Map<String, Double> getCityCoordinates(String cityName) {
        // Coordenadas de ciudades españolas (sincronizadas con frontend)
        Map<String, Map<String, Double>> cities = new java.util.HashMap<>();
        cities.put("A Coruña", Map.of("latitude", 43.3623, "longitude", -8.4115));
        cities.put("Albacete", Map.of("latitude", 38.9943, "longitude", -1.8585));
        cities.put("Alicante", Map.of("latitude", 38.3452, "longitude", -0.4810));
        cities.put("Almería", Map.of("latitude", 36.8381, "longitude", -2.4597));
        cities.put("Ávila", Map.of("latitude", 40.6561, "longitude", -4.6813));
        cities.put("Badajoz", Map.of("latitude", 38.8794, "longitude", -6.9707));
        cities.put("Barcelona", Map.of("latitude", 41.3851, "longitude", 2.1734));
        cities.put("Bilbao", Map.of("latitude", 43.2630, "longitude", -2.9350));
        cities.put("Burgos", Map.of("latitude", 42.3439, "longitude", -3.6970));
        cities.put("Cáceres", Map.of("latitude", 39.4753, "longitude", -6.3724));
        cities.put("Cádiz", Map.of("latitude", 36.5271, "longitude", -6.2886));
        cities.put("Castellón", Map.of("latitude", 39.9864, "longitude", -0.0513));
        cities.put("Ceuta", Map.of("latitude", 35.8894, "longitude", -5.3213));
        cities.put("Ciudad Real", Map.of("latitude", 38.9848, "longitude", -3.9276));
        cities.put("Córdoba", Map.of("latitude", 37.8882, "longitude", -4.7794));
        cities.put("Cuenca", Map.of("latitude", 40.0704, "longitude", -2.1374));
        cities.put("Gijón", Map.of("latitude", 43.5322, "longitude", -5.6611));
        cities.put("Girona", Map.of("latitude", 41.9794, "longitude", 2.8214));
        cities.put("Granada", Map.of("latitude", 37.1773, "longitude", -3.5986));
        cities.put("Guadalajara", Map.of("latitude", 40.6331, "longitude", -3.1674));
        cities.put("Huelva", Map.of("latitude", 37.2614, "longitude", -6.9447));
        cities.put("Huesca", Map.of("latitude", 42.1401, "longitude", -0.4080));
        cities.put("Jaén", Map.of("latitude", 37.7796, "longitude", -3.7849));
        cities.put("Las Palmas", Map.of("latitude", 28.1236, "longitude", -15.4366));
        cities.put("León", Map.of("latitude", 42.5987, "longitude", -5.5671));
        cities.put("Lleida", Map.of("latitude", 41.6175, "longitude", 0.6200));
        cities.put("Logroño", Map.of("latitude", 42.4627, "longitude", -2.4450));
        cities.put("Lugo", Map.of("latitude", 43.0097, "longitude", -7.5567));
        cities.put("Madrid", Map.of("latitude", 40.4168, "longitude", -3.7038));
        cities.put("Málaga", Map.of("latitude", 36.7213, "longitude", -4.4214));
        cities.put("Melilla", Map.of("latitude", 35.2923, "longitude", -2.9381));
        cities.put("Murcia", Map.of("latitude", 37.9922, "longitude", -1.1307));
        cities.put("Ourense", Map.of("latitude", 42.3361, "longitude", -7.8640));
        cities.put("Oviedo", Map.of("latitude", 43.3614, "longitude", -5.8493));
        cities.put("Palencia", Map.of("latitude", 42.0096, "longitude", -4.5288));
        cities.put("Palma", Map.of("latitude", 39.5696, "longitude", 2.6502));
        cities.put("Pamplona", Map.of("latitude", 42.8125, "longitude", -1.6458));
        cities.put("Pontevedra", Map.of("latitude", 42.4334, "longitude", -8.6444));
        cities.put("Salamanca", Map.of("latitude", 40.9701, "longitude", -5.6635));
        cities.put("San Sebastián", Map.of("latitude", 43.3183, "longitude", -1.9812));
        cities.put("Santander", Map.of("latitude", 43.4623, "longitude", -3.8100));
        cities.put("Santiago", Map.of("latitude", 42.8782, "longitude", -8.5448));
        cities.put("Segovia", Map.of("latitude", 40.9429, "longitude", -4.1088));
        cities.put("Sevilla", Map.of("latitude", 37.3891, "longitude", -5.9845));
        cities.put("Soria", Map.of("latitude", 41.7665, "longitude", -2.4790));
        cities.put("Tarragona", Map.of("latitude", 41.1189, "longitude", 1.2445));
        cities.put("Tenerife", Map.of("latitude", 28.4682, "longitude", -16.2546));
        cities.put("Teruel", Map.of("latitude", 40.3456, "longitude", -1.1065));
        cities.put("Toledo", Map.of("latitude", 39.8628, "longitude", -4.0273));
        cities.put("Valencia", Map.of("latitude", 39.4699, "longitude", -0.3763));
        cities.put("Valladolid", Map.of("latitude", 41.6523, "longitude", -4.7245));
        cities.put("Vigo", Map.of("latitude", 42.2328, "longitude", -8.7226));
        cities.put("Vitoria", Map.of("latitude", 42.8467, "longitude", -2.6716));
        cities.put("Zamora", Map.of("latitude", 41.5034, "longitude", -5.7467));
        cities.put("Zaragoza", Map.of("latitude", 41.6488, "longitude", -0.8891));
        
        return cities.getOrDefault(cityName, cities.get("Madrid"));
    }
}

