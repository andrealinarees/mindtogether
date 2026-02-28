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
        if (categoryRepository.count() == 0) {
            loadCategories();
        }
    }

    private void loadCategories() {
        List<Category> categories = Arrays.asList(
            Category.builder()
                .name("Salud y Bienestar")
                .description("Hábitos relacionados con la salud física y mental, como ejercicio, alimentación saludable, meditación y descanso.")
                .icon("bi bi-heart-pulse-fill")
                .color("#e74c3c")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Productividad y Estudio")
                .description("Hábitos para mejorar el rendimiento laboral y académico, gestión del tiempo y aprendizaje continuo.")
                .icon("bi bi-laptop-fill")
                .color("#3498db")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Finanzas Personales")
                .description("Hábitos de ahorro, control de gastos, inversiones y planificación financiera.")
                .icon("bi bi-piggy-bank-fill")
                .color("#2ecc71")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Crecimiento Personal")
                .description("Hábitos para el desarrollo personal, autoconocimiento, lectura, creatividad y nuevas habilidades.")
                .icon("bi bi-star-fill")
                .color("#f39c12")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Relaciones Sociales")
                .description("Hábitos para fortalecer vínculos familiares, amistades y comunicación efectiva.")
                .icon("bi bi-people-fill")
                .color("#9b59b6")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Hogar y Organización")
                .description("Hábitos de limpieza, orden, mantenimiento del hogar y organización del espacio personal.")
                .icon("bi bi-house-heart-fill")
                .color("#1abc9c")
                .createdAt(LocalDateTime.now())
                .build(),

            Category.builder()
                .name("Ocio y Entretenimiento")
                .description("Hábitos de descanso activo, hobbies, actividades recreativas y disfrute del tiempo libre.")
                .icon("bi bi-controller")
                .color("#e67e22")
                .createdAt(LocalDateTime.now())
                .build()
        );

        categoryRepository.saveAll(categories);
        System.out.println("✅ Se han cargado " + categories.size() + " categorías predeterminadas");
    }
}


