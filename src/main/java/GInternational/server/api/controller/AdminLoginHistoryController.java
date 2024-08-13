package GInternational.server.api.controller;

import GInternational.server.api.dto.AdminLoginResultDTO;
import GInternational.server.api.dto.AdminLoginSuccessRecordDTO;
import GInternational.server.api.service.AdminLoginHistoryService;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.vo.AmazonUserStatusEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 왼쪽메뉴 [16] 관리자 관리, 84 접속로그/관리자 접속 로그
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class AdminLoginHistoryController {

    private final AdminLoginHistoryService adminLoginHistoryService;

    /**
     * 모든 관리자의 로그인 시도 이력을 조회.
     *
     * @param authentication 인증 객체
     * @return ResponseEntity 모든 관리자의 로그인 이력 목록
     */
    @GetMapping("/admins/history/all")
    public ResponseEntity<List<AdminLoginResultDTO>> getAllLoginHistories(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AdminLoginResultDTO> histories = adminLoginHistoryService.getAllLoginHistories(principal);
        return ResponseEntity.ok(histories);
    }

    /**
     * 로그인 성공 이력만 조회.
     *
     * @param authentication 인증 객체
     * @param username       사용자 이름 (선택적)
     * @param attemptIp      로그인 시도 IP (선택적)
     * @param countryCode    국가 코드 (선택적)
     * @return ResponseEntity 로그인 성공 이력 목록
     */
    @GetMapping("/admins/history/success")
    public ResponseEntity<List<AdminLoginSuccessRecordDTO>> getLoginSuccessRecords(
            Authentication authentication,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String attemptIp,
            @RequestParam(required = false) String countryCode) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AdminLoginSuccessRecordDTO> successRecords = adminLoginHistoryService.getLoginSuccessRecords(username, attemptIp, countryCode, principal);
        return ResponseEntity.ok(successRecords);
    }

    /**
     * 특정 관리자의 로그인 카운트를 0으로 초기화.
     *
     * @param userId 초기화할 관리자의 사용자 ID
     * @param authentication 인증 객체
     * @return ResponseEntity 초기화 결과 메시지
     */
    @PostMapping("/admins/history/reset/count/{userId}")
    public ResponseEntity<String> resetVisitCount(@PathVariable Long userId,
                                                  HttpServletRequest request,
                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        adminLoginHistoryService.resetVisitCount(userId, principal, request);
        return ResponseEntity.ok("user ID: " + userId + " 에 의해 로그인 카운트가 0으로 초기화 되었습니다.");
    }

    /**
     * 특정 사용자의 Amazon 사용자 상태값 변경.
     *
     * @param userId 상태를 변경할 사용자의 ID
     * @param requestBody 새로운 상태값
     * @param authentication 인증 객체
     * @return ResponseEntity 상태 변경 결과 메시지
     */
    @PostMapping("/admins/history/update/status/{userId}")
    public ResponseEntity<String> updateAmazonUserStatus(@PathVariable Long userId,
                                                         @RequestBody Map<String, String> requestBody,
                                                         HttpServletRequest request,
                                                         Authentication authentication) {
        String statusString = requestBody.get("newStatus");
        AmazonUserStatusEnum newStatus;
        try {
            newStatus = AmazonUserStatusEnum.valueOf(statusString);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value");
        }

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        adminLoginHistoryService.updateAmazonUserStatus(userId, newStatus, principal, request);
        return ResponseEntity.ok("user ID: " + userId + " 에 의해 상태값이 성공적으로 변경되었습니다.");
    }

    /**
     * 특정 관리자의 비밀번호를 변경.
     *
     * @param userId 비밀번호를 변경할 관리자의 사용자 ID
     * @param newPassword 새 비밀번호
     * @param authentication 인증 객체
     * @return ResponseEntity 비밀번호 변경 결과 메시지
     */
    @PutMapping("/admins/history/update/password/{userId}")
    public ResponseEntity<String> updateAmazonUserPassword(@PathVariable Long userId,
                                                           @RequestBody String newPassword,
                                                           HttpServletRequest request,
                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        adminLoginHistoryService.updateAmazonUserPassword(userId, newPassword, principal, request);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    /**
     * 관리자의 접속 가능 IP 수정 API
     *
     * @param userId 관리자의 사용자 ID
     * @param newApproveIp 새로운 접속 가능 IP 주소
     * @param authentication 인증 정보
     * @return ResponseEntity with status
     */
    @PatchMapping("/admins/update-approve-ip/{userId}")
    public ResponseEntity<?> updateApproveIp(@PathVariable Long userId,
                                             @RequestParam String newApproveIp,
                                             HttpServletRequest request,
                                             Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        adminLoginHistoryService.updateApproveIp(userId, newApproveIp, principalDetails, request);
        return ResponseEntity.ok().body("접속가능 ip가 업데이트 되었습니다.");
    }

    /**
     * ROLE_ADMIN 또는 ROLE_MANAGER 역할을 가진 사용자들 중 접속 가능 IP가 설정된 사용자들의 목록 조회
     *
     * @param authentication 인증 정보
     * @return 사용자의 접속 가능 IP, 사용자 이름, IP 업데이트 날짜 포함한 리스트
     */
    @GetMapping("/admins/approve-ips")
    public ResponseEntity<List<Map<String, Object>>> getAllApproveIp(Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        List<Map<String, Object>> approveIps = adminLoginHistoryService.getAllApproveIp(principalDetails);
        return ResponseEntity.ok().body(approveIps);
    }

    /**
     * 관리자의 접속 가능 IP 주소를 삭제.
     *
     * @param userId 삭제할 접속 가능 IP의 소유자인 관리자의 사용자 ID.
     * @param request 현재 HTTP 요청 정보를 담고 있는 HttpServletRequest 객체.
     * @param authentication 현재 인증 세션의 인증 정보를 담고 있는 Authentication 객체.
     * @return ResponseEntity 객체, 성공 시 "접속가능 ip가 삭제되었습니다." 메시지를 담아 반환.
     */
    @DeleteMapping("/admins/delete-approve-ip/{userId}")
    public ResponseEntity<?> deleteApproveIp(@PathVariable Long userId,
                                             HttpServletRequest request,
                                             Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        adminLoginHistoryService.deleteApproveIp(userId, principalDetails, request);
        return ResponseEntity.ok().body("접속가능 ip가 삭제되었습니다.");
    }
}
