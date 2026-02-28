package com.mindtogether.community.service;

import com.mindtogether.community.dto.CommunityMemberViewDTO;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityMemberService {

    private final CommunityMemberRepository memberRepository;
    private final CommunityRepository communityRepository;

    /**
     * ⚠️ Si aún lo usas en algún sitio, lo puedes dejar.
     * Pero para el frontend usa el método DTO: getCommunityMembersView(...)
     */
    public List<CommunityMember> getCommunityMembers(Long communityId) {
        return memberRepository.findByCommunityId(communityId);
    }

    /**
     * ✅ Nuevo: devuelve miembros como DTO y aplica anonimato:
     * - anonymous=true -> username=null
     * - anonymous=false -> username=valor real
     */
    public List<CommunityMemberViewDTO> getCommunityMembersView(Long communityId) {
        List<CommunityMember> members = memberRepository.findByCommunityId(communityId);

        return members.stream()
                .map(this::toViewDto)
                .toList();
    }

    public Long getMemberCount(Long communityId) {
        return memberRepository.countMembersByCommunityId(communityId);
    }

    /**
     * ✅ Cambiado: ahora recibe username y lo guarda
     */
    @Transactional
    public CommunityMemberViewDTO joinCommunity(Long communityId, String userId, String username, boolean anonymous) {
        log.info("User {} ({}) joining community {} (anonymous: {})", userId, username, communityId, anonymous);

        // Verificar que la comunidad existe
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        // Verificar que el usuario no es ya miembro
        if (memberRepository.existsByCommunityIdAndUserId(communityId, userId)) {
            throw new IllegalStateException("User is already a member of this community");
        }

        CommunityMember member = CommunityMember.builder()
                .community(community)
                .userId(userId)
                .username(username)
                .anonymous(anonymous)
                .role(CommunityMember.MemberRole.MEMBER)
                .build();

        member = memberRepository.save(member);

        return toViewDto(member);
    }

    @Transactional
    public boolean leaveCommunity(Long communityId, String userId) {
        log.info("User {} leaving community {}", userId, communityId);

        // Verificar que el usuario no es el creador
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (community.getCreatorUserId().equals(userId)) {
            throw new IllegalStateException("Creator cannot leave the community. Delete the community instead.");
        }

        Optional<CommunityMember> member = memberRepository.findByCommunityIdAndUserId(communityId, userId);

        if (member.isEmpty()) {
            return false;
        }

        memberRepository.delete(member.get());
        return true;
    }

    @Transactional
    public Optional<CommunityMember> updateMemberRole(Long communityId, String userId,
                                                     CommunityMember.MemberRole newRole, String adminUserId) {
        // Verificar que el adminUser tiene permisos
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));

        if (!community.getCreatorUserId().equals(adminUserId)) {
            throw new SecurityException("Only the creator can change member roles");
        }

        return memberRepository.findByCommunityIdAndUserId(communityId, userId)
                .map(member -> {
                    member.setRole(newRole);
                    return memberRepository.save(member);
                });
    }

    /**
     * ✅ (Recomendado) Guardar anonimato en servidor para que todos lo vean
     */
    @Transactional
    public void updateMyAnonymous(Long communityId, String userId, boolean anonymous) {
        CommunityMember member = memberRepository.findByCommunityIdAndUserId(communityId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Membership not found"));

        member.setAnonymous(anonymous);
        memberRepository.save(member);
    }

    private CommunityMemberViewDTO toViewDto(CommunityMember m) {
        return new CommunityMemberViewDTO(
                m.getId(),
                m.getUserId(),
                m.isAnonymous() ? null : m.getUsername(),
                m.isAnonymous(),
                m.getRole().name(),
                m.getJoinedAt()
        );
    }
}