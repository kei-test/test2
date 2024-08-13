package GInternational.server.api.controller;

import GInternational.server.api.dto.*;
import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.service.AdminService;
import GInternational.server.api.vo.UserGubunEnum;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RequestMapping("/api/v2")
@RestController
@Validated
@RequiredArgsConstructor
public class AdminController {


    private final AdminService adminService;

    /**
     * 최상위 관리자 생성.
     *
     * @param adminRequestDTO 관리자 생성 정보
     * @param request HTTP 요청 정보
     * @return 생성된 관리자의 정보
     */
    @PostMapping("/register/admin")
    public ResponseEntity createAdmin(@RequestBody AdminRequestDTO adminRequestDTO,
                                      HttpServletRequest request) {
        UserResponseDTO response = adminService.createAdmin(adminRequestDTO, request);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.CREATED);
    }

    /**
     * 회원 삭제. (Hard Delete)
     *
     * @param userId 삭제할 사용자의 ID
     * @param authentication 인증 정보
     * @return 처리 결과
     */
    @DeleteMapping("/admins/{userId}")
    public ResponseEntity deleteUser(@PathVariable("userId") @Positive Long userId,
                                     HttpServletRequest request,
                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        adminService.deleteUser(userId, principal, request);
        return ResponseEntity.ok(HttpStatus.NO_CONTENT);
    }

    /**
     * 사용자 Soft Delete 처리.
     *
     * @param userId 사용자 ID
     * @param authentication 인증 정보
     * @return 처리 결과
     */
    @PatchMapping("/admins/{userId}/delete")
    public ResponseEntity deleteUser(@PathVariable("userId") @Positive Long userId,
                                     @RequestParam UserGubunEnum userGubunEnum,
                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        UserResponseDTO response = adminService.updateIsDeleted(userId, userGubunEnum, principal);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    /**
     * 사용자 Soft Delete 복구 처리.
     *
     * @param userId 사용자 ID
     * @param authentication 인증 정보
     * @return 처리 결과
     */
    @PatchMapping("/admins/{userId}/recover")
    public ResponseEntity recoverUser(@PathVariable("userId") @Positive Long userId,
                                      Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        UserResponseDTO response = adminService.recoverUser(userId, principal);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    /**
     * 회원 정보 수정.
     *
     * @param userId 수정할 사용자의 ID
     * @param userRequestDTO 수정할 사용자 정보
     * @param authentication 인증 정보
     * @return 수정된 사용자의 정보
     */
    @PatchMapping("/admins/users/{userId}")
    public ResponseEntity updateUser(@PathVariable("userId") @Positive Long userId,
                                     @RequestBody UserRequestDTO userRequestDTO,
                                     HttpServletRequest request,
                                     Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        UserResponseDTO response = adminService.updateUser(userId, userRequestDTO, principal, request);
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    @PostMapping("/admins/admin-change-password")
    public ResponseEntity<String> changePassword(@RequestBody AdminPasswordChangeReqDTO adminPasswordChangeReqDTO,
                                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            adminService.adminPasswordChange(adminPasswordChangeReqDTO, principal);
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (RestControllerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 레벨별 사용자 관리를 위한 통계 정보 조회.
     *
     * @param level 조회할 사용자 레벨
     * @param authentication 인증 정보
     * @return 레벨별 사용자 수 통계 정보
     */
    @GetMapping("/admins/lv/list")
    public ResponseEntity<Map<String, Map<UserGubunEnum, Long>>> getLvCountList(@RequestParam int level,
                                                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Map<String, Map<UserGubunEnum, Long>> list = adminService.getLvCountList(level, principal);
        return ResponseEntity.ok(list);
    }

    /**
     * 지정된 기간 동안의 사용자 계산 정보 조회.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param authentication 인증 정보
     * @return 조회된 사용자 계산 정보 리스트
     */
    @GetMapping("/admins/lv/calculate")
    public ResponseEntity getUserCalculate(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                           @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                           Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<UserCalculateDTO> response = adminService.searchCalculate(startDate,endDate, principal);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    // 관리자 목록 조회
    @GetMapping("/admins/all-admins")
    public ResponseEntity<List<AdminListResDTO>> getAllAdmins(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AdminListResDTO> admins = adminService.getAllAdmins(principal);
        return ResponseEntity.ok(admins);
    }

    // 관리자 상세 정보 업데이트
    @PatchMapping("/admins/update-admins/{userId}")
    public ResponseEntity<AdminListResDTO> updateAdminDetails(@PathVariable Long userId,
                                                              @RequestBody AdminListUpdateDTO adminListUpdateDTO,
                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        AdminListResDTO updatedAdmin = adminService.updateAdminDetails(userId, adminListUpdateDTO, principal);
        return ResponseEntity.ok(updatedAdmin);
    }
}
