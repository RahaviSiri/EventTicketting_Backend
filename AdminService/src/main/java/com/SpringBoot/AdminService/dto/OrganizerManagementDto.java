package com.SpringBoot.AdminService.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class OrganizerManagementDto {
    private Long id;
    private String name;
    private String email;
    private Long activeEventsCount;
    private Long pendingEventsCount;

    public OrganizerManagementDto(Long id, String name, String email, Long activeEventsCount, Long pendingEventsCount) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.activeEventsCount = activeEventsCount;
        this.pendingEventsCount = pendingEventsCount;
    }
}
