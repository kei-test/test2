package GInternational.server.api.controller;

import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.DedicatedAccountRequestDTO;
import GInternational.server.api.dto.DedicatedAccountResponseDTO;
import GInternational.server.api.service.DedicatedAccountService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class DedicatedAccountController {

    private final DedicatedAccountService dedicatedAccountService;

    /**
     * 새로운 전용 계좌를 생성.
     *
     * @param requestDTO 전용 계좌 생성을 위한 요청 데이터
     * @param authentication 현재 사용자의 인증 정보
     * @return 생성된 전용 계좌의 정보를 담은 ResponseEntity 객체
     */
    @PostMapping("/managers/dedicated-account")
    public ResponseEntity<?> createDedicatedAccount(@Valid @RequestBody DedicatedAccountRequestDTO requestDTO,
                                                    HttpServletRequest request,
                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            DedicatedAccountResponseDTO response = dedicatedAccountService.createDedicatedAccount(requestDTO, principal, request);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", new SingleResponseDto<>(response));
            responseBody.put("message", "전용계좌가 생성되었습니다.");
            return ResponseEntity.ok(response);
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    /**
     * 기존 전용 계좌 정보를 업데이트.
     *
     * @param id 업데이트할 전용 계좌의 ID
     * @param requestDTO 전용 계좌 업데이트를 위한 요청 데이터
     * @param authentication 현재 사용자의 인증 정보
     * @return 업데이트된 전용 계좌의 정보를 담은 ResponseEntity 객체
     */
    @PutMapping("/managers/dedicated-account/{id}")
    public ResponseEntity<?> updateDedicatedAccount(@Valid @PathVariable Long id,
                                                    @RequestBody DedicatedAccountRequestDTO requestDTO,
                                                    HttpServletRequest request,
                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            DedicatedAccountResponseDTO response = dedicatedAccountService.updateDedicatedAccount(id, requestDTO, principal, request);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", new SingleResponseDto<>(response));
            responseBody.put("message", "전용계좌 정보가 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * 특정 전용 계좌을 삭제.
     *
     * @param id 삭제할 전용 계좌의 ID
     * @param authentication 현재 사용자의 인증 정보
     * @return 성공적으로 처리된 경우 빈 ResponseEntity 객체
     */
    @DeleteMapping("/managers/dedicated-account/{id}")
    public ResponseEntity<?> deleteDedicatedAccount(@PathVariable Long id,
                                                    HttpServletRequest request,
                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            dedicatedAccountService.deleteDedicatedAccount(id, principal, request);
            return ResponseEntity.ok().build();
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * 사용자 레벨에 따른 활성화된 전용 계좌 목록을 가져옴.
     *
     * @param lv 사용자 레벨
     * @return 해당 레벨에 활성화된 전용 계좌들의 목록을 담은 ResponseEntity 객체
     */
    @GetMapping("/users/dedicated-account/user-level/{lv}")
    public ResponseEntity<?> getActiveDedicatedAccountsForUserLevel(@PathVariable int lv,
                                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            List<DedicatedAccountResponseDTO> response = dedicatedAccountService.getActiveDedicatedAccountsForUserLevel(lv, principal);
            return ResponseEntity.ok(response);
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    /**
     * 특정 전용 계좌의 활성화 상태를 설정.
     * @param id 활성화 상태를 설정할 전용 계좌의 ID
     * @param isActive 활성화 여부
     * @param authentication 현재 사용자의 인증 정보
     * @return 활성화 상태가 변경된 전용 계좌의 정보를 담은 ResponseEntity 객체
     */
    @PatchMapping("/managers/dedicated-account/set-active/{id}")
    public ResponseEntity<?> setActive(@PathVariable Long id,
                                       @RequestParam boolean isActive,
                                       HttpServletRequest request,
                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            DedicatedAccountResponseDTO response = dedicatedAccountService.setActive(id, isActive, principal, request);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", new SingleResponseDto<>(response));
            responseBody.put("message", "활성상태가 변경되었습니다.");
            return ResponseEntity.ok(response);
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
