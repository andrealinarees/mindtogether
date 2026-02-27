package es.udc.asi.community.repository;

import es.udc.asi.community.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {
    
    Optional<Community> findByName(String name);
    
    List<Community> findByCreatorUserId(String creatorUserId);
    
    @Query("SELECT c FROM Community c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Community> searchByNameOrDescription(@Param("searchTerm") String searchTerm);

    @Query("SELECT DISTINCT c FROM Community c LEFT JOIN FETCH c.members WHERE c.id = :id")
    Optional<Community> findByIdWithMembers(@Param("id") Long id);

    @Query("SELECT DISTINCT c FROM Community c LEFT JOIN FETCH c.entries WHERE c.id = :id")
    Optional<Community> findByIdWithEntries(@Param("id") Long id);

    @Query("SELECT COUNT(m) FROM CommunityMember m WHERE m.community.id = :communityId")
    Long countMembersByCommunityId(@Param("communityId") Long communityId);

    @Query("SELECT COUNT(e) FROM CommunityEntry e WHERE e.community.id = :communityId")
    Long countEntriesByCommunityId(@Param("communityId") Long communityId);
}
