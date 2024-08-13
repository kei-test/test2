package GInternational.server.api.controller;

import GInternational.server.api.dto.MonthlyAttendanceDTO;
import GInternational.server.api.dto.NewCheckAttendanceDTO;
import GInternational.server.api.service.NewCheckAttendanceService;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class NewCheckAttendanceController {

    private final NewCheckAttendanceService newCheckAttendanceService;
    private static final Logger logger = LoggerFactory.getLogger(NewCheckAttendanceController.class);


    @PostMapping("/users/new-check-attendance/check/{userId}")
    public ResponseEntity<String> checkAttendance(@PathVariable("userId") @Positive Long userId,
                                                  Authentication authentication,
                                                  HttpServletRequest request) {
        try {
            PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
            newCheckAttendanceService.newAttendanceCheck(userId, principal, request);
            return ResponseEntity.ok("출석체크가 성공적으로 완료되었습니다.");
        } catch (RestControllerException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected server error", e);
            return ResponseEntity.status(500).body("서버 오류가 발생했습니다.");
        }
    }

    @GetMapping("/users/current-month-attendance/{userId}")
    public ResponseEntity<MonthlyAttendanceDTO> getCurrentMonthAttendance(@PathVariable("userId") @Positive Long userId,
                                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        MonthlyAttendanceDTO attendanceDTO = newCheckAttendanceService.getCurrentMonthAttendance(userId, principal);
        return ResponseEntity.ok(attendanceDTO);
    }

    
    @GetMapping("/managers/attendances")
    public ResponseEntity<List<NewCheckAttendanceDTO>> getAllAttendances(
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<NewCheckAttendanceDTO> attendances = newCheckAttendanceService.getAllAttendance(username, nickname, startDate, endDate, principal);
        return ResponseEntity.ok(attendances);
    }
}
