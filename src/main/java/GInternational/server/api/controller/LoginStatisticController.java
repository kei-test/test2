package GInternational.server.api.controller;

import GInternational.server.api.dto.LoginStatisticDTO;

import GInternational.server.api.service.LoginStatisticService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/managers/statistics")
public class LoginStatisticController {

    private final LoginStatisticService loginStatisticService;

    /**
     * 지정된 날짜 범위의 모든 통계 데이터 조회.
     *
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param authentication 스프링 시큐리티의 Authentication 객체
     * @return ResponseEntity<List<LoginStatisticDTO>> 조회된 통계 데이터 목록
     */
    @GetMapping("/all")
    public ResponseEntity<List<LoginStatisticDTO>> getAllStatisticsForDateRange(@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                                @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<LoginStatisticDTO> allStatistics = loginStatisticService.getAllStatisticsForDateRange(startDate, endDate, principal);
        return ResponseEntity.ok(allStatistics);
    }
}
