package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonExchangeRequestDTO;
import GInternational.server.api.service.AmazonExchangeService;
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
public class AmazonExchangeController {

    private final AmazonExchangeService amazonExchangeService;

    /**
     * 환전 신청.
     *
     * @param userId 사용자 ID
     * @param amazonExchangeRequestDTO 환전 신청 정보
     * @param request HTTP 요청 정보
     * @param authentication 현재 인증된 사용자의 정보
     * @return 응답 엔티티로 환전 신청 완료 메시지와 함께 HTTP 상태 OK 반환
     */
    @PostMapping("/users/{userId}/exchange")
    public ResponseEntity recharge(@PathVariable ("userId") @Positive Long userId,
                                   @RequestBody AmazonExchangeRequestDTO amazonExchangeRequestDTO,
                                   HttpServletRequest request,
                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try{
            amazonExchangeService.exchangeAmazonMoney(userId, amazonExchangeRequestDTO, request, principal);
            return ResponseEntity.ok("환전 신청이 완료되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * 관리자가 환전 신청을 승인. 대기 중인 환전 신청만 승인 가능.
     *
     * @param transactionIds 승인할 거래 ID 목록
     * @param authentication 현재 인증된 사용자의 정보
     * @return 응답 엔티티로 환전 신청 승인 완료 메시지와 함께 HTTP 상태 OK 반환, 대기 상태가 아닌 신청건은 오류 메시지와 함께 BAD_REQUEST 반환
     */
    @PatchMapping("/managers/exchange/approval")
    public ResponseEntity<String> approval(@RequestParam("transactionIds") List<@Positive Long> transactionIds,
                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            amazonExchangeService.updateAmazonMoney(transactionIds, principal);
            return ResponseEntity.ok("환전 신청이 승인되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * 관리자가 사용자의 환전 신청을 거절(취소)
     *
     * @param userId 거절할 환전 신청을 한 사용자의 ID
     * @param transactionIds 거절할 환전 거래의 ID 목록
     * @param authentication 현재 요청을 한 사용자의 인증 정보
     * @return 응답 엔티티로 환전 신청 거절 완료 메시지와 함께 HTTP 상태 OK 반환
     */
    @PatchMapping("/managers/{userId}/exchange/cancel")
    public ResponseEntity cancel(@PathVariable ("userId") @Positive Long userId,
                                 @RequestParam ("transactionId") List<@Positive Long> transactionIds,
                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        amazonExchangeService.cancelTransaction(userId,transactionIds,principal);
        return ResponseEntity.ok("환전 신청이 거절되었습니다.");
    }

    /**
     * 환전 거래의 상태를 대기중으로 변경.
     *
     * @param transactionId 상태를 대기중으로 변경할 환전 거래의 ID
     * @param authentication 현재 요청을 한 사용자의 인증 정보
     * @return 상태 변경 성공시 "신청건이 대기상태로 변경되었습니다." 메시지와 함께 HTTP 상태 OK 반환, 실패시 오류 메시지와 함께 BAD_REQUEST 반환
     */
    @PatchMapping("/managers/exchange/status-to-waiting")
    public ResponseEntity<String> updateStatusToWaiting(@RequestParam("transactionId") @Positive Long transactionId,
                                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            amazonExchangeService.updateTransactionStatusToWaiting(transactionId,principal);
            return ResponseEntity.ok("신청건이 대기상태로 변경되었습니다.");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}
