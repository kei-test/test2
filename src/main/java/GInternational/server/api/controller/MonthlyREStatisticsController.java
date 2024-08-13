//package GInternational.server.api.controller;
//
//import GInternational.server.api.dto.DailyREStatisticsDTO;
//import GInternational.server.api.dto.MonthlyREStatisticsDTO;
//import GInternational.server.api.service.MonthlyREStatisticsService;
//import GInternational.server.security.auth.PrincipalDetails;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//
///**
// * 왼쪽메뉴 3-1 날짜별 충환 목록
// */
//@RestController
//@RequestMapping("/api/v2/managers")
//@RequiredArgsConstructor
//public class MonthlyREStatisticsController {
//
//    private final MonthlyREStatisticsService monthlyREStatisticsService;
//
//    /**
//     * 지정된 월과 연도에 대한 월별 통계를 조회.
//     *
//     * @param month 조회할 월
//     * @param year 조회할 연도
//     * @param authentication 인증 객체, 현재 인증된 사용자의 정보를 포함.
//     * @return ResponseEntity<MonthlyREStatisticsDTO> 월별 부동산 통계 정보를 담은 DTO
//     */
//    @GetMapping("/statistics/monthly")
//    public ResponseEntity<MonthlyREStatisticsDTO> getMonthlyStatistics(@RequestParam int month,
//                                                                       @RequestParam int year,
//                                                                       Authentication authentication) {
//        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//        MonthlyREStatisticsDTO statistics = monthlyREStatisticsService.calculateMonthlyStatistics(month, year, principal);
//        return ResponseEntity.ok(statistics);
//    }
//
//    /**
//     * 지정된 월과 연도에 대한 일별 통계를 조회.
//     *
//     * @param month 조회할 월
//     * @param year 조회할 연도
//     * @param authentication 인증 객체, 현재 인증된 사용자의 정보를 포함합니다.
//     * @return ResponseEntity<Map<Integer, DailyREStatisticsDTO>> 일별 부동산 통계 정보를 담은 DTO의 맵
//     *         맵의 키는 일자이며, 값은 해당 일자의 통계 정보.
//     */
//    @GetMapping("/statistics/daily")
//    public ResponseEntity<Map<Integer, DailyREStatisticsDTO>> getDailyStatistics(@RequestParam int month,
//                                                                                 @RequestParam int year,
//                                                                                 Authentication authentication) {
//        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
//        Map<Integer, DailyREStatisticsDTO> dailyStatistics = monthlyREStatisticsService.calculateDailyStatistics(month, year, principal);
//        return ResponseEntity.ok(dailyStatistics);
//    }
//}
