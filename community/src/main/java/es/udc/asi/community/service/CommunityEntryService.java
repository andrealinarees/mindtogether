package es.udc.asi.community.service;

import es.udc.asi.community.model.Community;
import es.udc.asi.community.model.CommunityEntry;
import es.udc.asi.community.repository.CommunityEntryRepository;
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
public class CommunityEntryService {

    private final CommunityEntryRepository entryRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository memberRepository;

    public List<CommunityEntry> getCommunityEntries(Long communityId) {
        return entryRepository.findByCommunityIdOrderByCreatedAtDesc(communityId);
    }

    public List<CommunityEntry> getEntriesByType(Long communityId, CommunityEntry.EntryType type) {
        return entryRepository.findByCommunityIdAndTypeOrderByCreatedAtDesc(communityId, type);
    }

    public List<CommunityEntry> getUserEntries(String userId) {
        return entryRepository.findByAuthorUserIdOrderByCreatedAtDesc(userId);
    }

    public Optional<CommunityEntry> getEntryById(Long entryId) {
        return entryRepository.findById(entryId);
    }

    @Transactional
    public CommunityEntry createEntry(Long communityId, CommunityEntry entry, String userId) {
        log.info("Creating entry in community {} by user {}", communityId, userId);
        
        // Verificar que la comunidad existe
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new IllegalArgumentException("Community not found"));
        
        // Verificar que el usuario es miembro de la comunidad
        if (!memberRepository.existsByCommunityIdAndUserId(communityId, userId)) {
            throw new SecurityException("User must be a member to create entries");
        }
        
        entry.setCommunity(community);
        entry.setAuthorUserId(userId);
        
        return entryRepository.save(entry);
    }

    @Transactional
    public Optional<CommunityEntry> updateEntry(Long entryId, CommunityEntry updatedEntry, String userId) {
        return entryRepository.findById(entryId)
                .map(entry -> {
                    // Solo el autor puede editar su propia entrada
                    if (!entry.getAuthorUserId().equals(userId)) {
                        throw new SecurityException("Only the author can edit this entry");
                    }
                    
                    entry.setContent(updatedEntry.getContent());
                    entry.setType(updatedEntry.getType());
                    
                    return entryRepository.save(entry);
                });
    }

    @Transactional
    public boolean deleteEntry(Long entryId, String userId, boolean isAdmin) {
        Optional<CommunityEntry> entry = entryRepository.findById(entryId);
        
        if (entry.isEmpty()) {
            return false;
        }
        
        // El autor o un administrador pueden eliminar la entrada
        if (!entry.get().getAuthorUserId().equals(userId) && !isAdmin) {
            throw new SecurityException("Not authorized to delete this entry");
        }
        
        entryRepository.deleteById(entryId);
        return true;
    }

    public Long getEntryCount(Long communityId) {
        return entryRepository.countEntriesByCommunityId(communityId);
    }
}
