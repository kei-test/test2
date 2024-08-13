package GInternational.server.api.controller;

import GInternational.server.api.dto.DailyLimitDTO;
import GInternational.server.api.dto.JoinPointDTO;
import GInternational.server.api.service.JoinPointService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class JoinPointController {

    private final JoinPointService joinPointService;

    @PostMapping("/managers/join-point/create")
    public ResponseEntity<JoinPointDTO> createDailyLimit(@RequestBody JoinPointDTO joinPointDTO,
                                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        JoinPointDTO createdJoinPoint = joinPointService.createJoinPoint(joinPointDTO, principal);
        return ResponseEntity.ok(createdJoinPoint);
    }

    @PutMapping("/managers/join-point/update/{id}")
    public ResponseEntity<JoinPointDTO> updateDailyLimit(@PathVariable("id") @Positive Long id,
                                                          @RequestBody JoinPointDTO joinPointDTO,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        JoinPointDTO updatedJoinPoint = joinPointService.updateJoinPoint(id, joinPointDTO, principal);
        return ResponseEntity.ok(updatedJoinPoint);
    }
}
