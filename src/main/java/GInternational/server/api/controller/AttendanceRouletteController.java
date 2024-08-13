package GInternational.server.api.controller;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.AttendanceRouletteSettingDTO;
import GInternational.server.api.dto.AttendanceRouletteSpinResultDTO;
import GInternational.server.api.dto.AttendanceRouletteSettingsUpdateAllDTO;
import GInternational.server.api.service.AttendanceRouletteService;
import GInternational.server.api.vo.PaymentStatusEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AttendanceRouletteController {

    private final AttendanceRouletteService attendanceRouletteService;
    private static final Logger logger = LoggerFactory.getLogger(AttendanceRouletteController.class);

    /**
     * 룰렛 설정을 조회.
     * 만약 설정이 존재하지 않을 경우, 초기 설정을 생성하고 해당 설정을 반환.
     *
     * @param userId 조회하고자 하는 사용자의 식별자(ID).
     * @param authentication 현재 인증된 사용자의 세부 정보를 담고 있는 객체.
     * @return 조회된 룰렛 설정 목록을 반환합니다. 설정이 없을 경우 초기화 후 반환.
     *         반환되는 데이터는 AttendanceRouletteSettingDTO 리스트.
     */
    @GetMapping("/managers/attendance_roulette/settings/{userId}")
    public ResponseEntity<List<AttendanceRouletteSettingDTO>> getAllAttendanceRouletteSettings(@PathVariable Long userId,
                                                                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AttendanceRouletteSettingDTO> existingSettings = attendanceRouletteService.getAllAttendanceRouletteSettings(principal);

        if (existingSettings.isEmpty()) {
            attendanceRouletteService.initializeAttendanceRouletteSettings(principal);
            logger.info("사용자 ID: {}에 대한 룰렛 설정 초기화됨.", userId);
            return ResponseEntity.ok(attendanceRouletteService.getAllAttendanceRouletteSettings(principal));
        }

        return ResponseEntity.ok(existingSettings);
    }

    /**
     * 모든 룰렛 스핀 결과를 조회.
     *
     * @return 모든 룰렛 스핀 결과 목록과 함께 HTTP 응답
     */
    @GetMapping("/managers/attendance_roulette/results")
    public ResponseEntity<List<AttendanceRouletteSpinResultDTO>> getAllAttendanceRouletteResults(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AttendanceRouletteSpinResultDTO> results = attendanceRouletteService.getAllAttendanceRouletteSpinResults(principal);
        return ResponseEntity.ok(results);
    }

    /**
     * 상태별로 룰렛 스핀 결과를 조회.
     *
     * @param status            조회하고자 하는 결과의 상태.
     * @param authentication  현재 인증된 사용자의 보안 주체 세부 정보.
     * @return 상태에 일치하는 결과의 DTO 리스트를 ResponseEntity로 감싸서 반환.
     */
    @GetMapping("/managers/attendance_roulette/resultsByStatus")
    public ResponseEntity<?> findResultsByStatus(@RequestParam String status,
                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.fromString(status);
            List<AttendanceRouletteSpinResultDTO> results = attendanceRouletteService.findRouletteResultsByStatus(paymentStatusEnum, principal);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value: " + status);
        }
    }

    /**
     * 룰렛 설정을 업데이트함.
     *
     * @param attendanceRouletteSettingsUpdateAllDTO 업데이트할 설정 정보
     * @param authentication  현재 인증된 사용자의 세부 정보
     * @return 상태 메시지와 함께 응답. 성공 시 설정 업데이트에 따른 메시지 반환.
     *         권한이 없는 사용자가 접근하려 할 경우 "접근 권한이 없습니다." 반환.
     *         그 외의 예외 발생 시 "서버 오류." 반환.
     */
    @PatchMapping("/managers/attendance_roulette/settings/{userId}")
    public ResponseEntity<String> updateAttendanceRouletteSettings(@PathVariable Long userId,
                                                                   @RequestBody AttendanceRouletteSettingsUpdateAllDTO attendanceRouletteSettingsUpdateAllDTO,
                                                                   HttpServletRequest request,
                                                                   Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            if (attendanceRouletteSettingsUpdateAllDTO == null) {
                return new ResponseEntity<>("룰렛 설정 업데이트에 필요한 데이터가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            attendanceRouletteService.updateAllAttendanceRouletteSettings(attendanceRouletteSettingsUpdateAllDTO, request, principal);
            logger.info("사용자 ID: {}에 대한 룰렛 설정이 업데이트 되었습니다.", userId);
            return new ResponseEntity<>("룰렛 설정이 업데이트 되었습니다.", HttpStatus.OK);
        } catch (RestControllerException e) {
            logger.error("사용자 ID: {}에 대한 룰렛 설정 업데이트 중 오류 발생. 오류: {}", userId, e.getMessage());
            if (e.getExceptionCode() == ExceptionCode.UNAUTHORIZED_ACCESS) {
                return new ResponseEntity<>("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
            } else if (e.getExceptionCode() == ExceptionCode.ROULETTE_PROBABILITY_EXCEEDED) {
                return new ResponseEntity<>("총 확률이 100%를 초과합니다.", HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("서버 오류.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * 지급 상태를 업데이트.
     *
     * @param resultId 룰렛 결과의 아이디
     * @param statusString 새로운 지급 상태
     * @param authentication 현재 인증된 사용자의 세부 정보
     * @return 업데이트 결과에 따른 응답
     */
    @PutMapping("/managers/attendance_roulette/updateStatus/{resultId}")
    public ResponseEntity<?> updateAttendanceRouletteStatus(@PathVariable Long resultId,
                                                            @RequestParam String statusString,
                                                            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        logger.info("Received statusString: {}", statusString);
        try {
            PaymentStatusEnum newStatus = PaymentStatusEnum.valueOf(statusString.toUpperCase());
            attendanceRouletteService.updateAttendanceRouletteStatus(resultId, newStatus, principal);
            return ResponseEntity.ok("상태 업데이트 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 상태 값입니다.");
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * 룰렛을 돌림.
     * @param userId 룰렛을 돌릴 사용자의 ID
     * @return 룰렛 결과
     */
    @PostMapping("/users/attendance_roulette/spin/{userId}")
    public ResponseEntity<?> spinRoulette(@PathVariable Long userId,
                                          HttpServletRequest request,
                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            AttendanceRouletteSpinResultDTO result = attendanceRouletteService.spinAttendanceRoulette(userId, request, principal);
            return ResponseEntity.ok(result);
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(e.getExceptionCode().getStatus())
                    .body(e.getExceptionCode().getMessage());
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 내부 오류가 발생했습니다.");
        }
    }
}
