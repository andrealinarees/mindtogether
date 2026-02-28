package com.mindtogether.community.web;

import com.mindtogether.community.dto.CommunityWithUserInfoDTO;
import com.mindtogether.community.model.Community;
import com.mindtogether.community.model.CommunityEntry;
import com.mindtogether.community.model.CommunityMember;
import com.mindtogether.community.service.CommunityEntryService;
import com.mindtogether.community.service.CommunityMemberService;
import com.mindtogether.community.service.CommunityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
@Slf4j
public class CommunityResource {

    private final CommunityService communityService;
    private final CommunityMemberService memberService;
    private final CommunityEntryService entryService;

    // ==================== COMMUNITY MANAGEMENT ====================

    /**
     * HU31 - Ver comunidades disponibles
     * GET /api/communities
     */
    @GetMapping
    public ResponseEntity<List<CommunityWithUserInfoDTO>> getAllCommunities(
            @RequestParam(required = false) String sort,
            @RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/communities - sort: {}, userId: {}", sort, userId);

        List<CommunityWithUserInfoDTO> communities = communityService.getAllCommunitiesWithUserInfo(userId);

        // Ordenar si se solicita
        if ("members".equals(sort)) {
            communities.sort((c1, c2) -> Integer.compare(c2.getMemberCount(), c1.getMemberCount()));
        }

        return ResponseEntity.ok(communities);
    }

    /**
     * HU31 - Ver comunidades del usuario
     * GET /api/communities/my-communities
     */
    @GetMapping("/my-communities")
    public ResponseEntity<List<CommunityWithUserInfoDTO>> getUserCommunities(@RequestHeader("X-User-Id") String userId) {
        log.info("🔍 GET /api/communities/my-communities - user: {}", userId);

        // Obtener las comunidades del usuario
        List<Community> communities = communityService.getUserCommunities(userId);

        // Convertir a DTOs - todas son comunidades del usuario, así que isMember=true
        List<CommunityWithUserInfoDTO> dtos = communities.stream()
                .map(community -> CommunityWithUserInfoDTO.from(
                    community,
                    true, // Siempre true porque son "mis comunidades"
                    community.getCreatorUserId().equals(userId) // Es creador si coincide el userId
                ))
                .toList();

        log.info("✅ Returning {} communities for user {}", dtos.size(), userId);
        dtos.forEach(c -> log.info("   - {} (id: {}, creator: {}, members: {}, isMember: {}, isCreator: {})",
            c.getName(), c.getId(), c.getCreatorUserId(), c.getMemberCount(), c.isMember(), c.isCreator()));

        return ResponseEntity.ok(dtos);
    }

