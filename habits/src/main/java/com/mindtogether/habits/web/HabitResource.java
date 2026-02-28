package com.mindtogether.habits.web;

import com.mindtogether.habits.dto.*;
import com.mindtogether.habits.service.CategoryService;
import com.mindtogether.habits.service.HabitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class HabitResource {

    private final HabitService habitService;
    private final CategoryService categoryService;

    // HU04 - Crear hábito
    @PostMapping
    public ResponseEntity<HabitResponseDTO> createHabit(
            @RequestBody HabitCreateDTO habitDTO,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            log.info("POST /api/habits - Received request");
            log.info("User ID from header: {}", userId);
            log.info("HabitDTO received: name={}, frequency={}, location={}",
                    habitDTO.getName(), habitDTO.getFrequency(), habitDTO.getLocation());

            if (userId == null || userId.isEmpty()) {
                log.error("X-User-Id header is missing or empty!");
                throw new IllegalArgumentException("X-User-Id header is required");
            }

            HabitResponseDTO created = habitService.createHabit(userId, habitDTO);
            log.info("Habit created successfully with id: {}", created.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            log.error("Error creating habit for user {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    // HU07 - Ver listado de hábitos del usuario
    // También permite a admins ver hábitos de otros usuarios usando ?userId=X
    @GetMapping
    public ResponseEntity<List<HabitResponseDTO>> getUserHabits(
            @RequestHeader(value = "X-User-Id", required = false) String currentUserId,
            @RequestParam(value = "userId", required = false) String targetUserId) {

        // Si viene targetUserId, es un admin viendo hábitos de otro usuario
        String userIdToFetch = targetUserId != null ? targetUserId : currentUserId;

        log.info("GET /api/habits - Fetching habits for user: {} (requested by: {})",
                 userIdToFetch, currentUserId);

        List<HabitResponseDTO> habits = habitService.getUserHabits(userIdToFetch);
        return ResponseEntity.ok(habits);
    }

    // HU08 - Ver detalle de un hábito
    @GetMapping("/{id}")
    public ResponseEntity<HabitResponseDTO> getHabitById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/habits/{} - user: {}", id, userId);
        HabitResponseDTO habit = habitService.getHabitById(id, userId);
        return ResponseEntity.ok(habit);
    }

    // HU08 - Ver detalle COMPLETO de un hábito (con metas y recomendación del tiempo)
    @GetMapping("/{id}/detail")
    public ResponseEntity<HabitDetailDTO> getHabitDetail(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader(value = "X-Roles", required = false) String roles,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude) {
        log.info("GET /api/habits/{}/detail - user: {}, roles: {}, lat: {}, lon: {}", id, userId, roles, latitude, longitude);

        boolean isAdmin = roles != null && roles.contains("ADMIN");

        HabitDetailDTO detail = habitService.getHabitDetail(id, userId, isAdmin, latitude, longitude);
        return ResponseEntity.ok(detail);
    }

    // HU09 y HU10 - Toggle completar/desmarcar hábito
    @PostMapping("/{id}/toggle")
    public ResponseEntity<HabitResponseDTO> toggleHabitCompletion(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("POST /api/habits/{}/toggle - user: {}, date: {}", id, userId, date);
        LocalDate completionDate = date != null ? date : LocalDate.now();
        HabitResponseDTO updated = habitService.toggleHabitCompletion(id, userId, completionDate);
        return ResponseEntity.ok(updated);
    }

    // HU06 - Eliminar hábito
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        log.info("DELETE /api/habits/{} - user: {}", id, userId);
        habitService.deleteHabit(id, userId);
        return ResponseEntity.noContent().build();
    }

    // HU05 - Actualizar hábito
    @PutMapping("/{id}")
    public ResponseEntity<HabitResponseDTO> updateHabit(
            @PathVariable Long id,
            @RequestBody HabitCreateDTO habitDTO,
            @RequestHeader(value = "X-User-Id", required = false) String userId) {
        try {
            log.info("PUT /api/habits/{} - user: {}", id, userId);
            log.info("Update data: name={}, frequency={}, location={}",
                    habitDTO.getName(), habitDTO.getFrequency(), habitDTO.getLocation());

            if (userId == null || userId.isEmpty()) {
                log.error("X-User-Id header is missing or empty!");
                throw new IllegalArgumentException("X-User-Id header is required");
            }

            HabitResponseDTO updated = habitService.updateHabit(id, userId, habitDTO);
            log.info("Habit updated successfully with id: {}", updated.getId());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating habit {} for user {}: {}", id, userId, e.getMessage(), e);
            throw e;
        }
    }

    // HU11 - Añadir comentario a un hábito
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponseDTO> addComment(
            @PathVariable Long id,
            @RequestBody CommentCreateDTO commentDTO,
            @RequestHeader("X-User-Id") String userId) {
        try {
            log.info("POST /api/habits/{}/comments - user: {}", id, userId);
            CommentResponseDTO comment = habitService.addComment(id, userId, commentDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(comment);
        } catch (Exception e) {
            log.error("Error adding comment to habit {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    // HU12 - Editar comentario
    @PutMapping("/{habitId}/comments/{commentId}")
    public ResponseEntity<CommentResponseDTO> updateComment(
            @PathVariable Long habitId,
            @PathVariable Long commentId,
            @RequestBody CommentCreateDTO commentDTO,
            @RequestHeader("X-User-Id") String userId) {
        try {
            log.info("PUT /api/habits/{}/comments/{} - user: {}", habitId, commentId, userId);
            CommentResponseDTO updated = habitService.updateComment(habitId, commentId, userId, commentDTO);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            log.error("Error updating comment {}: {}", commentId, e.getMessage(), e);
            throw e;
        }
    }

    // HU13 - Eliminar comentario
    @DeleteMapping("/{habitId}/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long habitId,
            @PathVariable Long commentId,
            @RequestHeader("X-User-Id") String userId) {
        try {
            log.info("DELETE /api/habits/{}/comments/{} - user: {}", habitId, commentId, userId);
            habitService.deleteComment(habitId, commentId, userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting comment {}: {}", commentId, e.getMessage(), e);
            throw e;
        }
    }

    // Obtener todas las categorías
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        log.info("GET /api/habits/categories");
        List<CategoryResponseDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // Estadísticas generales de hábitos (para administradores)
    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Object>> getHabitStats() {
        log.info("GET /api/habits/stats");
        java.util.Map<String, Object> stats = habitService.getGlobalStats();
        return ResponseEntity.ok(stats);
    }
}


