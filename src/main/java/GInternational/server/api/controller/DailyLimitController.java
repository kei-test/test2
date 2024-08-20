package GInternational.server.api.controller;

import GInternational.server.api.dto.DailyLimitDTO;
import GInternational.server.api.service.DailyLimitService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class DailyLimitController {

    /**
     *  125 사이트 설정 - 게시판 관리
     */

    private final DailyLimitService dailyLimitService;

    @PostMapping("/managers/daily-limit/create")
    public ResponseEntity<DailyLimitDTO> createDailyLimit(@RequestBody DailyLimitDTO dailyLimitDTO,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        DailyLimitDTO createdDailyLimit = dailyLimitService.createDailyLimit(dailyLimitDTO, principal);
        return ResponseEntity.ok(createdDailyLimit);
    }

    @GetMapping("/managers/daily-limit/get")
    public ResponseEntity<List<DailyLimitDTO>> getAllDailyLimits(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<DailyLimitDTO> dailyLimits = dailyLimitService.getAllDailyLimits(principal);
        return ResponseEntity.ok(dailyLimits);
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
