package GInternational.server.api.controller;

import GInternational.server.api.dto.TradeLogResponseDTO;
import GInternational.server.api.service.TradeLogService;
import GInternational.server.api.vo.TradeLogCategory;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/amazon/api/v2/managers")
@RequiredArgsConstructor
public class TradeLogController {

    private final TradeLogService tradeLogService;

    /**
     * 필터링된 거래 로그를 조회. 카테고리, 역할, 시작/종료 날짜, 사용자 이름을 기준으로 거래 로그를 필터링하여 반환.
     *
     * @param authentication 현재 인증된 사용자의 정보
     * @param category 거래 로그의 카테고리 (선택 사항)
     * @param role 조회하려는 사용자의 역할 (선택 사항)
     * @param startDate 조회 시작 날짜 (선택 사항)
     * @param endDate 조회 종료 날짜 (선택 사항)
     * @param username 조회하려는 사용자의 이름 (선택 사항)
     * @return 필터링된 거래 로그 목록을 담은 ResponseEntity 객체
     */
    @GetMapping("/trade")
    public ResponseEntity<List<TradeLogResponseDTO>> getFilteredTradeLogs(
            Authentication authentication,
            @RequestParam(required = false) TradeLogCategory category,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String username) {

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<TradeLogResponseDTO> response = tradeLogService.getFilteredTradeLogs(principal, category, role, startDate, endDate, username);
        return ResponseEntity.ok(response);
    }
}

