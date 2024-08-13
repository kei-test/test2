package GInternational.server.api.controller;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.User;
import GInternational.server.api.service.ManagerService;
import GInternational.server.api.service.UserService;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/api/v2")
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
public class ManagerController {

    private final UserService userService;
    private final ManagerService managerService;

    /**
     * 매니저 회원 가입. ROLE_ADMIN만 가능.
     *
     * @param adminRequestDTO 매니저 생성 정보
     * @param authentication 인증 정보
     * @param request HTTP 요청 정보
     * @return 생성된 매니저의 정보
     */
    @PostMapping("/admins/register/manager")
    public ResponseEntity<?> createManager(@Valid @RequestBody AdminRequestDTO adminRequestDTO,
                                           Authentication authentication,
                                           HttpServletRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        UserResponseDTO response = managerService.createManager(adminRequestDTO, principal, request);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.CREATED);
    }

    /**
     * 모든 유저 조회. admin 또는 manager만 가능합니다.
     *
     * @param authentication 인증 정보
     * @return 조회된 유저 목록
     */
    @GetMapping("/managers/users")
    public ResponseEntity<List<UserResponseDTO>> getUsers(
            @RequestParam(required = false) Integer lv,
            @RequestParam(required = false) UserGubunEnum userGubunEnum,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String distributor,
            @RequestParam(required = false) String store,
            Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<User> users = managerService.findAllUser(principal, lv, userGubunEnum, startDate, endDate, username, phone, distributor, store);
        List<UserResponseDTO> usersList = users.stream()
                .map(user -> new UserResponseDTO(user, null, null))
                .collect(Collectors.toList());

        return new ResponseEntity<>(usersList, HttpStatus.OK);
    }

    /**
     * 특정 유저(1명) 조회.
     *
     * @param userId 조회할 유저의 ID
     * @param authentication 인증 정보
     * @return 조회된 유저 정보
     */
    @GetMapping("/managers/users/{userId}")
    public ResponseEntity getUser(@PathVariable("userId") @Positive Long userId,
                                  Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        User response = userService.detailUser(userId,principalDetails);
        return new ResponseEntity<>(new SingleResponseDto<>((response)),HttpStatus.OK);
    }

    /**
     * UserGubun 업데이트 메서드.
     * @param userId 업데이트할 유저의 ID
     * @param roleUpdateDTO 업데이트할 Gubun 정보
     * @param authentication 인증 정보
     */
    @PatchMapping("/managers/gubun/{userId}")
    public void updateUserGubun(@PathVariable ("userId") @Positive Long userId,
                                @RequestBody UserGubunUpdateDTO roleUpdateDTO,
                                HttpServletRequest request,
                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        managerService.updateUserGubun(userId, roleUpdateDTO, principal, request);
    }

    /**
     * 사용자 모니터링 상태 업데이트 메서드.
     *
     * @param userId 업데이트할 사용자의 ID
     * @param monitoringStatusUpdateDTO 사용자 모니터링 상태 업데이트 요청 데이터
     * @param request 클라이언트 요청 정보
     * @param authentication 인증 정보
     * @return ResponseEntity
     */
    @PatchMapping("/managers/monitoring-status/{userId}")
    public ResponseEntity<?> updateMonitoringStatus(@PathVariable("userId") Long userId,
                                                    @Valid @RequestBody MonitoringStatusUpdateDTO monitoringStatusUpdateDTO,
                                                    HttpServletRequest request,
                                                    Authentication authentication) {
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        managerService.updateUserMonitoringStatus(userId, monitoringStatusUpdateDTO, principalDetails, request);
        return ResponseEntity.ok("사용자 모니터링 상태가 성공적으로 업데이트되었습니다.");
    }

    /**
     * 탈퇴 회원 철회 처리.
     *
     * @param userIds 처리할 탈퇴 회원의 ID 목록
     * @param authentication 인증 정보
     * @return 처리 결과
     */
    @PatchMapping("/managers/users/update")
    public ResponseEntity updateUsers(@RequestParam ("userId") List<@Positive Long> userIds,
                                      HttpServletRequest request,
                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        managerService.deleteUpdate(userIds, principal, request);
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    /**
     * 가입 신청한 회원(GUEST)의 ROLE을 "ROLE_GUEST"에서 "ROLE_USER"로 변경.
     *
     * @param userId 등급을 변경할 유저의 ID
     * @param userRoleUpdateDTO 변경할 역할 정보
     * @param authentication 인증 정보
     * @return 처리 결과
     */
    @PatchMapping("/managers/users/{userId}/role")
    public ResponseEntity updateUser(@PathVariable("userId") @Positive Long userId,
                                     @RequestBody UserRoleUpdateDTO userRoleUpdateDTO,
                                     HttpServletRequest request,
                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        UserResponseDTO response = managerService.updateUserRole(userId,userRoleUpdateDTO, principal, request);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    /**
     * 회원 레벨 업데이트.
     *
     * @param userId 업데이트할 유저의 ID
     * @param userLevelUpdateDTO 업데이트할 레벨 정보
     * @param authentication 인증 정보
     * @return 처리 결과
     */
    @PatchMapping("/managers/users/{userId}/level")
    public ResponseEntity<?> updateUserLevel(@PathVariable("userId") @Positive Long userId,
                                             @RequestBody UserLvUpdateDTO userLevelUpdateDTO,
                                             HttpServletRequest request,
                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        UserResponseDTO response = managerService.updateUserLevel(userId, userLevelUpdateDTO, principal, request);
        return ResponseEntity.ok(new SingleResponseDto<>(response));
    }

    /**
     * 회원 종합 정보 조회.
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param authentication 인증 정보
     * @return 조회된 회원 종합 정보
     */
    @GetMapping("/managers/info")
    public ResponseEntity<MultiResponseDto<UserResponseDTO>> usersInfo(@Positive @RequestParam int page,
                                                                       @Positive @RequestParam int size,
                                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<UserResponseDTO> pages = managerService.findUsersInfo(page -1, size, startDate, endDate, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(pages.getContent(), pages), HttpStatus.OK);
    }

    /**
     * 탈퇴 회원 종합 정보 조회.
     *
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param authentication 인증 정보
     * @return 조회된 탈퇴 회원 종합 정보
     */
    @GetMapping("/managers/deletedInfo")
    public ResponseEntity deletedUsers(@RequestParam int page,
                                       @RequestParam int size,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<User> pages = managerService.deletedUsers(page,size, startDate, endDate, principal);
        List<User> users = pages.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>((users),pages),HttpStatus.OK);
    }

    /**
     * 다양한 상태의 트랜잭션, 게시글, 사용자 수 등을 카운트하여 반환
     * @param authentication 현재 인증된 사용자의 상세 정보
     * @return 카운트된 정보를 담은 DTO 객체
     */
    @GetMapping("/managers/main/page/counts")
    public ResponseEntity<MainPageCountDTO> getCountsByStatus(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        MainPageCountDTO counts = managerService.countByStatus(principal);
        return ResponseEntity.ok(counts);
    }
}
