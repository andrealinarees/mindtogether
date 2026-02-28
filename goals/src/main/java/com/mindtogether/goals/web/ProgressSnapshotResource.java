package com.mindtogether.goals.web;

import com.mindtogether.goals.model.ProgressSnapshot;
import com.mindtogether.goals.service.ProgressSnapshotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller para la gestión de instantáneas de progreso
 */
@RestController
@RequestMapping("/api/progress-snapshots")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProgressSnapshotResource {

    private final ProgressSnapshotService snapshotService;

    /**
     * GET /api/progress-snapshots/goal/{goalId} - Obtener snapshots de una meta
     */
    @GetMapping("/goal/{goalId}")
    public ResponseEntity<List<ProgressSnapshot>> getSnapshotsByGoal(
            @PathVariable Long goalId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        log.info("GET /api/progress-snapshots/goal/{} - user: {}, range: {} to {}", 
                 goalId, userId, startDate, endDate);
        
        List<ProgressSnapshot> snapshots;
        if (startDate != null && endDate != null) {
            snapshots = snapshotService.getSnapshotsByDateRange(goalId, userId, startDate, endDate);
        } else {
            snapshots = snapshotService.getSnapshotsByGoal(goalId, userId);
        }
        
        return ResponseEntity.ok(snapshots);
    }

    /**
     * GET /api/progress-snapshots/goal/{goalId}/latest - Obtener último snapshot
     */
    @GetMapping("/goal/{goalId}/latest")
    public ResponseEntity<ProgressSnapshot> getLatestSnapshot(
            @PathVariable Long goalId,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/progress-snapshots/goal/{}/latest - user: {}", goalId, userId);
        
        return snapshotService.getLatestSnapshot(goalId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/progress-snapshots/milestones - Obtener snapshots con hitos
     */
    @GetMapping("/milestones")
    public ResponseEntity<List<ProgressSnapshot>> getMilestoneSnapshots(
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("GET /api/progress-snapshots/milestones - user: {}", userId);
        
        List<ProgressSnapshot> snapshots = snapshotService.getMilestoneSnapshots(userId);
        return ResponseEntity.ok(snapshots);
    }

    /**
     * POST /api/progress-snapshots/goal/{goalId} - Crear snapshot manual
     */
    @PostMapping("/goal/{goalId}")
    public ResponseEntity<ProgressSnapshot> createSnapshot(
            @PathVariable Long goalId,
            @RequestBody ProgressSnapshot snapshot,
            @RequestHeader("X-User-Id") String userId) {
        
        log.info("POST /api/progress-snapshots/goal/{} - user: {}", goalId, userId);
        
        try {
            ProgressSnapshot created = snapshotService.createSnapshot(goalId, userId, snapshot);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            log.error("Error creating snapshot: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/progress-snapshots/goal/{goalId}/analytics - Análisis de progreso
     */
    @GetMapping("/goal/{goalId}/analytics")
    public ResponseEntity<ProgressSnapshotService.ProgressAnalytics> getProgressAnalytics(
            @PathVariable Long goalId,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("GET /api/progress-snapshots/goal/{}/analytics - user: {}, days: {}", 
                 goalId, userId, days);
        
        try {
            ProgressSnapshotService.ProgressAnalytics analytics = 
                    snapshotService.getProgressAnalytics(goalId, userId, days);
            return ResponseEntity.ok(analytics);
        } catch (IllegalArgumentException e) {
            log.error("Error getting analytics: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/progress-snapshots/mood-trend - Tendencia de estado de ánimo
     */
    @GetMapping("/mood-trend")
    public ResponseEntity<Map<LocalDate, Double>> getMoodTrend(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(defaultValue = "30") int days) {
        
        log.info("GET /api/progress-snapshots/mood-trend - user: {}, days: {}", userId, days);
        
        Map<LocalDate, Double> moodTrend = snapshotService.getMoodTrend(userId, days);
        return ResponseEntity.ok(moodTrend);
    }
}

