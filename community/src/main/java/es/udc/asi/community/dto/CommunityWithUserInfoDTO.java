package es.udc.asi.community.dto;

import es.udc.asi.community.model.Community;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityWithUserInfoDTO {

    private Long id;
    private String name;
    private String creationReason;
    private String description;
    private String creatorUserId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int memberCount;
    private int entryCount;

    // Información adicional para el usuario actual
    private boolean isMember;      // ¿Es miembro de esta comunidad?
    private boolean isCreator;     // ¿Es el creador de esta comunidad?

    public static CommunityWithUserInfoDTO from(Community community, boolean isMember, boolean isCreator) {
        return CommunityWithUserInfoDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .creationReason(community.getCreationReason())
                .description(community.getDescription())
                .creatorUserId(community.getCreatorUserId())
                .createdAt(community.getCreatedAt())
                .updatedAt(community.getUpdatedAt())
                .memberCount(community.getMemberCount())
                .entryCount(community.getEntryCount())
                .isMember(isMember)
                .isCreator(isCreator)
                .build();
    }
}

