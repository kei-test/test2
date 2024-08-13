package GInternational.server.api.controller;

import GInternational.server.api.dto.LevelAccountSettingReqDTO;
import GInternational.server.api.dto.LevelAccountSettingResDTO;
import GInternational.server.api.entity.LevelAccountSetting;
import GInternational.server.api.service.LevelAccountSettingService;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
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
public class LevelAccountSettingController {

    private final LevelAccountSettingService levelAccountSettingService;

    // 레벨별 설정 추가
    @PostMapping("/managers/level/account/settings/create")
    public ResponseEntity<List<LevelAccountSettingResDTO>> createLevelAccountSettings(@RequestBody List<LevelAccountSettingReqDTO> reqDTOs,
                                                                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelAccountSettingResDTO> settings = levelAccountSettingService.createLevelAccountSettings(reqDTOs, principal);
        return ResponseEntity.ok(settings);
    }

    // 레벨별 설정 업데이트
    @PutMapping("/managers/level/account/settings/update")
    public ResponseEntity<List<LevelAccountSettingResDTO>> updateLevelAccountSettings(@RequestBody List<LevelAccountSettingReqDTO> reqDTOs,
                                                                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelAccountSettingResDTO> settings = levelAccountSettingService.updateLevelAccountSettings(reqDTOs, principal);
        return ResponseEntity.ok(settings);
    }

    // 모든 레벨 설정 조회
    @GetMapping("/managers/level/account/settings/all")
    public ResponseEntity<List<LevelAccountSettingResDTO>> getAllLevelAccountSettings(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LevelAccountSettingResDTO> settings = levelAccountSettingService.getAllLevelAccountSettings(principal);
        return ResponseEntity.ok(settings);
    }

    // 레벨별 설정 조회
    @GetMapping("/users/level/account/settings/{lv}")
    public ResponseEntity<?> getLevelAccountSettingByLevel(@PathVariable int lv, Authentication authentication) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            LevelAccountSettingResDTO setting = levelAccountSettingService.getLevelAccountSettingByLevel(lv, principal);
            return ResponseEntity.ok(setting);
        } catch (RestControllerException ex) {
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // Default error status
            if (ex.getExceptionCode() == ExceptionCode.DATA_NOT_FOUND) {
                status = HttpStatus.NOT_FOUND;
                return ResponseEntity.status(status).body("해당 레벨의 계좌 설정이 존재하지 않습니다.");
            } else if (ex.getExceptionCode() == ExceptionCode.PERMISSION_DENIED) {
                status = HttpStatus.FORBIDDEN;
                return ResponseEntity.status(status).body("고객센터로 문의해주세요.");
            }
            return ResponseEntity.status(status).body("내부 서버 오류가 발생하였습니다.");
        }
    }
}
