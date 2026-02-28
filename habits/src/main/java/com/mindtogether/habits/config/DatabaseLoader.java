package com.mindtogether.habits.config;

import com.mindtogether.habits.model.Category;
import com.mindtogether.habits.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DatabaseLoader implements ApplicationRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        // Verificar si las categorías actuales son de salud mental
        // Si no existen o son las antiguas genéricas, recargar
        boolean needsReload = categoryRepository.count() == 0;
        if (!needsReload) {
            // Comprobar si existe la categoría "Meditación y Mindfulness" (nueva)
            needsReload = categoryRepository.findAll().stream()
                .noneMatch(c -> c.getName().equals("Meditación y Mindfulness"));
        }
        if (needsReload) {
            categoryRepository.deleteAll();
            loadCategories();
        }
    }

    private void loadCategories() {
        List<Category> categories = Arrays.asList(
            Category.builder()
                .name("Meditación y Mindfulness")
                .description("Prácticas de meditación, atención plena, respiración consciente y escaneo corporal para reducir el estrés.")
                .icon("bi bi-peace-fill")
                .color("#009688")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Gestión del Estrés")
                .description("Técnicas para manejar el estrés: respiración 4-7-8, relajación muscular progresiva, desconexión digital.")
                .icon("bi bi-wind")
                .color("#00BCD4")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Ejercicio y Movimiento")
                .description("Actividad física para la salud mental: caminatas conscientes, yoga, estiramientos y deporte al aire libre.")
                .icon("bi bi-heart-pulse-fill")
                .color("#4CAF50")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Higiene del Sueño")
                .description("Rutinas para mejorar la calidad del descanso: horarios regulares, desconexión antes de dormir, rituales nocturnos.")
                .icon("bi bi-moon-stars-fill")
                .color("#1A237E")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Alimentación Consciente")
                .description("Hábitos de alimentación saludable: comer sin distracciones, hidratación, comidas regulares y nutritivas.")
                .icon("bi bi-cup-hot-fill")
                .color("#8BC34A")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Escritura Terapéutica")
                .description("Diario emocional, escritura de gratitud, reflexiones y procesamiento de pensamientos por escrito.")
                .icon("bi bi-journal-text")
                .color("#3F51B5")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Conexión Social")
                .description("Fortalecer relaciones: llamar a un ser querido, compartir emociones, participar en grupos de apoyo.")
                .icon("bi bi-people-fill")
                .color("#9C27B0")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Autocuidado")
                .description("Rutinas de cuidado personal: skincare, baños relajantes, tiempo a solas, actividades que disfrutas.")
                .icon("bi bi-emoji-smile-fill")
                .color("#E91E63")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Expresión Creativa")
                .description("Arte como terapia: pintura, música, escritura creativa, manualidades y otras formas de expresión artística.")
                .icon("bi bi-palette-fill")
                .color("#FF5722")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Naturaleza y Aire Libre")
                .description("Contacto con la naturaleza: paseos por el parque, baños de bosque, jardinería y actividades al aire libre.")
                .icon("bi bi-tree-fill")
                .color("#2E7D32")
                .createdAt(LocalDateTime.now())
                .build()
        );

        categoryRepository.saveAll(categories);
        System.out.println("✅ Se han cargado " + categories.size() + " categorías de salud mental");
    }
}

