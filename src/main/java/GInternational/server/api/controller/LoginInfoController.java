package GInternational.server.api.controller;

import GInternational.server.api.dto.LoginInfoResponseDTO;
import GInternational.server.api.dto.UserLoginCountDTO;
import GInternational.server.api.service.LoginInfoService;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/managers/login-info")
public class LoginInfoController {

    private final LoginInfoService loginInfoService;

    @GetMapping("/filter")
    public ResponseEntity<List<LoginInfoResponseDTO>> findByCriteria(
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> nickname,
            @RequestParam Optional<String> accessedIp,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        // LocalDate를 LocalDateTime으로 변환
        Optional<LocalDateTime> startDateTime = Optional.ofNullable(startDate).map(sd -> sd.atStartOfDay());
        Optional<LocalDateTime> endDateTime = Optional.ofNullable(endDate).map(ed -> ed.atTime(23, 59, 59, 999999999));

        // 변환된 LocalDateTime 객체를 사용하여 서비스 메소드 호출
        List<LoginInfoResponseDTO> responseDTOS = loginInfoService.findByCriteria(username, nickname, accessedIp, startDateTime, endDateTime, principal);
        return ResponseEntity.ok(responseDTOS);
    }

    /**
     * 날짜 범위 내에서 각 유저의 로그인 횟수를 반환.
     *
     * @param startDate 조회를 시작할 날짜. 선택적으로 제공될 수 있음.
     * @param endDate 조회를 종료할 날짜. 선택적으로 제공될 수 있음.
     * @return 날짜 범위 내에서 각 유저의 로그인 횟수를 담은 {@link UserLoginCountDTO} 리스트.
     */
    @GetMapping("/user-login-counts")
    public ResponseEntity<List<UserLoginCountDTO>> getUserLoginCountsByDateRange(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                                                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<UserLoginCountDTO> userLoginCounts = loginInfoService.getUserLoginCountsByDateRange(Optional.ofNullable(startDate), Optional.ofNullable(endDate), principal);
        return ResponseEntity.ok(userLoginCounts);
    }
}
