package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonRechargeProcessedRequestDTO;
import GInternational.server.api.dto.AmazonRechargeRequestDTO;
import GInternational.server.api.service.AmazonRechargeService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class AmazonRechargeController {


    private final AmazonRechargeService amazonRechargeService;

    /**
     * 충전 신청.
     *
     * @param userId 식별할 사용자의 ID
     * @param walletId 충전될 지갑의 ID
     * @param amazonRechargeRequestDTO 충전 요청에 대한 데이터 전송
     * @param request ip 정보
     * @param authentication 현재 사용자의 인증 정보
     * @return 충전 신청의 성공 여부를 나타내는 ResponseEntity 객체
     */
    @PostMapping("/users/{userId}/{walletId}/recharge")
    public ResponseEntity<?> amazonRecharge(@PathVariable("userId") @Positive Long userId,
                                            @PathVariable ("walletId") @Positive Long walletId,
                                            @RequestBody AmazonRechargeRequestDTO amazonRechargeRequestDTO,
                                            HttpServletRequest request,
                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            amazonRechargeService.rechargeMoney(userId, walletId, amazonRechargeRequestDTO, request, principal);
            return ResponseEntity.ok("충전 신청이 완료되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * 관리자가 사용자의 충전 신청을 승인.
     *
     * @param transactionIds 승인할 충전 거래의 ID 목록
     * @param amazonRechargeProcessedRequestDTO 처리된 충전 요청에 대한 데이터 전송 객체
     * @param authentication 현재 사용자의 인증 정보
     * @return 충전 승인의 성공 여부를 나타내는 ResponseEntity 객체
     */
    @PatchMapping("/managers/approval")
    public ResponseEntity approval(@RequestParam ("transactionIds") List<@Positive Long> transactionIds,
                                   @RequestBody AmazonRechargeProcessedRequestDTO amazonRechargeProcessedRequestDTO,
                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonRechargeService.updateMoney(transactionIds, amazonRechargeProcessedRequestDTO, principal);
        return ResponseEntity.ok("충전 신청이 승인되었습니다.");
    }

    /**
     * 관리자가 사용자의 충전 신청을 거절.
     *
     * @param userId 식별할 사용자의 ID
     * @param transactionIds 취소할 충전 거래의 ID 목록
     * @param authentication 현재 사용자의 인증 정보
     * @return 충전 거절 또는 취소의 성공 여부를 나타내는 ResponseEntity 객체
     */
    @PatchMapping("/managers/{userId}/cancel")
    public ResponseEntity cancel(@PathVariable ("userId") @Positive Long userId,
                                 @RequestParam ("transactionId") List<@Positive Long> transactionIds,
                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonRechargeService.cancelTransaction(userId,transactionIds,principal);
        return ResponseEntity.ok("충전 신청이 거절되었습니다.");
    }

    /**
     * 관리자가 충전 신청의 상태를 '읽지 않음'에서 '대기 중'으로 변경.
     *
     * @param transactionId 상태를 변경할 충전 거래의 ID
     * @param authentication 현재 사용자의 인증 정보
     * @return 상태 변경의 성공 여부를 나타내는 ResponseEntity 객체
     */
    @PatchMapping("/managers/status-to-waiting")
    public ResponseEntity updateStatusToWaiting(@RequestParam("transactionIds") @Positive Long transactionId,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            amazonRechargeService.updateTransactionStatusToWaiting(transactionId,principal);
            return ResponseEntity.ok("신청건이 대기상태로 변경되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
