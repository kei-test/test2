package GInternational.server.api.controller;

import GInternational.server.api.dto.AutoTransactionUpdateDTO;
import GInternational.server.api.dto.AutoChargeUserReqDTO;
import GInternational.server.api.service.AutoChargeService;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class  AutoChargeController {

    private final AutoChargeService autoTransactionService;

//    /**
//     * 사용자로부터 자동 충전 신청을 받아 처리.
//     *
//     * @param userId 자동 충전을 신청하는 사용자의 ID
//     * @param autoChargeReqDTO 자동 충전 신청에 필요한 정보를 담은 DTO
//     * @param authentication 현재 인증된 사용자의 인증 정보
//     * @return 자동 충전 신청 완료 메시지와 함께 HTTP 상태 코드 OK를 반환.
//     */
//    @PostMapping("/users/{userId}/auto")
//    public ResponseEntity autoRecharge(@PathVariable ("userId") Long userId,
//                                       @RequestBody AutoChargeUserReqDTO autoChargeReqDTO,
//                                       HttpServletRequest request,
//                                       Authentication authentication) {
//        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//        autoTransactionService.autoCharge(userId, autoChargeReqDTO, principal, request);
//        return ResponseEntity.ok("자동충전 신청이 완료되었습니다.");
//    }

//    @PatchMapping("/auto/approval")
//    public ResponseEntity updateAutoTransaction(@RequestParam ("transactionId") Long transactionId,
//                                                @RequestHeader("API-Key") String apiKey) {
//        autoTransactionService.autoApproval(transactionId, apiKey);
//        return ResponseEntity.ok("승인 처리되었습니다.");
//    }

    /**
     * 지정된 트랜잭션의 상태를 새로운 상태로 업데이트.
     *
     * @param transactionId 상태를 업데이트할 트랜잭션 ID
     * @param newStatus 적용할 새로운 상태
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 상태 업데이트 성공 메시지와 함께 HTTP 상태 코드 OK 반환
     */
    @PatchMapping("/managers/{transactionId}")
    public ResponseEntity<String> updateTransactionStatus(@PathVariable Long transactionId,
                                                          @RequestParam TransactionEnum newStatus,
                                                          HttpServletRequest request,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        autoTransactionService.updateTransactionStatus(transactionId, newStatus, principal, request);
        return ResponseEntity.ok("트랜잭션 상태가 성공적으로 업데이트되었습니다.");
    }
}
