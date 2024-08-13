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
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/amazon/api/v2/managers/login-history")
public class AmazonLoginHistoryController {

    private final AmazonLoginHistoryService amazonLoginHistoryService;

    /**
     * 모든 로그인 이력 조회.
     *
     * @param authentication 현재 인증된 사용자 정보
     * @return 로그인 이력 목록
     */
    @GetMapping("/all")
    public List<AmazonLoginHistoryDTO> getAllAmazonLoginHistory(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return amazonLoginHistoryService.getAllAmazonLoginHistory(principal);
    }

    /**
     * username을 기준으로 로그인 이력 조회.
     *
     * @param username 사용자명
     * @param principalDetails 현재 인증된 사용자 정보
     * @return 로그인 이력 목록
     */
    @GetMapping("/username")
    public List<AmazonLoginHistoryDTO> getAmazonLoginHistoryByUsername(@RequestParam String username,
                                                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return amazonLoginHistoryService.getAmazonLoginHistoryByUsername(username, principalDetails);
    }

    /**
     * nickname을 기준으로 로그인 이력 조회.
     *
     * @param nickname 닉네임
     * @param principalDetails 현재 인증된 사용자 정보
     * @return 로그인 이력 목록
     */
    @GetMapping("/nickname")
    public List<AmazonLoginHistoryDTO> getAmazonLoginHistoryByNickname(@RequestParam String nickname,
                                                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return amazonLoginHistoryService.getAmazonLoginHistoryByNickname(nickname, principalDetails);
    }

    /**
     * 특정 날짜 범위의 로그인 이력 조회.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param principalDetails 현재 인증된 사용자 정보
     * @return 로그인 이력 목록
     */
    @GetMapping("/range")
    public List<AmazonLoginHistoryDTO> getAmazonLoginHistoryByDateRange(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                        @AuthenticationPrincipal PrincipalDetails principalDetails) {
        return amazonLoginHistoryService.getAmazonLoginHistoryByDateRange(startDate, endDate, principalDetails);
    }
}
