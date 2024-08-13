package GInternational.server.api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class RollingDateUtils {

    public static LocalDateTime getStartOfYesterday(String zoneId) {
        LocalDate yesterday = LocalDate.now(ZoneId.of(zoneId)).minusDays(1);
        return yesterday.atStartOfDay();
    }

    public static LocalDateTime getEndOfYesterday(String zoneId) {
        LocalDate yesterday = LocalDate.now(ZoneId.of(zoneId)).minusDays(1);
        return yesterday.atTime(23, 59, 59);
    }

//    /**
//     * .minusDays(1)를 제외하여 오늘날짜 기준으로 조회
//     */
//    public static LocalDateTime getStartOfYesterday(String zoneId) {
//        LocalDate yesterday = LocalDate.now(ZoneId.of(zoneId));
//        return yesterday.atStartOfDay();
//    }
//
//    /**
//     * .minusDays(1)를 제외하여 오늘날짜 기준으로 조회
//     */
//    public static LocalDateTime getEndOfYesterday(String zoneId) {
//        LocalDate yesterday = LocalDate.now(ZoneId.of(zoneId));
//        return yesterday.atTime(23, 59, 59);
//    }
}
