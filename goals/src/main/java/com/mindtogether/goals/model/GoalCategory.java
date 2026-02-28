package com.mindtogether.goals.model;

/**
 * Categorías de metas de salud mental y bienestar
 */
public enum GoalCategory {
    MINDFULNESS("Atención Plena", "Prácticas de meditación, respiración y presencia"),
    PHYSICAL_ACTIVITY("Actividad Física", "Ejercicio, movimiento y deporte para el bienestar"),
    SOCIAL_CONNECTION("Conexión Social", "Fortalecer relaciones y vínculos sociales"),
    EMOTIONAL_REGULATION("Regulación Emocional", "Gestión de emociones y estados de ánimo"),
    SLEEP_QUALITY("Calidad del Sueño", "Mejorar hábitos de descanso y sueño"),
    NUTRITION("Nutrición", "Alimentación consciente y saludable"),
    CREATIVE_EXPRESSION("Expresión Creativa", "Arte, música, escritura y creatividad"),
    PERSONAL_GROWTH("Crecimiento Personal", "Desarrollo personal y autoconocimiento"),
    STRESS_MANAGEMENT("Manejo del Estrés", "Técnicas para reducir y gestionar el estrés"),
    GRATITUDE("Gratitud", "Practicar el agradecimiento y pensamiento positivo"),
    SELF_CARE("Autocuidado", "Actividades de cuidado personal"),
    THERAPY_SUPPORT("Apoyo Terapéutico", "Asistencia a terapia y seguimiento profesional"),
    JOURNALING("Diario Personal", "Escritura reflexiva y expresión emocional"),
    OTHER("Otro", "Otras metas de bienestar");

    private final String displayName;
    private final String description;

    GoalCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}

