package GInternational.server.api.controller;

import GInternational.server.api.dto.LevelBetSettingReqDTO;
import GInternational.server.api.dto.LevelBetSettingResDTO;
import GInternational.server.api.service.LevelBetSettingService;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class LevelBetSettingController {

    private final LevelBetSettingService levelBetSettingService;

    @PostMapping("/managers/level-bet-setting/set")
    public ResponseEntity<?> createOrUpdateAllLevels(@RequestBody List<LevelBetSettingReqDTO> levelBetSettingReqDTOs,
                                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelBetSettingResDTO> response = levelBetSettingService.createOrUpdateAllLevels(levelBetSettingReqDTOs, principal);
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    @GetMapping("/managers/level-bet-setting/get-all")
    public ResponseEntity<List<LevelBetSettingResDTO>> getAllLevelBetSettings(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelBetSettingResDTO> responseDTOs = levelBetSettingService.getAllLevelBetSettings(principal);
        return new ResponseEntity<>(responseDTOs, HttpStatus.OK);
    }

    @PutMapping("/managers/level-bet-setting/update")
    public ResponseEntity<?> updateSelectedLevels(@RequestBody List<LevelBetSettingReqDTO> levelBetSettingReqDTOs,
                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelBetSettingResDTO> response = levelBetSettingService.updateSelectedLevels(levelBetSettingReqDTOs, principal);
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }
}