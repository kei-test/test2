package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonDedicatedAccountRequestDTO;
import GInternational.server.api.dto.AmazonDedicatedAccountResponseDTO;
import GInternational.server.api.service.AmazonDedicatedAccountService;
import GInternational.server.common.exception.RestControllerException;

import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/amazon/api/v2/managers/dedicated-account")
@RequiredArgsConstructor
public class AmazonDedicatedAccountController {

    private final AmazonDedicatedAccountService amazonDedicatedAccountService;

    /**
     * 새로운 전용 계좌를 생성.
     *
     * @param requestDTO 전용 계좌 생성을 위한 요청 데이터
     * @param authentication 현재 사용자의 인증 정보
     * @return 생성된 전용 계좌의 정보를 담은 ResponseEntity 객체
     */
    @PostMapping("/")
    public ResponseEntity<?> createDedicatedAccount(@Valid @RequestBody AmazonDedicatedAccountRequestDTO requestDTO,
                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            AmazonDedicatedAccountResponseDTO response = amazonDedicatedAccountService.createDedicatedAccount(requestDTO, principal);
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
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDedicatedAccount(@Valid @PathVariable Long id,
                                                    @RequestBody AmazonDedicatedAccountRequestDTO requestDTO,
                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            AmazonDedicatedAccountResponseDTO response = amazonDedicatedAccountService.updateDedicatedAccount(id, requestDTO, principal);
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
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDedicatedAccount(@PathVariable Long id,
                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            amazonDedicatedAccountService.deleteDedicatedAccount(id, principal);
            return ResponseEntity.ok().build();
        } catch (RestControllerException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }
}
