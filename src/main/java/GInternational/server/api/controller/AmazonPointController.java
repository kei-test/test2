package GInternational.server.api.controller;


import GInternational.server.api.dto.AmazonPointRequestDTO;
import GInternational.server.api.entity.AmazonPoint;
import GInternational.server.api.service.AmazonPointService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class AmazonPointController {

    private final AmazonPointService amazonPointService;

    /**
     * 포인트 지급.
     *
     * @param userId 사용자 ID
     * @param walletId 지갑 ID
     * @param requestDTO 포인트 지급 요청 정보
     * @param request ip 정보
     * @param authentication 인증 정보
     * @return 포인트 지급 완료 메시지
     */
    @PatchMapping("/managers/point/{userId}/{walletId}")
    public ResponseEntity<?> amazonPointsToUser(@PathVariable("userId") @Positive Long userId,
                                                @PathVariable ("walletId") @Positive Long walletId,
                                                @RequestBody AmazonPointRequestDTO requestDTO,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonPointService.addAmazonPointsToUser(userId, walletId, requestDTO, principal);
        return ResponseEntity.ok("포인트가 지급 되었습니다.");
    }

    /**
     * 포인트 차감.
     *
     * @param userId 사용자 ID
     * @param walletId 지갑 ID
     * @param requestDTO 포인트 차감 요청 정보
     * @param request ip 정보
     * @param authentication 인증 정보
     * @return 포인트 차감 완료 메시지
     */
    @PatchMapping("/managers/point/subtract/{userId}/{walletId}")
    public ResponseEntity<?> subtractAmazonPointsFromUser(@PathVariable("userId") @Positive Long userId,
                                                          @PathVariable("walletId") @Positive Long walletId,
                                                          @RequestBody AmazonPointRequestDTO requestDTO,
                                                          HttpServletRequest request,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonPointService.subtractAmazonPointsFromUser(userId, walletId, requestDTO, request, principal);
        return ResponseEntity.ok("포인트가 차감되었습니다.");
    }

    /**
     * 전액 회수.
     *
     * @param userId 사용자 ID
     * @param walletId 지갑 ID
     * @param request ip 정보
     * @param authentication 인증 정보
     * @return 포인트 전액 회수 완료 메시지
     */
    @PatchMapping("/managers/point/reclaim/{userId}/{walletId}")
    public ResponseEntity<?> reclaimAllAmazonPointsFromUser(@PathVariable("userId") @Positive Long userId,
                                                            @PathVariable ("walletId") @Positive Long walletId,
                                                            HttpServletRequest request,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonPointService.reclaimAllAmazonPoints(userId, walletId, request, principal);
        return ResponseEntity.ok("모든 포인트가 회수되었습니다.");
    }

    /**
     * 전체 조회.
     *
     * @param authentication 인증 정보
     * @return 모든 아마존 포인트
     */
    @GetMapping("/managers/point")
    public ResponseEntity<List<AmazonPoint>> getAllAmazonPoints(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmazonPoint> amazonPoints = amazonPointService.findAllAmazonPoints(principal);
        return ResponseEntity.ok(amazonPoints);
    }

    /**
     * 사용자별 조회.
     *
     * @param userId 사용자 ID
     * @param authentication 인증 정보
     * @return 해당 사용자의 아마존 포인트
     */
    @GetMapping("/managers/point/user/{userId}")
    public ResponseEntity<List<AmazonPoint>> getPointsByUserId(@PathVariable Long userId,
                                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmazonPoint> amazonPoints = amazonPointService.findAmazonPointsByUserId(userId, principal);
        return ResponseEntity.ok(amazonPoints);
    }

    /**
     * 날짜 범위 조회.
     *
     * @param userId 사용자 ID
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param authentication 인증 정보
     * @return 지정된 날짜 범위 내 해당 사용자의 아마존 포인트
     */
    @GetMapping("/managers/point/user/{userId}/range")
    public ResponseEntity<List<AmazonPoint>> getAmazonPointsByUserIdAndDateRange(@PathVariable Long userId,
                                                                                 @RequestParam LocalDate startDate,
                                                                                 @RequestParam LocalDate endDate,
                                                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AmazonPoint> amazonPoints = amazonPointService.findAmazonPointsByUserIdAndDateRange(userId, startDateTime, endDateTime, principal);
        return ResponseEntity.ok(amazonPoints);
    }

    /**
     * 날짜 범위 전체 조회.
     *
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param authentication 인증 정보
     * @return 지정된 날짜 범위 내 모든 아마존 포인트
     */
    @GetMapping("/managers/point/range")
    public ResponseEntity<List<AmazonPoint>> getAllAmazonPointsByDateRange(@RequestParam LocalDate startDate,
                                                                           @RequestParam LocalDate endDate,
                                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AmazonPoint> amazonPoints = amazonPointService.findAllAmazonPointsByDateRange(startDateTime, endDateTime, principal);
        return ResponseEntity.ok(amazonPoints);
    }
}
