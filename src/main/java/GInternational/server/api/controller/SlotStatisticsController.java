package GInternational.server.api.controller;

import GInternational.server.api.dto.UserSlotBetWinDTO;
import GInternational.server.api.service.SlotStatisticsService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/v2/managers/slot-stats")
@RequiredArgsConstructor
public class SlotStatisticsController {

    private final SlotStatisticsService slotStatisticsService;

    /**
     * 지정된 시작일과 종료일 사이의 사용자 슬롯 게임 통계 조회.
     *
     * @param startDate 조회할 기간의 시작일
     * @param endDate 조회할 기간의 종료일
     * @param authentication 인증된 사용자의 정보
     * @return ResponseEntity<List<UserSlotBetWinDTO>> 조회된 사용자 슬롯 게임 통계 목록
     */
    @GetMapping
    public ResponseEntity<List<UserSlotBetWinDTO>> getSlotStatistics(@RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                     @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<UserSlotBetWinDTO> stats = slotStatisticsService.calculateUserSlotStatistics(startDate.atStartOfDay(), endDate.plusDays(1).atStartOfDay(), principal);
        return ResponseEntity.ok(stats);
    }
}
