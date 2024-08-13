package GInternational.server.api.controller;

import GInternational.server.api.entity.AuditLog;
import GInternational.server.api.service.AuditLogService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 왼쪽메뉴 [16] 관리자 관리, 85 활동로그/관리자 활동 로그
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/managers/audit-log")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * 모든 활동 로그를 조회하거나 필터링된 활동 로그를 조회.
     *
     * @param action         활동 제목 (옵션)
     * @param details        처리 내용 (옵션)
     * @param ip             IP 주소 (옵션)
     * @param username       관리자가 활동한 대상의 사용자 이름 (옵션)
     * @param adminUsername  관리자의 사용자 이름 (옵션)
     * @param authentication 인증 정보
     * @return 필터링된 활동 로그 목록을 담은 ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllAuditLogs(@RequestParam(required = false) String action,
                                                          @RequestParam(required = false) String details,
                                                          @RequestParam(required = false) String ip,
                                                          @RequestParam(required = false) String username,
                                                          @RequestParam(required = false) String adminUsername,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AuditLog> logs = auditLogService.getAllAuditLogs(action, details, ip, username, adminUsername, startDate, endDate, principal);
        return ResponseEntity.ok(logs);
    }
}