    /**
     * GET /api/communities/{id} - Ver detalle de comunidad
     */
    @GetMapping("/{id}")
    public ResponseEntity<Community> getCommunityById(@PathVariable Long id) {
        log.info("GET /api/communities/{}", id);
        return communityService.getCommunityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/communities/search - Buscar comunidades
     */
    @GetMapping("/search")
    public ResponseEntity<List<Community>> searchCommunities(@RequestParam String query) {
        log.info("GET /api/communities/search - query: {}", query);
        List<Community> communities = communityService.searchCommunities(query);
        return ResponseEntity.ok(communities);
    }

    /**
     * HU26 - Crear comunidad
     * POST /api/communities
     */
    @PostMapping
    public ResponseEntity<Community> createCommunity(@RequestBody Community community,
                                                    @RequestHeader("X-User-Id") String userId,
                                                    @RequestHeader("X-User") String username) {
        community.setCreatorUserId(userId);

        try {
            Community created = communityService.createCommunity(community, username); // ✅ aquí
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * HU27 - Editar comunidad
     * PUT /api/communities/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Community> updateCommunity(@PathVariable Long id, 
                                                      @RequestBody Community community,
                                                      @RequestHeader("X-User-Id") String userId,
                                                      @RequestHeader(value = "X-Roles", required = false) String roles) {
        log.info("PUT /api/communities/{} - user: {}, roles: {}", id, userId, roles);

        boolean isAdmin = roles != null && roles.contains("ADMIN");

        try {
            return communityService.updateCommunity(id, community, userId, isAdmin)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (SecurityException e) {
            log.warn("❌ User {} is not authorized to update community {}", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * HU28 - Eliminar comunidad
     * DELETE /api/communities/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommunity(@PathVariable Long id, 
                                                 @RequestHeader("X-User-Id") String userId,
                                                 @RequestHeader(value = "X-Roles", required = false) String roles) {
        log.info("DELETE /api/communities/{} - user: {}, roles: {}", id, userId, roles);

        boolean isAdmin = roles != null && roles.contains("ADMIN");

        try {
            boolean deleted = communityService.deleteCommunity(id, userId, isAdmin);
            if (deleted) {
                log.info("✅ Community {} deleted successfully by user {}", id, userId);
            }
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            log.warn("❌ User {} is not authorized to delete community {}", userId, id);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    // ==================== MEMBERSHIP MANAGEMENT ====================

    /**
     * GET /api/communities/{id}/members - Ver miembros de la comunidad
     */
    @GetMapping("/{id}/members")
    public ResponseEntity<List<com.mindtogether.community.dto.CommunityMemberViewDTO>> getCommunityMembers(@PathVariable Long id) {
        log.info("GET /api/communities/{}/members", id);
        return ResponseEntity.ok(memberService.getCommunityMembersView(id));
    }

    /**
     * GET /api/communities/{id}/members/count - Contar miembros
     */
    @GetMapping("/{id}/members/count")
    public ResponseEntity<Long> getMemberCount(@PathVariable Long id) {
        log.info("GET /api/communities/{}/members/count", id);
        Long count = memberService.getMemberCount(id);
        return ResponseEntity.ok(count);
    }

    /**
     * HU29 - Unirse a comunidad
     * POST /api/communities/{id}/join
     */
    @PostMapping("/{id}/join")
    public ResponseEntity<com.mindtogether.community.dto.CommunityMemberViewDTO> joinCommunity(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User") String username
    ) {
        log.info("🤝 POST /api/communities/{}/join - user: {} ({})", id, userId, username);

        try {
            var member = memberService.joinCommunity(id, userId, username);
            log.info("✅ User {} joined community {} successfully", userId, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(member);
        } catch (IllegalArgumentException e) {
            log.warn("❌ Community {} not found", id);
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            log.warn("❌ User {} is already a member of community {}", userId, id);
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    /**
     * HU30 - Abandonar comunidad
     * DELETE /api/communities/{id}/leave
     */
    @DeleteMapping("/{id}/leave")
    public ResponseEntity<Void> leaveCommunity(@PathVariable Long id, 
                                                @RequestHeader("X-User-Id") String userId) {
        log.info("DELETE /api/communities/{}/leave - user: {}", id, userId);
        
        try {
            boolean left = memberService.leaveCommunity(id, userId);
            return left ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * PUT /api/communities/{id}/members/{userId}/role - Cambiar rol de miembro (solo creador)
     */
    @PutMapping("/{id}/members/{userId}/role")
    public ResponseEntity<CommunityMember> updateMemberRole(@PathVariable Long id,
                                                             @PathVariable String userId,
                                                             @RequestParam CommunityMember.MemberRole role,
                                                             @RequestHeader("X-User-Id") String adminUserId) {
        log.info("PUT /api/communities/{}/members/{}/role - new role: {}, admin: {}", id, userId, role, adminUserId);
        
        try {
            return memberService.updateMemberRole(id, userId, role, adminUserId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * PATCH /api/communities/{id}/members/me/anonymous - Cambiar modo anónimo (persistente)
     */
    @PatchMapping("/{id}/members/me/anonymous")
    public ResponseEntity<Void> updateMyAnonymous(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") String userId,
            @RequestBody UpdateAnonymousRequest req
    ) {
        log.info("PATCH /api/communities/{}/members/me/anonymous - user: {}, anonymous: {}", id, userId, req.anonymous());
        memberService.updateMyAnonymous(id, userId, req.anonymous());
        return ResponseEntity.noContent().build();
    }

    public record UpdateAnonymousRequest(boolean anonymous) {}

        // ==================== ENTRY MANAGEMENT ====================

    /**
     * HU35 - Ver entradas de comunidad
     * GET /api/communities/{id}/entries
     */
    @GetMapping("/{id}/entries")
    public ResponseEntity<List<CommunityEntry>> getCommunityEntries(@PathVariable Long id,
                                                                      @RequestParam(required = false) CommunityEntry.EntryType type) {
        log.info("GET /api/communities/{}/entries - type: {}", id, type);
        
        List<CommunityEntry> entries = type != null 
            ? entryService.getEntriesByType(id, type)
            : entryService.getCommunityEntries(id);
            
        return ResponseEntity.ok(entries);
    }

    /**
     * GET /api/communities/entries/my-entries - Ver entradas del usuario
     */
    @GetMapping("/entries/my-entries")
    public ResponseEntity<List<CommunityEntry>> getUserEntries(@RequestHeader("X-User-Id") String userId) {
        log.info("GET /api/communities/entries/my-entries - user: {}", userId);
        List<CommunityEntry> entries = entryService.getUserEntries(userId);
        return ResponseEntity.ok(entries);
    }

    /**
     * HU32 - Añadir entrada en comunidad
     * POST /api/communities/{id}/entries
     */
    @PostMapping("/{id}/entries")
    public ResponseEntity<CommunityEntry> createEntry(@PathVariable Long id,
                                                       @RequestBody CommunityEntry entry,
                                                       @RequestHeader("X-User-Id") String userId) {
        log.info("POST /api/communities/{}/entries - user: {}, type: {}", id, userId, entry.getType());
        
        try {
            CommunityEntry created = entryService.createEntry(id, entry, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * HU33 - Editar entrada de comunidad
     * PUT /api/communities/{communityId}/entries/{entryId}
     */
    @PutMapping("/{communityId}/entries/{entryId}")
    public ResponseEntity<CommunityEntry> updateEntry(@PathVariable Long communityId,
                                                       @PathVariable Long entryId,
                                                       @RequestBody CommunityEntry entry,
                                                       @RequestHeader("X-User-Id") String userId) {
        log.info("PUT /api/communities/{}/entries/{} - user: {}", communityId, entryId, userId);
        
        try {
            return entryService.updateEntry(entryId, entry, userId)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * HU34 - Eliminar entrada de comunidad
     * DELETE /api/communities/{communityId}/entries/{entryId}
     */
    @DeleteMapping("/{communityId}/entries/{entryId}")
    public ResponseEntity<Void> deleteEntry(@PathVariable Long communityId,
                                             @PathVariable Long entryId,
                                             @RequestHeader("X-User-Id") String userId,
                                             @RequestHeader(value = "X-Roles", required = false) String roles) {
        log.info("DELETE /api/communities/{}/entries/{} - user: {}", communityId, entryId, userId);
        
        boolean isAdmin = roles != null && roles.contains("ADMIN");
        
        try {
            boolean deleted = entryService.deleteEntry(entryId, userId, isAdmin);
            return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * GET /api/communities/{id}/entries/count - Contar entradas
     */
    @GetMapping("/{id}/entries/count")
    public ResponseEntity<Long> getEntryCount(@PathVariable Long id) {
        log.info("GET /api/communities/{}/entries/count", id);
        Long count = entryService.getEntryCount(id);
        return ResponseEntity.ok(count);
    }

    /**
     * Estadísticas generales de comunidades (para administradores)
     */
    @GetMapping("/stats")
    public ResponseEntity<java.util.Map<String, Object>> getCommunityStats() {
        log.info("GET /api/communities/stats");
        java.util.Map<String, Object> stats = communityService.getGlobalStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Community service is running");
    }
}

