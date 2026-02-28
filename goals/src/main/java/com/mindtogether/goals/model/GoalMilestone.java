package com.mindtogether.goals.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Hito intermedio dentro de una meta de salud mental
 * Permite dividir metas grandes en pasos más pequeños y manejables
 */
@Entity
@Table(name = "goal_milestones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalMilestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private MentalHealthGoal goal;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(name = "target_value", nullable = false)
    private Integer targetValue;  // Valor objetivo para este hito

    @Column(name = "current_progress", nullable = false)
    @Builder.Default
    private Integer currentProgress = 0;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;  // Orden del hito (1, 2, 3...)

    @Column(name = "achievement_date")
    private LocalDate achievementDate;  // Fecha en que se alcanzó

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    @Column(name = "celebration_message", length = 500)
    private String celebrationMessage;  // Mensaje motivacional al completar

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (currentProgress == null) {
            currentProgress = 0;
        }
        if (isCompleted == null) {
            isCompleted = false;
        }
    }

    @Transient
    public double getProgressPercentage() {
        if (targetValue == 0) {
            return 0.0;
        }
        return Math.min(100.0, (currentProgress * 100.0) / targetValue);
    }

    public void markAsCompleted() {
        this.isCompleted = true;
        this.completedAt = LocalDateTime.now();
        this.achievementDate = LocalDate.now();
        this.currentProgress = this.targetValue;
    }

    public void incrementProgress(int amount) {
        this.currentProgress = Math.min(this.currentProgress + amount, this.targetValue);
        if (this.currentProgress >= this.targetValue && !this.isCompleted) {
            markAsCompleted();
        }
    }
}

