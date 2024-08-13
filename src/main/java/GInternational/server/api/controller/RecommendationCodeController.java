package GInternational.server.api.controller;

import GInternational.server.api.dto.RecommendationCodeResDTO;
import GInternational.server.api.entity.User;
import GInternational.server.api.service.UserRecommendationCodeService;
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
public class RecommendationCodeController {

    private final UserRecommendationCodeService recommendationCodeService;

    // 추천인 코드 발급
    @PostMapping("/managers/recommendation-code/insert")
    public ResponseEntity<?> assignRecommendationCodes(@RequestParam String usernames,
                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            recommendationCodeService.assignRecommendationCodes(usernames, principal);
            return ResponseEntity.ok("추천 코드가 성공적으로 발급되었습니다.");
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 추천인 코드 조회
    @GetMapping("/managers/recommendation-code/all")
    public ResponseEntity<?> getRecommendationCodeInfo(@RequestParam(required = false) Long userId,
                                                       @RequestParam(required = false) String username,
                                                       @RequestParam(required = false) String nickname,
                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            List<RecommendationCodeResDTO> recommendationCodeInfos = recommendationCodeService.findUsersByCriteria(userId, username, nickname, principal);
            return ResponseEntity.ok(recommendationCodeInfos);
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // 추천인 코드 삭제
    @DeleteMapping("/managers/recommendation-code/delete/{userId}")
    public ResponseEntity<?> deleteRecommendationCode(@PathVariable Long userId,
                                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            recommendationCodeService.deleteRecommendationCode(userId, principal);
            return ResponseEntity.ok("추천코드가 삭제되었습니다.");
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
