package GInternational.server.api.controller;

import GInternational.server.api.entity.CompRewardRate;
import GInternational.server.api.entity.RollingRewardRate;
import GInternational.server.api.service.CompRewardRateService;
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
@RequestMapping("/api/v2/managers/comp/reward/rate")
@RequiredArgsConstructor
public class CompRewardRateController {

    private final CompRewardRateService compRewardRateService;

    @PostMapping("/set")
    public ResponseEntity<List<CompRewardRate>> setRates(@RequestBody Map<Integer, BigDecimal> rates, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<CompRewardRate> savedRates = compRewardRateService.setRates(rates, principal);
        return ResponseEntity.ok(savedRates);
    }

    @PutMapping("/update")
    public ResponseEntity<List<CompRewardRate>> updateRates(@RequestBody Map<Integer, BigDecimal> rates, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<CompRewardRate> updatedRates = compRewardRateService.updateRates(rates, principal);
        return ResponseEntity.ok(updatedRates);
    }

    @GetMapping("/all")
    public ResponseEntity<List<CompRewardRate>> getAllRates(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<CompRewardRate> allRates = compRewardRateService.getAllRates(principal);
        return ResponseEntity.ok(allRates);
    }

    @DeleteMapping("/delete/{level}")
    public ResponseEntity<Void> deleteRate(@PathVariable Integer level, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        compRewardRateService.deleteRate(level, principal);
        return ResponseEntity.ok().build();
    }
}
