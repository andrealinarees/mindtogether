package com.mindtogether.goals.model;

/**
 * Tipos de logros desbloqueables en el sistema
 */
public enum AchievementType {
    // Logros de primer paso
    FIRST_GOAL_CREATED("Primera Meta", "Crear tu primera meta de bienestar", 10),
    FIRST_GOAL_COMPLETED("Primer Logro", "Completar tu primera meta", 25),
    FIRST_MILESTONE("Primer Hito", "Alcanzar tu primer hito", 15),
    
    // Logros de cantidad
    GOALS_COMPLETED_5("5 Metas Completadas", "Completar 5 metas de bienestar", 50),
    GOALS_COMPLETED_10("10 Metas Completadas", "Completar 10 metas de bienestar", 100),
    GOALS_COMPLETED_25("25 Metas Completadas", "Completar 25 metas de bienestar", 250),
    GOALS_COMPLETED_50("50 Metas Completadas", "Completar 50 metas de bienestar", 500),
    GOALS_COMPLETED_100("100 Metas Completadas", "Completar 100 metas de bienestar", 1000),
    
    // Logros de racha
    STREAK_7_DAYS("Racha de 7 Días", "Trabajar en tus metas 7 días seguidos", 50),
    STREAK_30_DAYS("Racha de 30 Días", "Trabajar en tus metas 30 días seguidos", 200),
    STREAK_90_DAYS("Racha de 90 Días", "Trabajar en tus metas 90 días seguidos", 500),
    
    // Logros por categoría
    MINDFULNESS_MASTER("Maestro de Mindfulness", "Completar 10 metas de atención plena", 100),
    FITNESS_CHAMPION("Campeón del Fitness", "Completar 10 metas de actividad física", 100),
    SOCIAL_BUTTERFLY("Mariposa Social", "Completar 10 metas de conexión social", 100),
    EMOTIONAL_WARRIOR("Guerrero Emocional", "Completar 10 metas de regulación emocional", 100),
    SLEEP_EXPERT("Experto en Descanso", "Completar 10 metas de calidad del sueño", 100),
    CREATIVE_SOUL("Alma Creativa", "Completar 10 metas de expresión creativa", 100),
    
    // Logros especiales
    OVERACHIEVER("Sobresaliente", "Completar una meta antes de la fecha límite", 30),
    PERFECTIONIST("Perfeccionista", "Completar 5 metas al 100%", 75),
    RESILIENT("Resiliente", "Completar una meta después de fallar otra", 40),
    CONSISTENT("Consistente", "Mantener 3 metas activas simultáneamente", 60),
    MOTIVATOR("Motivador", "Compartir progreso en círculos de apoyo 10 veces", 80),
    
    // Logros de progreso
    HALFWAY_HERO("Héroe a Medio Camino", "Alcanzar el 50% en 10 metas diferentes", 120),
    QUICK_STARTER("Inicio Rápido", "Alcanzar el 25% de una meta en 3 días", 35),
    
    // Logros anuales
    YEARLY_CHAMPION("Campeón del Año", "Completar 50 metas en un año", 1000),
    MONTHLY_ACHIEVER("Logro Mensual", "Completar al menos 5 metas cada mes durante 3 meses", 300);

    private final String displayName;
    private final String description;
    private final int points;

    AchievementType(String displayName, String description, int points) {
        this.displayName = displayName;
        this.description = description;
        this.points = points;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public int getPoints() {
        return points;
    }
}

