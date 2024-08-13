package GInternational.server.api.controller;

import GInternational.server.api.dto.MonthlyBetStatisticMonthlyTotalsDTO;
import GInternational.server.api.service.MonthlyBetStatisticService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v2/managers")
@RequiredArgsConstructor
public class MonthlyBetStatisticController {

    private final MonthlyBetStatisticService monthlyService;

    /**
     * 지정된 연도와 월에 대한 월별 총계 데이터를 조회.
     * 인증된 사용자의 권한을 확인한 후, 해당 기간의 금융 및 활동 통계를 계산하여 반환.
     *
     * @param year 조회하고자 하는 연도
     * @param month 조회하고자 하는 월
     * @param authentication 스프링 시큐리티의 인증 객체, 현재 인증된 사용자의 정보를 포함.
     * @return ResponseEntity<MonthlyBetStatisticMonthlyTotalsDTO> 월별 총계 데이터가 담긴 DTO를 포함한 응답 엔티티
     */
    @GetMapping("/monthly-totals/{year}/{month}")
    public ResponseEntity<MonthlyBetStatisticMonthlyTotalsDTO> getMonthlyTotals(@PathVariable int year,
                                                                                @PathVariable int month,
                                                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        MonthlyBetStatisticMonthlyTotalsDTO monthlyTotals = monthlyService.calculateMonthlyTotals(month, year, principal);
        return ResponseEntity.ok(monthlyTotals);
    }
}
