package GInternational.server.api.controller;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.RouletteSettingDTO;
import GInternational.server.api.dto.RouletteSpinResultDTO;
import GInternational.server.api.dto.RouletteSettingsUpdateAllDTO;
import GInternational.server.api.service.RouletteService;
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
public class RouletteController {

    private final RouletteService rouletteService;
    private static final Logger logger = LoggerFactory.getLogger(RouletteController.class);


    /**
     * 특정 사용자의 룰렛 결과를 조회하는 엔드포인트.
     *
     * @param userId 사용자 ID
     * @param authentication 현재 인증된 사용자의 보안 주체 세부 정보.
     * @return 특정 사용자의 룰렛 결과 리스트를 ResponseEntity로 감싸서 반환.
     */
    @GetMapping("/users/{userId}/roulette/results")
    public ResponseEntity<?> findResultsByUser(@PathVariable Long userId,
                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            List<RouletteSpinResultDTO> results = rouletteService.findRouletteResultsByUser(userId, principal);
            return ResponseEntity.ok(results);
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }



    /**
     * 특정 사용자의 모든 룰렛 설정을 조회하는 엔드포인트.
     * 만약 설정이 존재하지 않을 경우, 초기 설정을 생성하고 해당 설정을 반환.
     *
     * @param userId 조회하고자 하는 사용자의 식별자(ID).
     * @param authentication 현재 인증된 사용자의 세부 정보를 담고 있는 객체.
     * @return 조회된 룰렛 설정 목록을 반환. 설정이 없을 경우 초기화 후 반환.
     *         반환되는 데이터는 RouletteSettingDTO 리스트.
     */
    @GetMapping("/users/roulette/settings/{userId}")
    public ResponseEntity<List<RouletteSettingDTO>> getAllRouletteSettings(@PathVariable Long userId,
                                                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<RouletteSettingDTO> existingSettings = rouletteService.getAllRouletteSettings(principal);

        if (existingSettings.isEmpty()) {
            rouletteService.initializeRouletteSettings(principal);
            existingSettings = rouletteService.getAllRouletteSettings(principal);
            logger.info("사용자 ID: {}에 대한 룰렛 초기값이 설정되었습니다 .", userId);
        }

        return ResponseEntity.ok(existingSettings);
    }

    /**
     * 모든 룰렛 스핀 결과를 조회하는 엔드포인트.
     *
     * @return 모든 룰렛 스핀 결과 목록과 함께 HTTP 응답
     */
    @GetMapping("/managers/roulette/results")
    public ResponseEntity<List<RouletteSpinResultDTO>> getAllRouletteResults(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<RouletteSpinResultDTO> results = rouletteService.getAllRouletteSpinResults(principal);
        return ResponseEntity.ok(results);
    }

    /**
     * 상태별로 룰렛 스핀 결과를 조회.
     *
     * @param status 조회하고자 하는 결과의 상태 (PaymentStatus enum 타입, 쿼리 파라미터로 전달됨)
     * @param authentication 현재 인증된 사용자의 보안 주체 세부 정보.
     * @return 상태에 일치하는 결과의 DTO 리스트를 ResponseEntity로 감싸서 반환.
     */
    @GetMapping("/managers/roulette/resultsByStatus")
    public ResponseEntity<?> findResultsByStatus(@RequestParam String status,
                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            PaymentStatusEnum paymentStatusEnum = PaymentStatusEnum.fromString(status);
            List<RouletteSpinResultDTO> results = rouletteService.findRouletteResultsByStatus(paymentStatusEnum, principal);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid status value: " + status);
        }
    }

    /**
     * 룰렛 설정을 업데이트.
     *
     * @param userId 사용자의 ID로, 업데이트할 룰렛 설정의 소유자를 식별.
     * @param rouletteSettingsUpdateAllDTO 업데이트할 룰렛 설정 데이터를 담고 있는 DTO 객체.
     * @param authentication 현재 인증된 사용자의 세부 정보가 포함된 객체.
     * @return 업데이트 성공 시 HTTP 상태 코드 200(OK)과 함께 성공 메시지를 반환.
     *         요청 데이터 누락, 인증 실패 또는 기타 서버 오류 발생 시 HTTP 상태 코드와 오류 메시지를 반환.
     */
    @PatchMapping("/managers/roulette/settings/{userId}")
    public ResponseEntity<String> updateRouletteSettings(@PathVariable Long userId,
                                                         @RequestBody RouletteSettingsUpdateAllDTO rouletteSettingsUpdateAllDTO,
                                                         HttpServletRequest request,
                                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            if (rouletteSettingsUpdateAllDTO == null || rouletteSettingsUpdateAllDTO.getSettings().isEmpty()) {
                return new ResponseEntity<>("룰렛 설정 업데이트에 필요한 데이터가 없습니다.", HttpStatus.BAD_REQUEST);
            }
            rouletteService.updateAllRouletteSettings(rouletteSettingsUpdateAllDTO, principal, request);
            logger.info("사용자 ID: {}에 대한 룰렛 설정 업데이트됨.", userId);
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
     * 룰렛을 돌림.
     * @param userId 룰렛을 돌릴 사용자의 ID
     * @return 룰렛 결과
     */
    @PostMapping("/users/roulette/spin/{userId}")
    public ResponseEntity<?> spinRoulette(@PathVariable Long userId,
                                          HttpServletRequest request,
                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            RouletteSpinResultDTO result = rouletteService.spinRoulette(userId, request, principal);
            return ResponseEntity.ok(result);
        } catch (RestControllerException e) {
            logger.error("사용자 ID: {}에 대한 룰렛 스핀 중 오류 발생. 오류: {}", userId, e.getMessage(), e);  // 전체 예외 스택 트레이스를 로그에 출력
            if (e.getExceptionCode() == ExceptionCode.ROULETTE_ALREADY_SPUN) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("룰렛을 이미 돌렸습니다.");
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("룰렛을 이미 돌렸습니다.");
        } catch (Exception e) {
            logger.error("사용자 ID: {}에 대한 룰렛 스핀 중 예기치 않은 오류 발생. 오류: {}", userId, e.getMessage(), e);  // 일반 예외에 대한 전체 예외 스택 트레이스를 로그에 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }


    /**
     * 지급 상태를 업데이트하는 메서드.
     *
     * @param resultId 룰렛 결과의 아이디
     * @param statusString 새로운 지급 상태를 나타내는 문자열 (PaymentStatus enum으로 변환됨)
     * @param authentication 현재 인증된 사용자의 세부 정보
     * @return 업데이트 성공 시 "상태 업데이트 성공" 메시지와 함께 OK 응답을 반환하고,
     *         잘못된 상태 값이 입력된 경우 "잘못된 상태 값입니다." 메시지와 함께 Bad Request 응답을 반환합니다.
     *         예외가 발생한 경우, 적절한 HTTP 상태 코드와 오류 메시지를 반환합니다.
     */
    @PutMapping("/managers/roulette/updateStatus/{resultId}")
    public ResponseEntity<?> updateRouletteStatus(@PathVariable Long resultId,
                                                  @RequestParam String statusString,
                                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        logger.info("Received statusString: {}", statusString);
        try {
            PaymentStatusEnum newStatus = PaymentStatusEnum.valueOf(statusString.toUpperCase());
            rouletteService.updateRouletteStatus(resultId, newStatus, principal);
            return ResponseEntity.ok("상태 업데이트 성공");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("잘못된 상태 값입니다.");
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
