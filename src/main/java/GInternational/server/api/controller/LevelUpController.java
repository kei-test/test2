package GInternational.server.api.controller;

import GInternational.server.api.entity.LevelUp;
import GInternational.server.api.service.LevelUpService;
import GInternational.server.api.vo.LevelUpTransactionEnum;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class LevelUpController {

    private final LevelUpService levelUpService;

    // 레벨업 신청
    @PostMapping("/users/level-up/apply")
    public ResponseEntity<?> applyLevelUp(Authentication authentication) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            levelUpService.applyLevelUp(principal);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "레벨업 신청이 완료되었습니다."));
        } catch (RestControllerException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage(), "status", HttpStatus.BAD_REQUEST.value()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 내부 오류가 발생했습니다.", "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // 레벨업 신청 승인
    @PutMapping("/managers/level-up/approve/{levelUpId}")
    public ResponseEntity<?> approveLevelUp(@PathVariable Long levelUpId, Authentication authentication) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            levelUpService.approveLevelUp(levelUpId, principal);
            return ResponseEntity.ok(Map.of("message", "레벨업 신청이 승인되었습니다."));
        } catch (RestControllerException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage(), "status", HttpStatus.NOT_FOUND.value()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 내부 오류가 발생했습니다.", "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // 레벨업 신청 취소(거절)
    @PutMapping("/managers/level-up/cancel/{levelUpId}")
    public ResponseEntity<?> cancelLevelUp(@PathVariable Long levelUpId, Authentication authentication) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            levelUpService.cancelLevelUp(levelUpId, principal);
            return ResponseEntity.ok(Map.of("message", "레벨업 신청이 취소되었습니다."));
        } catch (RestControllerException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage(), "status", HttpStatus.NOT_FOUND.value()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 내부 오류가 발생했습니다.", "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    // 상태와 검색 조건을 기반으로 LevelUp 목록 조회
    @GetMapping("/users/level-up/search")
    public ResponseEntity<?> searchLevelUps(
            @RequestParam(value = "status", required = false) LevelUpTransactionEnum status,
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "nickname", required = false) String nickname,
            @RequestParam(value = "memo", required = false) String memo,
            @RequestParam(value = "applyLv", required = false) Integer applyLv,
            Authentication authentication) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            List<LevelUp> levelUps = levelUpService.searchLevelUps(status, username, nickname, memo, applyLv, principal);
            return ResponseEntity.ok(Map.of("data", levelUps));
        } catch (RestControllerException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", ex.getMessage(), "status", HttpStatus.BAD_REQUEST.value()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "서버 내부 오류가 발생했습니다.", "status", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
}
