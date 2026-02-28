package com.mindtogether.goals.web;

import com.mindtogether.goals.model.Goal;
import com.mindtogether.goals.service.GoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GoalResource {

    private final GoalService goalService;

    /**
     * HU23 - Ver lista de objetivos
     * GET /api/goals
     */
    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals(@RequestHeader("X-User-Id") String userId,
                                                   @RequestParam(required = false) Goal.GoalStatus status) {
        log.info("GET /api/goals - user: {}, status: {}", userId, status);
        
        // Actualizar objetivos vencidos
        goalService.checkAndUpdateOverdueGoals(userId);
        
        List<Goal> goals = status != null 
            ? goalService.getGoalsByStatus(userId, status)
            : goalService.getAllGoals(userId);
            
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/goals/active - Ver objetivos activos ordenados por fecha
     */
    @GetMapping("/active")
    public ResponseEntity<List<Goal>> getActiveGoals(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/goals/active - user: {}", userId);
        goalService.checkAndUpdateOverdueGoals(userId);
        List<Goal> goals = goalService.getActiveGoals(userId);
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/goals/overdue - Ver objetivos vencidos
     */
    @GetMapping("/overdue")
    public ResponseEntity<List<Goal>> getOverdueGoals(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/goals/overdue - user: {}", userId);
        List<Goal> goals = goalService.getOverdueGoals(userId);
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/goals/habit/{habitId} - Ver objetivos de un hábito específico
     */
    @GetMapping("/habit/{habitId}")
    public ResponseEntity<List<Goal>> getGoalsByHabit(@PathVariable Long habitId,
                                                       @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/goals/habit/{} - user: {}", habitId, userId);
        List<Goal> goals = goalService.getGoalsByHabit(userId, habitId);
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/goals/search - Buscar objetivos
     */
    @GetMapping("/search")
    public ResponseEntity<List<Goal>> searchGoals(@RequestParam String query,
                                                   @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/goals/search - query: {}, user: {}", query, userId);
        List<Goal> goals = goalService.searchGoals(userId, query);
        return ResponseEntity.ok(goals);
    }

    /**
     * GET /api/goals/{id} - Ver detalle de objetivo
     */
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable Long id,
                                            @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/goals/{} - user: {}", id, userId);
        return goalService.getGoalById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * HU25 - Ver progreso de objetivos
     * GET /api/goals/{id}/progress
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<Map<String, Object>> getGoalProgress(@PathVariable Long id,
                                                                @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/goals/{}/progress - user: {}", id, userId);
        
        return goalService.getGoalById(id, userId)
                .map(goal -> {
                    Map<String, Object> progress = Map.of(
                        "goalId", goal.getId(),
                        "currentProgress", goal.getCurrentProgress(),
                        "targetValue", goal.getTargetValue(),
                        "progressPercentage", goal.getProgressPercentage(),
                        "status", goal.getStatus(),
                        "daysRemaining", goal.getDaysRemaining(),
                        "isOverdue", goal.isOverdue()
                    );
                    return ResponseEntity.ok(progress);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * HU20 - Crear objetivo
     * POST /api/goals
     */
    @PostMapping
    public ResponseEntity<Goal> createGoal(@RequestBody Goal goal,
                                            @RequestHeader("X-User-Id") String userId) {
        log.info("POST /api/goals - user: {}, habit: {}", userId, goal.getHabitId());
        
        try {
            Goal created = goalService.createGoal(goal, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error creating goal: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * HU21 - Editar objetivo
     * PUT /api/goals/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable Long id,
                                            @RequestBody Goal goal,
                                            @RequestHeader("X-User-Id") String userId) {
        log.info("PUT /api/goals/{} - user: {}", id, userId);
        
        return goalService.updateGoal(id, goal, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * HU22 - Eliminar objetivo
     * DELETE /api/goals/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id,
                                            @RequestHeader("X-User-Id") String userId) {
        log.info("DELETE /api/goals/{} - user: {}", id, userId);
        
        boolean deleted = goalService.deleteGoal(id, userId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    /**
     * PUT /api/goals/{id}/progress - Actualizar progreso manualmente
     */
    @PutMapping("/{id}/progress")
    public ResponseEntity<Goal> updateProgress(@PathVariable Long id,
                                                @RequestBody Map<String, Integer> request,
                                                @RequestHeader("X-User-Id") String userId) {
        log.info("PUT /api/goals/{}/progress - user: {}, progress: {}", id, userId, request.get("progress"));
        
        Integer progress = request.get("progress");
        if (progress == null) {
            return ResponseEntity.badRequest().build();
        }
        
        return goalService.updateProgress(id, progress, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/goals/{id}/sync - Sincronizar progreso desde hábito
     */
    @PostMapping("/{id}/sync")
    public ResponseEntity<Goal> syncProgressFromHabit(@PathVariable Long id,
                                                       @RequestHeader("X-User-Id") String userId) {
        log.info("POST /api/goals/{}/sync - user: {}", id, userId);
        
        return goalService.syncProgressFromHabit(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * HU24 - Marcar objetivo como completado
     * POST /api/goals/{id}/complete
     */
    @PostMapping("/{id}/complete")
    public ResponseEntity<Goal> markAsCompleted(@PathVariable Long id,
                                                 @RequestHeader("X-User-Id") String userId) {
        log.info("POST /api/goals/{}/complete - user: {}", id, userId);
        
        return goalService.markAsCompleted(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/goals/{id}/fail - Marcar objetivo como fallido
     */
    @PostMapping("/{id}/fail")
    public ResponseEntity<Goal> markAsFailed(@PathVariable Long id,
                                              @RequestHeader("X-User-Id") String userId) {
        log.info("POST /api/goals/{}/fail - user: {}", id, userId);
        
        return goalService.markAsFailed(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/goals/stats - Obtener estadísticas de objetivos
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getGoalStats(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/goals/stats - user: {}", userId);
        
        goalService.checkAndUpdateOverdueGoals(userId);
        
        Long activeCount = goalService.getGoalCountByStatus(userId, Goal.GoalStatus.ACTIVE);
        Long completedCount = goalService.getGoalCountByStatus(userId, Goal.GoalStatus.COMPLETED);
        Long failedCount = goalService.getGoalCountByStatus(userId, Goal.GoalStatus.FAILED);
        Long cancelledCount = goalService.getGoalCountByStatus(userId, Goal.GoalStatus.CANCELLED);
        
        Map<String, Object> stats = Map.of(
            "active", activeCount,
            "completed", completedCount,
            "failed", failedCount,
            "cancelled", cancelledCount,
            "total", activeCount + completedCount + failedCount + cancelledCount
        );
        
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/goals/by-habit/{habitId} - Endpoint interno para obtener metas de un hábito
     * (sin autenticación para permitir llamadas entre servicios)
     */
    @GetMapping("/by-habit/{habitId}")
    public ResponseEntity<List<Goal>> getGoalsByHabitInternal(@PathVariable Long habitId) {
        log.info("GET /api/goals/by-habit/{} - internal call", habitId);
        List<Goal> goals = goalService.getGoalsByHabitId(habitId);
        return ResponseEntity.ok(goals);
    }

    /**
     * DELETE /api/goals/by-habit/{habitId} - Endpoint interno para eliminar todas las metas de un hábito
     * (sin autenticación para permitir llamadas entre servicios)
     */
    @DeleteMapping("/by-habit/{habitId}")
    public ResponseEntity<Map<String, Object>> deleteGoalsByHabitInternal(@PathVariable Long habitId) {
        log.info("DELETE /api/goals/by-habit/{} - internal call", habitId);
        int deletedCount = goalService.deleteGoalsByHabitId(habitId);
        return ResponseEntity.ok(Map.of(
                "habitId", habitId,
                "deletedGoals", deletedCount,
                "message", "Goals deleted successfully"
        ));
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Goals service is running");
    }
}

