package com.mindtogether.goals.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * Feign client para comunicación con Habits Service
 */
@FeignClient(name = "habits-service")
public interface HabitsClient {

    @GetMapping("/api/habits/{id}")
    HabitDTO getHabitById(@PathVariable("id") Long id, @RequestHeader("X-User-Id") String userId);

    @GetMapping("/api/habits/{id}/completions/count")
    Integer getHabitCompletionCount(@PathVariable("id") Long id, @RequestHeader("X-User-Id") String userId);
}

