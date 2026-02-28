package com.mindtogether.habits.service;

import com.mindtogether.habits.dto.*;
import com.mindtogether.habits.model.Comment;
import com.mindtogether.habits.model.Habit;
import com.mindtogether.habits.model.HabitCompletion;
import com.mindtogether.habits.repository.CategoryRepository;
import com.mindtogether.habits.repository.CommentRepository;
import com.mindtogether.habits.repository.HabitCompletionRepository;
import com.mindtogether.habits.repository.HabitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitCompletionRepository habitCompletionRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final WeatherRecommendationService weatherRecommendationService;
    private final RestTemplate loadBalancedRestTemplate;
    private final RestTemplate restTemplate;

    @Value("${goals.service.url:http://localhost:9095}")
    private String goalsServiceUrl;

    @Transactional
    public HabitResponseDTO createHabit(String userId, HabitCreateDTO dto) {
        Habit habit = Habit.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .userId(userId)
                .frequency(dto.getFrequency())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .habitTime(dto.getHabitTime())
                .location(dto.getLocation())
                .categoryId(dto.getCategoryId())
                .status(Habit.HabitStatus.ACTIVE)
                .build();

        Habit saved = habitRepository.save(habit);
        return mapToResponseDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<HabitResponseDTO> getUserHabits(String userId) {
        return habitRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HabitResponseDTO getHabitById(Long habitId, String userId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        if (!habit.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver este hábito");
        }

        return mapToResponseDTO(habit);
    }

    @Transactional
    public HabitResponseDTO toggleHabitCompletion(Long habitId, String userId, LocalDate date) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        if (!habit.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar este hábito");
        }

        // Validar que la fecha esté dentro del rango del hábito
        if (date.isBefore(habit.getStartDate()) || date.isAfter(habit.getEndDate())) {
            throw new RuntimeException("La fecha está fuera del rango del hábito");
        }

        // Determinar el período según la frecuencia
        LocalDate periodKey = getPeriodKey(date, habit.getFrequency());

        // Buscar si ya existe una entrada para este período
        HabitCompletion existing = habitCompletionRepository
                .findByHabitAndCompletionDate(habit, periodKey)
                .orElse(null);

        if (existing != null) {
            // Si existe, invertir el estado
            existing.setCompleted(!existing.isCompleted());
            habitCompletionRepository.save(existing);
        } else {
            // Si no existe, crear nueva entrada como completada
            HabitCompletion completion = HabitCompletion.builder()
                    .habit(habit)
                    .completionDate(periodKey)
                    .completed(true)
                    .build();
            habitCompletionRepository.save(completion);
        }

        // Actualizar racha
        updateStreak(habit);

        return mapToResponseDTO(habit);
    }

    /**
     * Obtiene la clave del período según la frecuencia
     * - DAILY: retorna la fecha exacta
     * - WEEKLY: retorna el lunes de la semana de la fecha
     */
    private LocalDate getPeriodKey(LocalDate date, Habit.Frequency frequency) {
        if (frequency == Habit.Frequency.DAILY) {
            return date;
        } else if (frequency == Habit.Frequency.WEEKLY) {
            // Obtener el lunes de la semana (primer día ISO)
            return date.with(java.time.DayOfWeek.MONDAY);
        }
        return date;
    }

    private void updateStreak(Habit habit) {
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        int streak = 0;

        // Contar racha actual hacia atrás desde hoy
        while (!checkDate.isBefore(habit.getStartDate())) {
            // Obtener el período correspondiente según la frecuencia
            LocalDate periodKey = getPeriodKey(checkDate, habit.getFrequency());
            
            HabitCompletion completion = habitCompletionRepository
                    .findByHabitAndCompletionDate(habit, periodKey)
                    .orElse(null);

            if (completion != null && completion.isCompleted()) {
                streak++;
                
                // Retroceder según la frecuencia
                if (habit.getFrequency() == Habit.Frequency.WEEKLY) {
                    // Retroceder una semana (7 días)
                    checkDate = checkDate.minusWeeks(1);
                } else {
                    // DAILY: retroceder un día
                    checkDate = checkDate.minusDays(1);
                }
            } else {
                break;
            }
        }

        habit.setCurrentStreak(streak);
        if (streak > habit.getLongestStreak()) {
            habit.setLongestStreak(streak);
        }

        habitRepository.save(habit);
    }

    @Transactional
    public void deleteHabit(Long habitId, String userId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        if (!habit.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar este hábito");
        }

        // Eliminar todas las metas asociadas a este hábito
        try {
            String url = "http://goals-service/api/goals/by-habit/" + habitId;
            log.info("Deleting goals for habit {} at URL: {}", habitId, url);
            try {
                loadBalancedRestTemplate.delete(url);
                log.info("Successfully deleted goals associated with habit {} via load balancer", habitId);
            } catch (Exception e) {
                log.warn("Load balancer failed for delete, trying direct URL: {}", e.getMessage());
                // Si falla el load balancer, intentar con URL directa
                String directUrl = goalsServiceUrl + "/api/goals/by-habit/" + habitId;
                log.info("Deleting goals for habit {} at direct URL: {}", habitId, directUrl);
                restTemplate.delete(directUrl);
                log.info("Successfully deleted goals associated with habit {} via direct URL", habitId);
            }
        } catch (Exception e) {
            log.error("Error deleting associated goals for habit {}: {}", habitId, e.getMessage(), e);
            // Continuar con la eliminación del hábito aunque falle la eliminación de metas
        }

        habitRepository.delete(habit);
    }

    @Transactional
    public HabitResponseDTO updateHabit(Long habitId, String userId, HabitCreateDTO dto) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        if (!habit.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar este hábito");
        }

        // Actualizar los campos
        habit.setName(dto.getName());
        habit.setDescription(dto.getDescription());
        habit.setFrequency(dto.getFrequency());
        habit.setStartDate(dto.getStartDate());
        habit.setEndDate(dto.getEndDate());
        habit.setHabitTime(dto.getHabitTime());
        habit.setLocation(dto.getLocation());
        habit.setCategoryId(dto.getCategoryId());

        Habit updated = habitRepository.save(habit);
        return mapToResponseDTO(updated);
    }

    private HabitResponseDTO mapToResponseDTO(Habit habit) {
        // Verificar si está completado en el período actual (hoy para diario, esta semana para semanal)
        LocalDate today = LocalDate.now();
        LocalDate periodKey = getPeriodKey(today, habit.getFrequency());
        boolean completedToday = habitCompletionRepository
                .findByHabitAndCompletionDate(habit, periodKey)
                .map(HabitCompletion::isCompleted)
                .orElse(false);

        // Calcular porcentaje de progreso
        double progressPercentage = calculateProgressPercentage(habit);

        // Mapear comentarios
        List<CommentResponseDTO> commentDTOs = habit.getComments().stream()
                .map(this::mapCommentToDTO)
                .collect(Collectors.toList());

        // Obtener categoría
        CategoryResponseDTO category = null;
        if (habit.getCategoryId() != null) {
            try {
                category = categoryService.getCategoryById(habit.getCategoryId());
            } catch (Exception e) {
                // Si no se encuentra la categoría, dejamos null
            }
        }

        return HabitResponseDTO.builder()
                .id(habit.getId())
                .name(habit.getName())
                .description(habit.getDescription())
                .userId(habit.getUserId())
                .frequency(habit.getFrequency())
                .startDate(habit.getStartDate())
                .endDate(habit.getEndDate())
                .habitTime(habit.getHabitTime())
                .location(habit.getLocation())
                .status(habit.getStatus())
                .categoryId(habit.getCategoryId())
                .category(category)
                .currentStreak(habit.getCurrentStreak())
                .longestStreak(habit.getLongestStreak())
                .completedToday(completedToday)
                .progressPercentage(progressPercentage)
                .comments(commentDTOs)
                .build();
    }

    private double calculateProgressPercentage(Habit habit) {
        LocalDate start = habit.getStartDate();
        LocalDate end = habit.getEndDate();
        
        // Contar cuántas veces se ha completado el hábito (solo las que están completed=true)
        long completedCount = habitCompletionRepository
                .findAll()
                .stream()
                .filter(c -> c.getHabit().getId().equals(habit.getId()))
                .filter(HabitCompletion::isCompleted)
                .count();
        
        long totalExpectedPeriods;
        
        if (habit.getFrequency() == Habit.Frequency.DAILY) {
            // Para diaria: contar todos los días entre inicio y fin (ambos incluidos)
            totalExpectedPeriods = java.time.temporal.ChronoUnit.DAYS.between(start, end) + 1;
        } else if (habit.getFrequency() == Habit.Frequency.WEEKLY) {
            // Para semanal: contar cuántas semanas completas hay
            // Calcular semanas desde el lunes de la semana de inicio hasta el lunes de la semana de fin
            LocalDate firstMonday = start.with(java.time.DayOfWeek.MONDAY);
            LocalDate lastMonday = end.with(java.time.DayOfWeek.MONDAY);
            totalExpectedPeriods = (java.time.temporal.ChronoUnit.WEEKS.between(firstMonday, lastMonday)) + 1;
        } else {
            totalExpectedPeriods = 1;
        }
        
        if (totalExpectedPeriods == 0) {
            return 0.0;
        }
        
        // Calcular porcentaje: (completados / total) * 100
        double percentage = (completedCount * 100.0) / totalExpectedPeriods;
        return Math.min(100.0, Math.max(0.0, percentage));
    }

    private CommentResponseDTO mapCommentToDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .id(comment.getId())
                .text(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // Métodos para gestionar comentarios
    @Transactional
    public CommentResponseDTO addComment(Long habitId, String userId, CommentCreateDTO dto) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        if (!habit.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para comentar este hábito");
        }

        Comment comment = Comment.builder()
                .content(dto.getText())
                .habit(habit)
                .build();

        Comment saved = commentRepository.save(comment);
        return mapCommentToDTO(saved);
    }

    @Transactional
    public CommentResponseDTO updateComment(Long habitId, Long commentId, String userId, CommentCreateDTO dto) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        if (!habit.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para modificar este comentario");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!comment.getHabit().getId().equals(habitId)) {
            throw new RuntimeException("El comentario no pertenece a este hábito");
        }

        comment.setContent(dto.getText());
        Comment updated = commentRepository.save(comment);
        return mapCommentToDTO(updated);
    }

    @Transactional
    public void deleteComment(Long habitId, Long commentId, String userId) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        if (!habit.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para eliminar este comentario");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!comment.getHabit().getId().equals(habitId)) {
            throw new RuntimeException("El comentario no pertenece a este hábito");
        }

        commentRepository.delete(comment);
    }

    // Obtener detalle completo del hábito con metas y recomendación del tiempo
    @Transactional(readOnly = true)
    public HabitDetailDTO getHabitDetail(Long habitId, String userId, boolean isAdmin, Double latitude, Double longitude) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Hábito no encontrado"));

        // Solo verificar permisos si NO es administrador
        if (!isAdmin && !habit.getUserId().equals(userId)) {
            throw new RuntimeException("No tienes permiso para ver este hábito");
        }

        // Obtener datos básicos del hábito
        HabitResponseDTO habitDTO = mapToResponseDTO(habit);

        // Obtener comentarios
        List<CommentResponseDTO> comments = habit.getComments().stream()
                .map(this::mapCommentToDTO)
                .collect(Collectors.toList());

        // Obtener metas asociadas desde el servicio de Goals
        List<GoalSummaryDTO> goals = fetchGoalsForHabit(habitId);

        // Obtener recomendación del tiempo
        WeatherRecommendationDTO weatherRecommendation = 
            weatherRecommendationService.getRecommendation(habit, latitude, longitude);

        return HabitDetailDTO.builder()
                .habit(habitDTO)
                .comments(comments)
                .goals(goals)
                .weatherRecommendation(weatherRecommendation)
                .build();
    }

    private List<GoalSummaryDTO> fetchGoalsForHabit(Long habitId) {
        try {
            // Intentar primero con load balancer
            String url = "http://goals-service/api/goals/by-habit/" + habitId;
            log.info("Fetching goals for habit {} from URL: {}", habitId, url);
            try {
                GoalSummaryDTO[] goals = loadBalancedRestTemplate.getForObject(url, GoalSummaryDTO[].class);
                log.info("Received {} goals for habit {} via load balancer", goals != null ? goals.length : 0, habitId);
                return goals != null ? Arrays.asList(goals) : List.of();
            } catch (Exception e) {
                log.warn("Load balancer failed, trying direct URL: {}", e.getMessage());
                // Si falla el load balancer, intentar con URL directa
                String directUrl = goalsServiceUrl + "/api/goals/by-habit/" + habitId;
                log.info("Fetching goals for habit {} from direct URL: {}", habitId, directUrl);
                GoalSummaryDTO[] goals = restTemplate.getForObject(directUrl, GoalSummaryDTO[].class);
                log.info("Received {} goals for habit {} via direct URL", goals != null ? goals.length : 0, habitId);
                return goals != null ? Arrays.asList(goals) : List.of();
            }
        } catch (Exception e) {
            // Si el servicio no está disponible o no hay metas, retornamos lista vacía
            log.error("Error fetching goals for habit {}: {}", habitId, e.getMessage(), e);
            return List.of();
        }
    }

    public java.util.Map<String, Object> getGlobalStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        List<Habit> allHabits = habitRepository.findAll();
        long totalHabits = allHabits.size();
        long activeHabits = allHabits.stream()
                .filter(h -> h.getStatus() == Habit.HabitStatus.ACTIVE)
                .count();
        long completedHabits = allHabits.stream()
                .filter(h -> h.getStatus() == Habit.HabitStatus.COMPLETED)
                .count();

        // Calcular total de completaciones de hoy
        long completionsToday = habitCompletionRepository.countByCompletionDate(LocalDate.now());

        stats.put("totalHabits", totalHabits);
        stats.put("activeHabits", activeHabits);
        stats.put("completedHabits", completedHabits);
        stats.put("completionsToday", completionsToday);

        return stats;
    }
}


