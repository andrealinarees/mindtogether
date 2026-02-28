package com.mindtogether.goals.web;

import com.mindtogether.goals.model.CustomReward;
import com.mindtogether.goals.service.CustomRewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller para la gestión de recompensas personalizadas.
 * Las recompensas se asocian a metas y se desbloquean al completarlas.
 */
@RestController
@RequestMapping("/api/custom-rewards")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CustomRewardResource {

    private final CustomRewardService rewardService;

    /**
     * GET /api/custom-rewards - Obtener todas las recompensas del usuario
     */
    @GetMapping
    public ResponseEntity<List<CustomReward>> getAllRewards(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) CustomReward.RewardStatus status,
            @RequestParam(required = false) CustomReward.RewardCategory category) {

        log.info("GET /api/custom-rewards - user: {}, status: {}, category: {}", userId, status, category);

        List<CustomReward> rewards;
        if (status != null) {
            rewards = rewardService.getRewardsByStatus(userId, status);
        } else if (category != null) {
            rewards = rewardService.getRewardsByCategory(userId, category);
        } else {
            rewards = rewardService.getAllRewards(userId);
        }
        return ResponseEntity.ok(rewards);
    }

    /**
     * GET /api/custom-rewards/unlocked - Obtener recompensas desbloqueadas
     */
    @GetMapping("/unlocked")
    public ResponseEntity<List<CustomReward>> getUnlockedRewards(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/custom-rewards/unlocked - user: {}", userId);
        return ResponseEntity.ok(rewardService.getUnlockedRewards(userId));
    }

    /**
     * GET /api/custom-rewards/stats - Estadísticas de recompensas
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRewardStats(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/custom-rewards/stats - user: {}", userId);
        return ResponseEntity.ok(rewardService.getRewardStats(userId));
    }

    /**
     * GET /api/custom-rewards/goal/{goalId} - Obtener recompensas de una meta básica
     */
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<CustomReward>> getRewardsByGoal(
            @PathVariable Long goalId,
            @RequestHeader("X-User-Id") String userId) {

        log.info("GET /api/custom-rewards/goal/{} - user: {}", goalId, userId);
        return ResponseEntity.ok(rewardService.getRewardsByGoal(userId, goalId));
    }

    /**
     * GET /api/custom-rewards/mental-health-goal/{goalId} - Obtener recompensas de una meta de salud mental
     */
    @GetMapping("/mental-health-goal/{goalId}")
    public ResponseEntity<List<CustomReward>> getRewardsByMentalHealthGoal(
            @PathVariable Long goalId,
            @RequestHeader("X-User-Id") String userId) {

        log.info("GET /api/custom-rewards/mental-health-goal/{} - user: {}", goalId, userId);
        return ResponseEntity.ok(rewardService.getRewardsByMentalHealthGoal(userId, goalId));
    }

    /**
     * GET /api/custom-rewards/{id} - Obtener una recompensa por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CustomReward> getRewardById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {

        log.info("GET /api/custom-rewards/{} - user: {}", id, userId);
        return rewardService.getRewardById(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/custom-rewards - Crear una nueva recompensa
     */
    @PostMapping
    public ResponseEntity<CustomReward> createReward(
            @RequestBody CustomReward reward,
            @RequestHeader("X-User-Id") String userId) {

        log.info("POST /api/custom-rewards - user: {}, name: {}", userId, reward.getName());

        try {
            CustomReward created = rewardService.createReward(reward, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.warn("Error creating reward: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/custom-rewards/{id} - Actualizar una recompensa
     */
    @PutMapping("/{id}")
    public ResponseEntity<CustomReward> updateReward(
            @PathVariable Long id,
            @RequestBody CustomReward reward,
            @RequestHeader("X-User-Id") String userId) {

        log.info("PUT /api/custom-rewards/{} - user: {}", id, userId);
        return rewardService.updateReward(id, reward, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE /api/custom-rewards/{id} - Eliminar una recompensa
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReward(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {

        log.info("DELETE /api/custom-rewards/{} - user: {}", id, userId);
        if (rewardService.deleteReward(id, userId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/custom-rewards/{id}/claim - Marcar recompensa como reclamada
     */
    @PostMapping("/{id}/claim")
    public ResponseEntity<CustomReward> claimReward(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {

        log.info("POST /api/custom-rewards/{}/claim - user: {}", id, userId);
        return rewardService.claimReward(id, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.badRequest().build());
    }
}
