package GInternational.server.api.controller;

import GInternational.server.api.dto.DailyLimitDTO;
import GInternational.server.api.service.DailyLimitService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class DailyLimitController {

    private final DailyLimitService dailyLimitService;

    @PostMapping("/managers/daily-limit/create")
    public ResponseEntity<DailyLimitDTO> createDailyLimit(@RequestBody DailyLimitDTO dailyLimitDTO,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        DailyLimitDTO createdDailyLimit = dailyLimitService.createDailyLimit(dailyLimitDTO, principal);
        return ResponseEntity.ok(createdDailyLimit);
    }

    @PutMapping("/managers/daily-limit/update/{id}")
    public ResponseEntity<DailyLimitDTO> updateDailyLimit(@PathVariable("id") @Positive Long id,
                                                          @RequestBody DailyLimitDTO dailyLimitDTO,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        DailyLimitDTO updatedDailyLimit = dailyLimitService.updateDailyLimit(id, dailyLimitDTO, principal);
        return ResponseEntity.ok(updatedDailyLimit);
    }
}
