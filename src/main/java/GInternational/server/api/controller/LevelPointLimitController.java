package GInternational.server.api.controller;

import GInternational.server.api.dto.LevelPointLimitDTO;
import GInternational.server.api.service.LevelPointLimitService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/managers/level-point-limit")
@RequiredArgsConstructor
public class LevelPointLimitController {

    private final LevelPointLimitService levelPointLimitService;

    /**
     * 레벨별 포인트 한도 설정을 생성.
     *
     * @param levelPointLimitDTO 생성할 레벨별 포인트 한도 데이터
     * @return 생성된 레벨별 포인트 한도 데이터
     */
    @PostMapping("/create")
    public ResponseEntity<LevelPointLimitDTO> createLevelPointLimit(@RequestBody LevelPointLimitDTO levelPointLimitDTO,
                                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LevelPointLimitDTO createdLevelPointLimit = levelPointLimitService.createLevelPointLimit(levelPointLimitDTO, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLevelPointLimit);
    }

    /**
     * 레벨별 포인트 한도 설정을 업데이트.
     * 값이 있는 필드만 업데이트하고 나머지는 기존 값을 유지.
     *
     * @param levelPointLimitDTO 업데이트할 레벨별 포인트 한도 데이터
     * @return 업데이트된 레벨별 포인트 한도 데이터
     */
    @PutMapping("/update")
    public ResponseEntity<LevelPointLimitDTO> updateLevelPointLimit(@RequestBody LevelPointLimitDTO levelPointLimitDTO,
                                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LevelPointLimitDTO updatedLevelPointLimit = levelPointLimitService.updateLevelPointLimit(levelPointLimitDTO, principal);
        return ResponseEntity.ok(updatedLevelPointLimit);
    }
}
