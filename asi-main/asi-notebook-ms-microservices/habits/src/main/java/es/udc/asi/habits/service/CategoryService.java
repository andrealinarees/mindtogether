package es.udc.asi.habits.service;

import es.udc.asi.habits.dto.CategoryResponseDTO;
import es.udc.asi.habits.model.Category;
import es.udc.asi.habits.repository.CategoryRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @PostConstruct
    @Transactional
    public void initializeCategories() {
        if (categoryRepository.count() == 0) {
            log.info("Inicializando categorías predefinidas...");
            
            List<Category> categories = List.of(
                Category.builder()
                    .name("Salud y Bienestar")
                    .description("Hábitos relacionados con ejercicio físico, alimentación y salud mental")
                    .icon("bi-heart-pulse")
                    .color("#ff6b6b")
                    .build(),
                Category.builder()
                    .name("Finanzas")
                    .description("Ahorro, inversión y gestión de gastos")
                    .icon("bi-cash-coin")
                    .color("#51cf66")
                    .build(),
                Category.builder()
                    .name("Desarrollo Personal")
                    .description("Aprendizaje, lectura y crecimiento personal")
                    .icon("bi-book")
                    .color("#4dabf7")
                    .build(),
                Category.builder()
                    .name("Productividad")
                    .description("Organización, gestión del tiempo y eficiencia")
                    .icon("bi-clipboard-check")
                    .color("#845ef7")
                    .build(),
                Category.builder()
                    .name("Relaciones Sociales")
                    .description("Familia, amigos y networking")
                    .icon("bi-people")
                    .color("#f783ac")
                    .build(),
                Category.builder()
                    .name("Hobbies y Creatividad")
                    .description("Arte, música, manualidades y pasatiempos")
                    .icon("bi-palette")
                    .color("#ffa94d")
                    .build(),
                Category.builder()
                    .name("Hogar y Organización")
                    .description("Limpieza, decoración y mantenimiento del hogar")
                    .icon("bi-house")
                    .color("#20c997")
                    .build(),
                Category.builder()
                    .name("Espiritualidad")
                    .description("Meditación, mindfulness y prácticas espirituales")
                    .icon("bi-moon-stars")
                    .color("#9775fa")
                    .build()
            );

            categoryRepository.saveAll(categories);
            log.info("Categorías inicializadas exitosamente: {} categorías creadas", categories.size());
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada"));
        return mapToDTO(category);
    }

    private CategoryResponseDTO mapToDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .color(category.getColor())
                .build();
    }
}
