package com.mindtogether.goals.web;

import com.mindtogether.goals.model.Achievement;
import com.mindtogether.goals.model.AchievementType;
import com.mindtogether.goals.service.AchievementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST Controller para la gestión de logros
 */
@RestController
@RequestMapping("/api/achievements")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AchievementResource {

    private final AchievementService achievementService;

    /**
     * GET /api/achievements - Obtener todos los logros del usuario
     */
    @GetMapping
    public ResponseEntity<List<Achievement>> getAllAchievements(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/achievements - user: {}", userId);
        
        List<Achievement> achievements = achievementService.getAllAchievements(userId);
        return ResponseEntity.ok(achievements);
    }

    /**
     * GET /api/achievements/recent - Obtener logros recientes
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Achievement>> getRecentAchievements(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "7") int days) {
        
        log.info("GET /api/achievements/recent - user: {}, days: {}", userId, days);
        
        List<Achievement> achievements = achievementService.getRecentAchievements(userId, days);
        return ResponseEntity.ok(achievements);
    }

    /**
     * GET /api/achievements/featured - Obtener logros destacados
     */
    @GetMapping("/featured")
    public ResponseEntity<List<Achievement>> getFeaturedAchievements(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/achievements/featured - user: {}", userId);
        
        List<Achievement> achievements = achievementService.getFeaturedAchievements(userId);
        return ResponseEntity.ok(achievements);
    }

    /**
     * GET /api/achievements/stats - Obtener estadísticas de logros
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getAchievementStats(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/achievements/stats - user: {}", userId);
        
        Long total = achievementService.getTotalAchievements(userId);
        Long points = achievementService.getTotalPoints(userId);
        
        // Calcular progreso (total de tipos de logros posibles)
        int totalPossible = AchievementType.values().length;
        double completionPercentage = (total * 100.0) / totalPossible;
        
        Map<String, Object> stats = Map.of(
                "totalUnlocked", total,
                "totalPoints", points,
                "totalPossible", totalPossible,
                "completionPercentage", Math.round(completionPercentage * 100.0) / 100.0
        );
        
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/achievements/available - Listar todos los tipos de logros disponibles
     */
    @GetMapping("/available")
    public ResponseEntity<List<Map<String, Object>>> getAvailableAchievements(
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/achievements/available - user: {}", userId);
        
        List<Map<String, Object>> availableAchievements = Arrays.stream(AchievementType.values())
                .map(type -> {
                    boolean unlocked = achievementService.hasAchievement(userId, type);
                    Map<String, Object> achievement = new java.util.HashMap<>();
                    achievement.put("type", type.name());
                    achievement.put("name", type.getDisplayName());
                    achievement.put("description", type.getDescription());
                    achievement.put("points", type.getPoints());
                    achievement.put("unlocked", unlocked);
                    return achievement;
                })
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(availableAchievements);
    }

    /**
     * POST /api/achievements/{id}/feature - Marcar/desmarcar logro como destacado
     */
    @PostMapping("/{id}/feature")
    public ResponseEntity<Achievement> toggleFeatured(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("POST /api/achievements/{}/feature - user: {}", id, userId);
        
        return achievementService.toggleFeatured(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/achievements/{id}/share - Compartir logro con círculos
     */
    @PostMapping("/{id}/share")
    public ResponseEntity<Achievement> shareWithCircles(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("POST /api/achievements/{}/share - user: {}", id, userId);
        
        return achievementService.shareWithCircles(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/achievements/mark-notifications-sent - Marcar notificaciones como enviadas
     */
    @PostMapping("/mark-notifications-sent")
    public ResponseEntity<Void> markNotificationsSent(@RequestHeader("X-User-Id") String userId) {
        log.info("POST /api/achievements/mark-notifications-sent - user: {}", userId);
        
        achievementService.markNotificationsSent(userId);
        return ResponseEntity.noContent().build();
    }
}

