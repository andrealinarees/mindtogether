package com.mindtogether.goals.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Meta de salud mental y bienestar
 * Puede estar relacionada con una práctica de bienestar del microservicio wellness-practices
 */
@Entity
@Table(name = "mental_health_goals")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MentalHealthGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "wellness_practice_id")
    private Long wellnessPracticeId;  // ID de la práctica de bienestar asociada (opcional)

    @Column(name = "practice_name", length = 200)
    private String practiceName;  // Nombre de la práctica para mostrar

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GoalCategory category;

    @Column(name = "target_value", nullable = false)
    private Integer targetValue;  // Valor objetivo (ej: 21 días, 5 veces, 100 minutos)

    @Column(name = "unit", length = 50)
    @Builder.Default
    private String unit = "veces";  // Unidad de medida (días, veces, minutos, etc.)

    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "personal_motivation", length = 1000)
    private String personalMotivation;  // Por qué es importante esta meta

    @Column(name = "expected_benefit", length = 1000)
    private String expectedBenefit;  // Beneficio esperado al completarla

    @Column(name = "reward_description", length = 500)
    private String rewardDescription;  // Recompensa personal al completarla

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private GoalStatus status = GoalStatus.ACTIVE;

    @Column(name = "current_progress", nullable = false)
    @Builder.Default
    private Integer currentProgress = 0;

    @Column(name = "is_shared_with_circles")
    @Builder.Default
    private Boolean isSharedWithCircles = false;  // Si se comparte en círculos de apoyo

    @Column(name = "reminder_enabled")
    @Builder.Default
    private Boolean reminderEnabled = false;

    @Column(name = "reminder_time")
    private String reminderTime;  // Formato HH:mm

    @Column(name = "difficulty_level")
    @Builder.Default
    private Integer difficultyLevel = 3;  // 1-5 (1=muy fácil, 5=muy difícil)

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;  // Cuándo empezó a trabajar en ella

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_progress_date")
    private LocalDate lastProgressDate;  // Última vez que hubo progreso

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GoalMilestone> milestones = new ArrayList<>();

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ProgressSnapshot> snapshots = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (currentProgress == null) {
            currentProgress = 0;
        }
        if (status == null) {
            status = GoalStatus.ACTIVE;
        }
        if (difficultyLevel == null) {
            difficultyLevel = 3;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Transient
    public double getProgressPercentage() {
        if (targetValue == 0) {
            return 0.0;
        }
        return Math.min(100.0, (currentProgress * 100.0) / targetValue);
    }

    @Transient
    public boolean isOverdue() {
        return status == GoalStatus.ACTIVE && LocalDate.now().isAfter(targetDate);
    }

    @Transient
    public long getDaysRemaining() {
        if (status != GoalStatus.ACTIVE) {
            return 0;
        }
        long days = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), targetDate);
        return Math.max(0, days);
    }

    @Transient
    public long getDaysActive() {
        LocalDate startDate = startedAt != null ? startedAt.toLocalDate() : createdAt.toLocalDate();
        return java.time.temporal.ChronoUnit.DAYS.between(startDate, LocalDate.now());
    }

    @Transient
    public boolean isCompletedEarly() {
        return status == GoalStatus.COMPLETED && completedAt != null && 
               completedAt.toLocalDate().isBefore(targetDate);
    }

    public enum GoalStatus {
        ACTIVE,      // Objetivo activo
        COMPLETED,   // Objetivo completado exitosamente
        FAILED,      // Objetivo fallido (no alcanzado en la fecha límite)
        CANCELLED,   // Objetivo cancelado por el usuario
        PAUSED       // Objetivo pausado temporalmente
    }

    // Métodos helper
    public void addMilestone(GoalMilestone milestone) {
        milestones.add(milestone);
        milestone.setGoal(this);
    }

    public void addSnapshot(ProgressSnapshot snapshot) {
        snapshots.add(snapshot);
        snapshot.setGoal(this);
    }

    public void incrementProgress(int amount) {
        this.currentProgress = Math.min(this.currentProgress + amount, this.targetValue);
        this.lastProgressDate = LocalDate.now();
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }

    public void setProgress(int progress) {
        this.currentProgress = Math.min(progress, this.targetValue);
        this.lastProgressDate = LocalDate.now();
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }
    }
}

