package es.udc.asi.community.service;

import es.udc.asi.community.model.Community;
import es.udc.asi.community.model.CommunityMember;
import es.udc.asi.community.repository.CommunityMemberRepository;
import es.udc.asi.community.repository.CommunityRepository;
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

    public List<CommunityMember> getCommunityMembers(Long communityId) {
        return memberRepository.findByCommunityId(communityId);
    }

    public Long getMemberCount(Long communityId) {
        return memberRepository.countMembersByCommunityId(communityId);
    }

    @Transactional
    public CommunityMember joinCommunity(Long communityId, String userId) {
        log.info("User {} joining community {}", userId, communityId);
        
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
                .role(CommunityMember.MemberRole.MEMBER)
                .build();
        
        return memberRepository.save(member);
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
    public Optional<CommunityMember> updateMemberRole(Long communityId, String userId, CommunityMember.MemberRole newRole, String adminUserId) {
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
}
