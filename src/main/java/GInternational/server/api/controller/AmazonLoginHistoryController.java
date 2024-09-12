package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonLoginHistoryDTO;
import GInternational.server.api.service.AmazonLoginHistoryService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/managers/amazon-login-history")
public class AmazonLoginHistoryController {

    private final AmazonLoginHistoryService amazonLoginHistoryService;

    /**
     * 모든 로그인 이력 조회.
     *
     * @param authentication 현재 인증된 사용자 정보
     * @param startDate 시작일
     * @param endDate 종료일
     * @param username (선택적) 사용자명
     * @param nickname (선택적) 닉네임
     * @return 로그인 이력 목록
     */
    @GetMapping("/all")
    public List<AmazonLoginHistoryDTO> getAllAmazonLoginHistory(
            Authentication authentication,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return amazonLoginHistoryService.getAllAmazonLoginHistory(principal, startDateTime, endDateTime, username, nickname);
    }
}
