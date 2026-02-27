package es.udc.asi.habits.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private Long id;
    private String text; // mapeado desde content
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

