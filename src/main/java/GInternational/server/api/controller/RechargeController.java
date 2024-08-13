package GInternational.server.api.controller;

import GInternational.server.api.dto.RechargeRequestDTO;
import GInternational.server.api.service.RechargeService;
import GInternational.server.api.dto.RechargeProcessedRequestDTO;
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
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class RechargeController {

    private final RechargeService rechargeService;

    /**
     * 사용자가 스포츠 머니를 충전하기 위해 충전 신청을 하는 엔드포인트입니다.
     *
     * @param userId 충전을 신청하는 사용자의 ID
     * @param walletId 충전될 지갑의 ID
     * @param rechargeRequestDTO 충전 신청에 대한 정보를 담은 DTO
     * @param request 클라이언트의 요청 정보를 담은 HttpServletRequest
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 충전 신청 완료 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PostMapping("/users/{userId}/{walletId}/recharge")
    public ResponseEntity recharge(@PathVariable ("userId") @Positive Long userId,
                                   @PathVariable ("walletId") @Positive Long walletId,
                                   @RequestBody RechargeRequestDTO rechargeRequestDTO,
                                   HttpServletRequest request,
                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        rechargeService.rechargeSportsBalance(userId, walletId, rechargeRequestDTO, principal, request);
        return ResponseEntity.ok("충전 신청이 완료되었습니다.");
    }

    /**
     * 관리자가 충전 신청에 대한 승인 처리.
     *
     * @param transactionIds 승인할 충전 신청의 트랜잭션 ID 목록
     * @param rechargeProcessedRequestDTO 승인 처리에 필요한 추가 정보를 담은 DTO
     * @param request 클라이언트의 요청 정보를 담은 HttpServletRequest
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 승인 처리 완료 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PatchMapping("/managers/approval")
    public ResponseEntity approval(@RequestParam ("transactionIds") List<@Positive Long> transactionIds,
                                   @RequestBody RechargeProcessedRequestDTO rechargeProcessedRequestDTO, HttpServletRequest request,
                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        rechargeService.updateRechargeSportsBalance(request, transactionIds, rechargeProcessedRequestDTO, principal);
        return ResponseEntity.ok("승인 처리가 완료되었습니다.");
    }

//    @PatchMapping("/auto/approval")
//    public ResponseEntity updateAutoTransaction(@RequestParam ("transactionId") Long transactionId,
//                                                @RequestHeader("API-Key") String apiKey,
//                                                @RequestBody String message) {
//        rechargeService.autoApproval(transactionId, apiKey, message);
//        return ResponseEntity.ok("승인 처리되었습니다.");
//    }

    /**
     * 관리자가 충전 신청을 거절(취소).
     *
     * @param userId 거절 처리할 사용자의 ID
     * @param transactionIds 거절할 충전 신청의 트랜잭션 ID 목록
     * @param request 클라이언트의 요청 정보를 담은 HttpServletRequest
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 거절 처리 완료 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PatchMapping("/managers/{userId}/cancel")
    public ResponseEntity cancel(@PathVariable ("userId") @Positive Long userId,
                                 @RequestParam ("transactionId") List<@Positive Long> transactionIds,
                                 HttpServletRequest request,
                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        rechargeService.cancelRechargeTransaction(request, userId, transactionIds, principal);
        return ResponseEntity.ok("거절 처리가 완료되었습니다.");
    }

    /**
     * 관리자가 충전 신청의 상태를 대기중으로 변경.
     *
     * @param transactionId 상태를 대기중으로 변경할 충전 신청의 트랜잭션 ID
     * @param request 클라이언트의 요청 정보를 담은 HttpServletRequest
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 상태값 변경 완료 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PatchMapping("/managers/status-to-waiting")
    public ResponseEntity updateStatusToWaiting(@RequestParam("transactionId") @Positive Long transactionId,
                                                HttpServletRequest request,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            rechargeService.updateRechargeTransactionStatusToWaiting(request, transactionId, principal);
            return ResponseEntity.ok("상태값 변경이 완료되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
