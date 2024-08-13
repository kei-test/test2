package GInternational.server.api.controller;

import GInternational.server.api.dto.LevelBonusPointSettingReqDTO;
import GInternational.server.api.dto.LevelBonusPointSettingResDTO;
import GInternational.server.api.service.LevelBonusPointSettingService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/managers/level-bonus-setting")
@RequiredArgsConstructor
public class LevelBonusPointSettingController {

    private final LevelBonusPointSettingService levelBonusPointSettingService;

    // 레벨별 설정 일괄 생성
    @PostMapping("/create")
    public ResponseEntity<List<LevelBonusPointSettingResDTO>> createBulkSettings(@RequestBody List<LevelBonusPointSettingReqDTO> reqDTOs,
                                                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelBonusPointSettingResDTO> resDTOs = levelBonusPointSettingService.createBulkSettingsForLevels(reqDTOs, principal);
        return ResponseEntity.ok(resDTOs);
    }

    // 레벨별 설정 일괄 업데이트
    @PostMapping("/update")
    public ResponseEntity<List<LevelBonusPointSettingResDTO>> updateBulkSettings(@RequestBody List<LevelBonusPointSettingReqDTO> reqDTOs,
                                                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelBonusPointSettingResDTO> resDTOs = levelBonusPointSettingService.updateBulkSettingsForLevels(reqDTOs, principal);
        return ResponseEntity.ok(resDTOs);
    }

    // 모든 레벨 설정 조회
    @GetMapping("/all")
    public ResponseEntity<List<LevelBonusPointSettingResDTO>> findAllSettings(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelBonusPointSettingResDTO> resDTOs = levelBonusPointSettingService.findAllSettings(principal);
        return ResponseEntity.ok(resDTOs);
    }

    @PostMapping("/update-bonus-activation")
    public ResponseEntity<List<LevelBonusPointSettingResDTO>> updateBonusActivation(@RequestBody List<Long> levelIds, Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelBonusPointSettingResDTO> resDTOs = levelBonusPointSettingService.updateBonusActivation(levelIds, principal);
        return ResponseEntity.ok(resDTOs);
    }
}
