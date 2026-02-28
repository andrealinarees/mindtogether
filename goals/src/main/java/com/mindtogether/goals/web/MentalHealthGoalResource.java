package com.mindtogether.goals.web;

import com.mindtogether.goals.model.GoalCategory;
import com.mindtogether.goals.model.GoalMilestone;
import com.mindtogether.goals.model.MentalHealthGoal;
import com.mindtogether.goals.model.ProgressSnapshot;
import com.mindtogether.goals.service.MentalHealthGoalService;
import com.mindtogether.goals.service.ProgressSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller para la gestión de metas de salud mental
 */
@RestController
@RequestMapping("/api/mental-health-goals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class MentalHealthGoalResource {

    private final MentalHealthGoalService goalService;
    private final ProgressSnapshotService snapshotService;

    // ==================== CRUD Operations ====================

    /**
     * GET /api/mental-health-goals - Obtener todas las metas del usuario
     */
    @GetMapping
    public ResponseEntity<List<MentalHealthGoal>> getAllGoals(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) MentalHealthGoal.GoalStatus status,
            @RequestParam(required = false) GoalCategory category) {
        
        log.info("GET /api/mental-health-goals - user: {}, status: {}, category: {}", userId, status, category);
        
        // Actualizar objetivos vencidos primero
        goalService.checkAndUpdateOverdueGoals(userId);
        
        List<MentalHealthGoal> goals;
        if (status != null) {
            goals = goalService.getGoalsByStatus(userId, status);
        } else if (category != null) {
            goals = goalService.getGoalsByCategory(userId, category);
        } else {
            goals = goalService.getAllGoals(userId);
        }
        
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/mental-health-goals/active - Obtener metas activas
     */
    @GetMapping("/active")
    public ResponseEntity<List<MentalHealthGoal>> getActiveGoals(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/mental-health-goals/active - user: {}", userId);
        
        goalService.checkAndUpdateOverdueGoals(userId);
        List<MentalHealthGoal> goals = goalService.getActiveGoals(userId);
        
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/mental-health-goals/overdue - Obtener metas vencidas
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<MentalHealthGoal>> getOverdueGoals(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/mental-health-goals/overdue - user: {}", userId);
        
        List<MentalHealthGoal> goals = goalService.getOverdueGoals(userId);
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/mental-health-goals/search - Buscar metas
     */
    @GetMapping("/search")
    public ResponseEntity<List<MentalHealthGoal>> searchGoals(
            @RequestParam String query,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/mental-health-goals/search - query: {}, user: {}", query, userId);
        
        List<MentalHealthGoal> goals = goalService.searchGoals(userId, query);
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/mental-health-goals/{id} - Obtener meta por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<MentalHealthGoal> getGoalById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/mental-health-goals/{} - user: {}", id, userId);
        
        return goalService.getGoalById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/mental-health-goals - Crear nueva meta
     */
    @PostMapping
    public ResponseEntity<MentalHealthGoal> createGoal(
            @RequestBody MentalHealthGoal goal,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("POST /api/mental-health-goals - user: {}, category: {}", userId, goal.getCategory());
        
        try {
            MentalHealthGoal created = goalService.createGoal(goal, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error creating goal: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/mental-health-goals/{id} - Actualizar meta
     */
    @PutMapping("/{id}")
    public ResponseEntity<MentalHealthGoal> updateGoal(
            @PathVariable Long id,
            @RequestBody MentalHealthGoal goal,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("PUT /api/mental-health-goals/{} - user: {}", id, userId);
        
        return goalService.updateGoal(id, goal, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/mental-health-goals/{id} - Eliminar meta
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("DELETE /api/mental-health-goals/{} - user: {}", id, userId);
        
        boolean deleted = goalService.deleteGoal(id, userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // ==================== Progress Management ====================

    /**
     * GET /api/mental-health-goals/{id}/progress - Ver progreso de la meta
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> getGoalProgress(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/mental-health-goals/{}/progress - user: {}", id, userId);
        
        return goalService.getGoalById(id, userId)
                .map(goal -> {
                    Map<String, Object> progress = new java.util.HashMap<>();
                    progress.put("goalId", goal.getId());
                    progress.put("name", goal.getName());
                    progress.put("currentProgress", goal.getCurrentProgress());
                    progress.put("targetValue", goal.getTargetValue());
                    progress.put("unit", goal.getUnit());
                    progress.put("progressPercentage", goal.getProgressPercentage());
                    progress.put("status", goal.getStatus());
                    progress.put("daysRemaining", goal.getDaysRemaining());
                    progress.put("daysActive", goal.getDaysActive());
                    progress.put("isOverdue", goal.isOverdue());
                    progress.put("isCompletedEarly", goal.isCompletedEarly());
                    return ResponseEntity.ok(progress);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * PUT /api/mental-health-goals/{id}/progress - Actualizar progreso
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<MentalHealthGoal> updateProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") String userId) {
        
        Integer progress = (Integer) request.get("progress");
        Integer moodRating = (Integer) request.get("moodRating");
        
        log.info("PUT /api/mental-health-goals/{}/progress - user: {}, progress: {}, mood: {}", 
                 id, userId, progress, moodRating);
        
        if (progress == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return goalService.updateProgress(id, progress, userId, moodRating)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/mental-health-goals/{id}/increment - Incrementar progreso
     */
    @PostMapping("/{id}/increment")
    public ResponseEntity<MentalHealthGoal> incrementProgress(
            @PathVariable Long id,
            @RequestBody Map<String, Object> request,
            @RequestHeader("X-User-Id") String userId) {
        
        Integer amount = (Integer) request.getOrDefault("amount", 1);
        Integer moodRating = (Integer) request.get("moodRating");
        
        log.info("POST /api/mental-health-goals/{}/increment - user: {}, amount: {}", id, userId, amount);
        
        return goalService.incrementProgress(id, amount, userId, moodRating)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== Milestones ====================

    /**
     * GET /api/mental-health-goals/{id}/milestones - Obtener hitos de la meta
     */
    @GetMapping("/{id}/milestones")
    public ResponseEntity<List<GoalMilestone>> getMilestones(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/mental-health-goals/{}/milestones - user: {}", id, userId);
        
        List<GoalMilestone> milestones = goalService.getMilestones(id, userId);
        return ResponseEntity.ok(milestones);
    }

    /**
     * POST /api/mental-health-goals/{id}/milestones - Agregar hito a la meta
     */
    @PostMapping("/{id}/milestones")
    public ResponseEntity<GoalMilestone> addMilestone(
            @PathVariable Long id,
            @RequestBody GoalMilestone milestone,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("POST /api/mental-health-goals/{}/milestones - user: {}", id, userId);
        
        try {
            GoalMilestone created = goalService.addMilestone(id, milestone, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error adding milestone: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    // ==================== Progress History ====================

    /**
     * GET /api/mental-health-goals/{id}/history - Obtener historial de progreso
     */
    @GetMapping("/{id}/history")
    public ResponseEntity<List<ProgressSnapshot>> getProgressHistory(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/mental-health-goals/{}/history - user: {}", id, userId);
        
        List<ProgressSnapshot> history = goalService.getProgressHistory(id, userId);
        return ResponseEntity.ok(history);
    }

    /**
     * GET /api/mental-health-goals/{id}/analytics - Obtener análisis de progreso
     */
    @GetMapping("/{id}/analytics")
    public ResponseEntity<ProgressSnapshotService.ProgressAnalytics> getProgressAnalytics(
            @PathVariable Long id,
            @RequestParam(defaultValue = "30") int days,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/mental-health-goals/{}/analytics - user: {}, days: {}", id, userId, days);
        
        try {
            ProgressSnapshotService.ProgressAnalytics analytics = 
                    snapshotService.getProgressAnalytics(id, userId, days);
            return ResponseEntity.ok(analytics);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ==================== Statistics ====================

    /**
     * GET /api/mental-health-goals/statistics - Obtener estadísticas del usuario
     */
    @GetMapping("/statistics")
    public ResponseEntity<MentalHealthGoalService.GoalStatistics> getStatistics(
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/mental-health-goals/statistics - user: {}", userId);
        
        MentalHealthGoalService.GoalStatistics stats = goalService.getStatistics(userId);
        return ResponseEntity.ok(stats);
    }
}

