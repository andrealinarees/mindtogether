package com.mindtogether.community.service;

import com.mindtogether.community.dto.CommunityWithUserInfoDTO;
import com.mindtogether.community.model.Community;
import com.mindtogether.community.model.CommunityMember;
import com.mindtogether.community.repository.CommunityMemberRepository;
import com.mindtogether.community.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository memberRepository;

    public List<Community> getAllCommunities() {
        List<Community> communities = communityRepository.findAll();
        // Establecer los contadores para cada comunidad
        communities.forEach(community -> {
            Long memberCount = communityRepository.countMembersByCommunityId(community.getId());
            Long entryCount = communityRepository.countEntriesByCommunityId(community.getId());
            community.setMemberCountCache(memberCount != null ? memberCount.intValue() : 0);
            community.setEntryCountCache(entryCount != null ? entryCount.intValue() : 0);
        });
        return communities;
    }

    public List<CommunityWithUserInfoDTO> getAllCommunitiesWithUserInfo(String userId) {
        log.info("📋 Getting all communities with user info for user: {}", userId);
        List<Community> communities = getAllCommunities();

        // Obtener todas las comunidades en las que el usuario es miembro
        List<CommunityMember> userMemberships = memberRepository.findByUserId(userId);
        Set<Long> memberCommunityIds = userMemberships.stream()
                .map(m -> m.getCommunity().getId())
                .collect(Collectors.toSet());

        log.info("   User {} is member of {} communities", userId, memberCommunityIds.size());

        // Convertir a DTO con información de membresía
        List<CommunityWithUserInfoDTO> result = communities.stream()
                .map(community -> {
                    boolean isMember = memberCommunityIds.contains(community.getId());
                    boolean isCreator = community.getCreatorUserId().equals(userId);
                    log.debug("   Community {}: isMember={}, isCreator={}",
                            community.getName(), isMember, isCreator);
                    return CommunityWithUserInfoDTO.from(community, isMember, isCreator);
                })
                .toList();

        log.info("✅ Returning {} communities with user info", result.size());
        return result;
    }

    public List<Community> getCommunitiesOrderedByMembers() {
        // Este método ya no existe en el repositorio, usar getAllCommunities y ordenar
        List<Community> communities = getAllCommunities();
        communities.sort((c1, c2) -> Integer.compare(c2.getMemberCount(), c1.getMemberCount()));
        return communities;
    }

    public Optional<Community> getCommunityById(Long id) {
        Optional<Community> communityOpt = communityRepository.findById(id);
        
        if (communityOpt.isPresent()) {
            Community community = communityOpt.get();
            
            // Calcular y establecer los contadores
            Long memberCount = communityRepository.countMembersByCommunityId(id);
            Long entryCount = communityRepository.countEntriesByCommunityId(id);
            
            community.setMemberCountCache(memberCount != null ? memberCount.intValue() : 0);
            community.setEntryCountCache(entryCount != null ? entryCount.intValue() : 0);
            
            log.info("✅ Community {} has {} members and {} entries", id, memberCount, entryCount);
        }
        
        return communityOpt;
    }

    public List<Community> searchCommunities(String searchTerm) {
        return communityRepository.searchByNameOrDescription(searchTerm);
    }

    public List<Community> getUserCommunities(String userId) {
        log.info("📋 Getting communities for user: {}", userId);
        List<CommunityMember> memberships = memberRepository.findByUserId(userId);
        log.info("   Found {} memberships", memberships.size());

        List<Community> userCommunities = memberships.stream()
                .map(CommunityMember::getCommunity)
                .toList();

        // Recargar desde el repository para asegurar que los contadores están correctos
        List<Long> communityIds = userCommunities.stream()
                .map(Community::getId)
                .toList();

        List<Community> result = communityRepository.findAllById(communityIds);

        // Establecer los contadores para cada comunidad
        result.forEach(community -> {
            Long memberCount = communityRepository.countMembersByCommunityId(community.getId());
            Long entryCount = communityRepository.countEntriesByCommunityId(community.getId());
            community.setMemberCountCache(memberCount != null ? memberCount.intValue() : 0);
            community.setEntryCountCache(entryCount != null ? entryCount.intValue() : 0);
        });

        log.info("   Returning {} communities with full data", result.size());
        return result;
    }

    @Transactional
    public Community createCommunity(Community community) {
        log.info("Creating community: {} by user: {}", community.getName(), community.getCreatorUserId());
        
        // Verificar que no exista una comunidad con el mismo nombre
        if (communityRepository.findByName(community.getName()).isPresent()) {
            throw new IllegalArgumentException("A community with this name already exists");
        }
        
        Community savedCommunity = communityRepository.save(community);
        
        // Añadir al creador como miembro con rol ADMIN
        CommunityMember creatorMembership = CommunityMember.builder()
                .community(savedCommunity)
                .userId(community.getCreatorUserId())
                .role(CommunityMember.MemberRole.ADMIN)
                .build();
        
        memberRepository.save(creatorMembership);
        
        // Recargar la comunidad para obtener los contadores actualizados
        return communityRepository.findById(savedCommunity.getId())
                .orElse(savedCommunity);
    }

    @Transactional
    public Optional<Community> updateCommunity(Long id, Community updatedCommunity, String userId, boolean isAdmin) {
        return communityRepository.findById(id)
                .map(community -> {
                    // Solo el creador o un administrador pueden editar la comunidad
                    if (!isAdmin && !community.getCreatorUserId().equals(userId)) {
                        throw new SecurityException("Only the creator or an admin can update this community");
                    }
                    
                    community.setName(updatedCommunity.getName());
                    community.setCreationReason(updatedCommunity.getCreationReason());
                    community.setDescription(updatedCommunity.getDescription());
                    
                    return communityRepository.save(community);
                });
    }

    @Transactional
    public boolean deleteCommunity(Long id, String userId, boolean isAdmin) {
        Optional<Community> community = communityRepository.findById(id);
        
        if (community.isEmpty()) {
            return false;
        }
        
        // Solo el creador o un administrador pueden eliminar la comunidad
        if (!isAdmin && !community.get().getCreatorUserId().equals(userId)) {
            throw new SecurityException("Only the creator or an admin can delete this community");
        }
        
        communityRepository.deleteById(id);
        return true;
    }

    public boolean isUserMember(Long communityId, String userId) {
        return memberRepository.existsByCommunityIdAndUserId(communityId, userId);
    }

    public boolean isUserAuthorized(Long communityId, String userId) {
        Optional<CommunityMember> member = memberRepository.findByCommunityIdAndUserId(communityId, userId);
        
        if (member.isEmpty()) {
            return false;
        }
        
        CommunityMember.MemberRole role = member.get().getRole();
        return role == CommunityMember.MemberRole.ADMIN || role == CommunityMember.MemberRole.MODERATOR;
    }

    public boolean isUserCreator(Long communityId, String userId) {
        Optional<Community> community = communityRepository.findById(communityId);
        return community.isPresent() && community.get().getCreatorUserId().equals(userId);
    }

    public java.util.Map<String, Object> getGlobalStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();

        List<Community> allCommunities = communityRepository.findAll();
        long totalCommunities = allCommunities.size();

        // Calcular total de miembros (sumando todos los miembros de todas las comunidades)
        long totalMembers = memberRepository.count();

        // Calcular total de entradas/publicaciones
        long totalEntries = allCommunities.stream()
                .mapToLong(c -> communityRepository.countEntriesByCommunityId(c.getId()))
                .sum();

        stats.put("totalCommunities", totalCommunities);
        stats.put("totalMembers", totalMembers);
        stats.put("totalEntries", totalEntries);

        return stats;
    }
}

