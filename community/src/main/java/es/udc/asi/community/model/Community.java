package es.udc.asi.community.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "communities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 500)
    private String creationReason;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private String creatorUserId;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<CommunityMember> members = new ArrayList<>();

    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<CommunityEntry> entries = new ArrayList<>();

    // Campos para almacenar los contadores sin acceder a las colecciones
    @Transient
    private Integer memberCountCache;

    @Transient
    private Integer entryCountCache;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public int getMemberCount() {
        if (memberCountCache != null) {
            return memberCountCache;
        }
        // Solo intentar acceder si la colección está inicializada
        if (members != null && org.hibernate.Hibernate.isInitialized(members)) {
            return members.size();
        }
        return 0;
    }

    public int getEntryCount() {
        if (entryCountCache != null) {
            return entryCountCache;
        }
        // Solo intentar acceder si la colección está inicializada
        if (entries != null && org.hibernate.Hibernate.isInitialized(entries)) {
            return entries.size();
        }
        return 0;
    }

    public void setMemberCountCache(Integer count) {
        this.memberCountCache = count;
    }

    public void setEntryCountCache(Integer count) {
        this.entryCountCache = count;
    }
}
