package com.SpringBoot.AdminService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.SpringBoot.AdminService.dto.UserDTO;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Map;

@FeignClient(name = "UserService")
public interface UserClient {

    @GetMapping("/api/users/summary")
    Map<String, Object> getOrganizersSummary(@RequestParam("range") String range);

    @GetMapping("/api/users/monthly-signups")
    List<Map<String, Object>> getSignupsLast6Months();

    @GetMapping("/api/users/organizers")
    Page<UserDTO> getAllOrganizers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size);

    @GetMapping("/api/users/{id}/username-and-email")
    UserDTO getUsernameAndEmailById(@PathVariable Long id);
}
