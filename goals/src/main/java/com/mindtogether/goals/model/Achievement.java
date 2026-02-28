package com.mindtogether.goals.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Logro desbloqueado por el usuario
 * Gamificación para motivar el progreso en salud mental
 */
@Entity
@Table(name = "achievements",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "achievement_type"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "achievement_type", nullable = false)
    private AchievementType achievementType;

    @Column(name = "unlocked_at", nullable = false)
    private LocalDateTime unlockedAt;

    @Column(name = "related_goal_id")
    private Long relatedGoalId;  // Meta relacionada que desbloqueó el logro (opcional)

    @Column(name = "points_earned", nullable = false)
    private Integer pointsEarned;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;  // Si se muestra destacado en el perfil

    @Column(name = "notification_sent")
    @Builder.Default
    private Boolean notificationSent = false;

    @Column(name = "shared_with_circles")
    @Builder.Default
    private Boolean sharedWithCircles = false;  // Si se compartió en círculos de apoyo

    @PrePersist
    protected void onCreate() {
        unlockedAt = LocalDateTime.now();
        if (pointsEarned == null) {
            pointsEarned = achievementType.getPoints();
        }
        if (isFeatured == null) {
            isFeatured = false;
        }
        if (notificationSent == null) {
            notificationSent = false;
        }
        if (sharedWithCircles == null) {
            sharedWithCircles = false;
        }
    }

    @Transient
    public String getDisplayName() {
        return achievementType.getDisplayName();
    }

    @Transient
    public String getDescription() {
        return achievementType.getDescription();
    }

    @Transient
    public boolean isRecent() {
        // Considera "reciente" si fue desbloqueado en los últimos 7 días
        return unlockedAt.isAfter(LocalDateTime.now().minusDays(7));
    }
}

