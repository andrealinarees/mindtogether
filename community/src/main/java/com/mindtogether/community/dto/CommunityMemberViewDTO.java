package com.mindtogether.community.dto;

import java.time.LocalDateTime;

public record CommunityMemberViewDTO(
        Long id,
        String userId,
        String username,     // null si anonymous=true
        boolean anonymous,
        String role,
        LocalDateTime joinedAt
) {}