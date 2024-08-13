package GInternational.server.api.controller;


import GInternational.server.api.dto.AmazonMoneyRequestDTO;
import GInternational.server.api.entity.AmazonMoney;
import GInternational.server.api.service.AmazonMoneyService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class AmazonMoneyController {

    private final AmazonMoneyService amazonMoneyService;

    /**
     * 머니 지급.
     *
     * @param userId 대상 사용자 ID
     * @param requestDTO 지급할 머니 정보
     * @param authentication 인증 정보
     * @return 머니 지급 성공 메시지
     */
    @PatchMapping("/managers/money/{userId}")
    public ResponseEntity<?> amazonMoneysToUser(@PathVariable Long userId,
                                                @RequestBody AmazonMoneyRequestDTO requestDTO,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonMoneyService.addAmazonMoneysToUser(userId, requestDTO, principal);
        return ResponseEntity.ok("머니가 지급 되었습니다.");
    }

    /**
     * 머니 차감.
     *
     * @param userId 대상 사용자 ID
     * @param requestDTO 차감할 머니 정보
     * @param authentication 인증 정보
     * @return 머니 차감 성공 메시지
     */
    @PatchMapping("/managers/money/subtract/{userId}")
    public ResponseEntity<?> subtractAmazonMoneysFromUser(@PathVariable Long userId,
                                                          @RequestBody AmazonMoneyRequestDTO requestDTO,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonMoneyService.subtractAmazonMoneysFromUser(userId, requestDTO, principal);
        return ResponseEntity.ok("머니가 차감되었습니다.");
    }

    /**
     * 전액 회수.
     *
     * @param userId 대상 사용자 ID
     * @param authentication 인증 정보
     * @return 전액 회수 성공 메시지
     */
    @PatchMapping("/managers/money/reclaim/{userId}")
    public ResponseEntity<?> reclaimAllAmazonMoneysFromUser(@PathVariable Long userId,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonMoneyService.reclaimAllAmazonMoneys(userId, principal);
        return ResponseEntity.ok("모든 머니가 회수되었습니다.");
    }

    /**
     * 전체 조회.
     *
     * @param authentication 인증 정보
     * @return 모든 아마존 머니 거래 내역
     */
    @GetMapping("/managers/money")
    public ResponseEntity<List<AmazonMoney>> getAllMoneys(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmazonMoney> moneys = amazonMoneyService.findAllAmazonMoneys(principal);
        return ResponseEntity.ok(moneys);
    }

    /**
     * 사용자별 조회.
     *
     * @param userId 대상 사용자 ID
     * @param authentication 인증 정보
     * @return 해당 사용자의 아마존 머니 거래 내역
     */
    @GetMapping("/managers/money/user/{userId}")
    public ResponseEntity<List<AmazonMoney>> getAmazonMoneysByUserId(@PathVariable Long userId,
                                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmazonMoney> moneys = amazonMoneyService.findAmazonMoneysByUserId(userId, principal);
        return ResponseEntity.ok(moneys);
    }

    /**
     * 사용자별 날짜 범위 조회.
     *
     * @param userId 대상 사용자 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param authentication 인증 정보
     * @return 지정된 날짜 범위 내 해당 사용자의 아마존 머니 거래 내역
     */
    @GetMapping("/managers/money/user/{userId}/range")
    public ResponseEntity<List<AmazonMoney>> getAmazonMoneysByUserIdAndDateRange(@PathVariable Long userId,
                                                                                 @RequestParam LocalDate startDate,
                                                                                 @RequestParam LocalDate endDate,
                                                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AmazonMoney> moneys = amazonMoneyService.findAmazonMoneysByUserIdAndDateRange(userId, startDateTime, endDateTime, principal);
        return ResponseEntity.ok(moneys);
    }

    /**
     * 날짜 범위 조회.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param authentication 인증 정보
     * @return 지정된 날짜 범위 내 모든 아마존 머니 거래 내역
     */
    @GetMapping("/managers/money/range")
    public ResponseEntity<List<AmazonMoney>> getAllAmazonMoneysByDateRange(@RequestParam LocalDate startDate,
                                                                           @RequestParam LocalDate endDate,
                                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AmazonMoney> moneys = amazonMoneyService.findAllAmazonMoneysByDateRange(startDateTime, endDateTime, principal);
        return ResponseEntity.ok(moneys);
    }
}
