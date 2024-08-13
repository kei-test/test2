package GInternational.server.api.controller;

import GInternational.server.api.entity.RollingRewardRate;
import GInternational.server.api.service.RollingRewardRateService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2/managers/rolling/reward/rate")
@RequiredArgsConstructor
public class RollingRewardRateController {

    private final RollingRewardRateService rollingRewardRateService;

    @PostMapping("/set")
    public ResponseEntity<List<RollingRewardRate>> setRates(@RequestBody Map<Integer, BigDecimal> rates, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<RollingRewardRate> savedRates = rollingRewardRateService.setRates(rates, principal);
        return ResponseEntity.ok(savedRates);
    }

    @PutMapping("/update")
    public ResponseEntity<List<RollingRewardRate>> updateRates(@RequestBody Map<Integer, BigDecimal> rates, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<RollingRewardRate> updatedRates = rollingRewardRateService.updateRates(rates, principal);
        return ResponseEntity.ok(updatedRates);
    }

    @GetMapping("/all")
    public ResponseEntity<List<RollingRewardRate>> getAllRates(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<RollingRewardRate> allRates = rollingRewardRateService.getAllRates(principal);
        return ResponseEntity.ok(allRates);
    }

    @DeleteMapping("/delete/{level}")
    public ResponseEntity<Void> deleteRate(@PathVariable Integer level, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        rollingRewardRateService.deleteRate(level, principal);
        return ResponseEntity.ok().build();
    }
}
