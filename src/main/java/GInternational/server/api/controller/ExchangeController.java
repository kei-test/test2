package GInternational.server.api.controller;

import GInternational.server.api.dto.ExchangeRequestDTO;

import GInternational.server.api.service.ExchangeService;
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
public class ExchangeController {

    private final ExchangeService exchangeService;

    /**
     * 사용자가 환전을 신청.
     *
     * @param userId 환전을 신청하는 사용자의 ID
     * @param exchangeRequestDTO 환전 신청에 필요한 정보를 담은 DTO
     * @param request HttpServletRequest 객체, 클라이언트의 요청 정보를 담고 있음
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 환전 신청 완료 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PostMapping("/users/{userId}/exchange")
    public ResponseEntity exchange(@PathVariable ("userId") @Positive Long userId,
                                   @RequestBody ExchangeRequestDTO exchangeRequestDTO,
                                   HttpServletRequest request,
                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        exchangeService.exchangeSportsBalance(userId, request, exchangeRequestDTO, principal);
        return ResponseEntity.ok("환전신청이 완료되었습니다.");
    }

    /**
     * 관리자가 환전 신청을 승인.
     *
     * @param transactionIds 승인할 환전 신청의 트랜잭션 ID 목록
     * @param request HttpServletRequest 객체, 클라이언트의 요청 정보를 담고 있음
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 승인 처리 완료 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PatchMapping("/managers/exchange/approval")
    public ResponseEntity approval(@RequestParam ("transactionIds") List<@Positive Long> transactionIds,
                                   HttpServletRequest request,
                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        exchangeService.updateExchangeSportsBalance(request, transactionIds, principal);
        return ResponseEntity.ok("승인 처리되었습니다.");
    }

    /**
     * 관리자가 환전 신청을 거절(취소).
     *
     * @param userId 거절(취소)할 환전 신청을 한 사용자의 ID
     * @param transactionIds 거절(취소)할 환전 신청의 트랜잭션 ID 목록
     * @param request HttpServletRequest 객체, 클라이언트의 요청 정보를 담고 있음
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 취소 처리 완료 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PatchMapping("/managers/{userId}/exchange/cancel")
    public ResponseEntity cancel(@PathVariable ("userId") @Positive Long userId,
                                 @RequestParam ("transactionId") List<@Positive Long> transactionIds,
                                 HttpServletRequest request,
                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        exchangeService.cancelExchangeTransaction(request, userId, transactionIds, principal);
        return ResponseEntity.ok("취소 처리되었습니다.");
    }

    /**
     * 관리자가 환전 신청의 상태를 대기중으로 변경.
     *
     * @param transactionId 상태를 변경할 환전 신청의 트랜잭션 ID
     * @param request HttpServletRequest 객체, 클라이언트의 요청 정보를 담고 있음
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 상태 변경 완료 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PatchMapping("/managers/exchange/status-to-waiting")
    public ResponseEntity updateStatusToWaiting(@RequestParam("transactionId") @Positive Long transactionId,
                                                HttpServletRequest request,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            exchangeService.updateExchangeTransactionStatusToWaiting(request, transactionId, principal);
            return ResponseEntity.ok("상태값이 변경되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
