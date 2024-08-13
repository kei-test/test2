package GInternational.server.api.controller;

import GInternational.server.api.dto.PasswordChangeRequestDTO;
import GInternational.server.api.entity.PasswordChangeTransaction;
import GInternational.server.api.service.PasswordChangeService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class PasswordChangeController {

    private final PasswordChangeService passwordChangeService;

    /**
     * 사용자 비밀번호 변경 신청 처리
     *
     * @param passwordChangeDTO 변경할 비밀번호 정보 DTO
     * @param httpServletRequest 클라이언트 요청 정보, IP 추출용
     * @param authentication 인증 정보, 현재 사용자 식별용
     * @return 비밀번호 변경 신청 완료 메시지 포함 ResponseEntity
     */
    @PostMapping("/users/change-password")
    public ResponseEntity changePassword(@RequestBody PasswordChangeRequestDTO passwordChangeDTO,
                                         HttpServletRequest httpServletRequest,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        passwordChangeService.applyChangePassword(passwordChangeDTO, principal, httpServletRequest);
        return ResponseEntity.ok().body("비밀번호 변경신청이 완료되었습니다.");
    }

    /**
     * 비밀번호 변경 신청 승인 처리
     *
     * @param transactionId 승인할 비밀번호 변경 신청 ID
     * @param authentication 인증 정보, 승인자 식별용
     * @return 비밀번호 변경 신청 승인 메시지 포함 ResponseEntity
     */
    @PutMapping("/managers/password-change/approve/{transactionId}")
    public ResponseEntity<String> approvePasswordChange(@PathVariable Long transactionId,
                                                        Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        passwordChangeService.approvePasswordChange(transactionId, principal);
        return ResponseEntity.ok("비밀번호 변경 신청이 승인되었습니다.");
    }

    /**
     * 비밀번호 변경 신청 취소 처리
     *
     * @param transactionId 취소할 비밀번호 변경 신청 ID
     * @param authentication 인증 정보, 취소자 식별용
     * @return 비밀번호 변경 신청 취소 메시지 포함 ResponseEntity
     */
    @PutMapping("/managers/password-change/cancel/{transactionId}")
    public ResponseEntity<String> cancelPasswordChange(@PathVariable Long transactionId,
                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        passwordChangeService.cancelPasswordChange(transactionId, principal);
        return ResponseEntity.ok("비밀번호 변경 신청이 취소되었습니다.");
    }

    /**
     * 비밀번호 변경 신청 목록 조회
     *
     * @param status 조회할 신청 상태 (옵션)
     * @param username 조회할 사용자명 (옵션)
     * @param nickname 조회할 닉네임 (옵션)
     * @param startDate 조회할 시작시간 (옵션)
     * @param endDate 조회할 종료시간 (옵션)
     * @return 비밀번호 변경 신청 목록 포함 ResponseEntity
     */
    @GetMapping("/managers/password-change/all")
    public ResponseEntity<List<PasswordChangeTransaction>> getPasswordChangeTransactions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<PasswordChangeTransaction> transactions = passwordChangeService.searchPasswordChangeTransactions(status, username, nickname, startDate, endDate, principal);
        return ResponseEntity.ok(transactions);
    }
}
