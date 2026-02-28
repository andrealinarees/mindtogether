package com.mindtogether.goals.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Recompensa personalizada creada por el usuario y asociada a una meta.
 * Cuando la meta se completa, la recompensa se desbloquea automáticamente.
 */
@Entity
@Table(name = "custom_rewards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    /** Meta básica asociada (Goal) */
    @Column(name = "goal_id")
    private Long goalId;

    /** Meta de salud mental asociada (MentalHealthGoal) */
    @Column(name = "mental_health_goal_id")
    private Long mentalHealthGoalId;

    /** Nombre de la recompensa */
    @Column(nullable = false, length = 200)
    private String name;

    /** Descripción detallada de la recompensa */
    @Column(length = 1000)
    private String description;

    /** Emoji o icono representativo */
    @Column(length = 10)
    @Builder.Default
    private String icon = "🎁";

    /** Categoría de la recompensa */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RewardCategory category = RewardCategory.OTHER;

    /** Estado de la recompensa */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RewardStatus status = RewardStatus.LOCKED;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "unlocked_at")
    private LocalDateTime unlockedAt;

    @Column(name = "claimed_at")
    private LocalDateTime claimedAt;

    /** Nombre de la meta asociada (transient, se puebla desde el servicio) */
    @Column(name = "goal_name", length = 200)
    private String goalName;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = RewardStatus.LOCKED;
        }
        if (category == null) {
            category = RewardCategory.OTHER;
        }
        if (icon == null) {
            icon = "🎁";
        }
    }

    @Transient
    public boolean isUnlocked() {
        return status == RewardStatus.UNLOCKED || status == RewardStatus.CLAIMED;
    }

    @Transient
    public boolean isClaimed() {
        return status == RewardStatus.CLAIMED;
    }

    public enum RewardStatus {
        LOCKED,     // La meta aún no se ha completado
        UNLOCKED,   // La meta se completó, recompensa disponible
        CLAIMED     // El usuario marcó la recompensa como reclamada
    }

    public enum RewardCategory {
        MATERIAL,    // Compras, objetos físicos
        EXPERIENCE,  // Experiencias, viajes, actividades
        PERSONAL,    // Autocuidado, descanso, tiempo libre
        SOCIAL,      // Actividades sociales, salidas
        FOOD,        // Comida, restaurantes, cocinar algo especial
        DIGITAL,     // Suscripciones, juegos, apps
        OTHER        // Otros
    }
}
