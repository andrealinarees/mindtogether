package es.udc.asi.community.repository;

import es.udc.asi.community.model.CommunityMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {
    
    Optional<CommunityMember> findByCommunityIdAndUserId(Long communityId, String userId);
    
    List<CommunityMember> findByCommunityId(Long communityId);
    
    List<CommunityMember> findByUserId(String userId);
    
    boolean existsByCommunityIdAndUserId(Long communityId, String userId);
    
    @Query("SELECT COUNT(cm) FROM CommunityMember cm WHERE cm.community.id = :communityId")
    Long countMembersByCommunityId(Long communityId);
    
    void deleteByCommunityIdAndUserId(Long communityId, String userId);
}
