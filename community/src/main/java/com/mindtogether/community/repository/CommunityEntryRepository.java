package com.mindtogether.community.repository;

import com.mindtogether.community.model.CommunityEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityEntryRepository extends JpaRepository<CommunityEntry, Long> {
    
    List<CommunityEntry> findByCommunityIdOrderByCreatedAtDesc(Long communityId);
    
    List<CommunityEntry> findByAuthorUserIdOrderByCreatedAtDesc(String authorUserId);
    
    List<CommunityEntry> findByCommunityIdAndTypeOrderByCreatedAtDesc(Long communityId, CommunityEntry.EntryType type);
    
    @Query("SELECT COUNT(ce) FROM CommunityEntry ce WHERE ce.community.id = :communityId")
    Long countEntriesByCommunityId(Long communityId);
    
    @Query("SELECT ce FROM CommunityEntry ce WHERE ce.community.id = :communityId ORDER BY ce.createdAt DESC")
    List<CommunityEntry> findRecentEntriesByCommunityId(Long communityId);
}

