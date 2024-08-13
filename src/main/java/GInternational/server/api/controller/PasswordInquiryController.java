package GInternational.server.api.controller;

import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.PasswordInquiryRequestDTO;
import GInternational.server.api.dto.PasswordInquiryResponseDTO;
import GInternational.server.api.dto.PasswordInquiryUpdateStatusDTO;
import GInternational.server.api.service.PasswordInquiryService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 왼쪽메뉴 [2] 회원관리, 16 비밀번호 문의
 */
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class PasswordInquiryController {

    private final PasswordInquiryService passwordInquiryService;

    /**
     * 클라이언트로부터 받은 비밀번호 문의 요청을 처리하여 저장.
     *
     * @param requestDTO 비밀번호 문의에 필요한 정보를 담고 있는 DTO
     * @param request 클라이언트의 HTTP 요청 정보
     * @return ResponseEntity 비밀번호 문의 처리 결과를 담은 DTO와 HTTP 상태 코드
     */
    @PostMapping("/inquire")
    public ResponseEntity<?> createInquiry(@RequestBody PasswordInquiryRequestDTO requestDTO,
                                           HttpServletRequest request) {
        try {
            PasswordInquiryResponseDTO responseDTO = passwordInquiryService.passwordInquiry(requestDTO, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (RestControllerException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    /**
     * 모든 비밀번호 문의를 조회.
     *
     * @param authentication 인증된 사용자의 정보
     * @return ResponseEntity 비밀번호 문의 목록과 HTTP 상태 코드
     */
    @GetMapping("/managers")
    public ResponseEntity<List<PasswordInquiryResponseDTO>> getAllInquiries(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<PasswordInquiryResponseDTO> responseDTOs = passwordInquiryService.getAllPasswordInquiries(principal);
        return ResponseEntity.ok(responseDTOs);
    }

    /**
     * 관리자가 특정 비밀번호 문의의 상태와 메모 업데이트.
     *
     * @param inquiryId 업데이트할 비밀번호 문의의 ID
     * @param updateRequest 상태와 메모를 업데이트하기 위한 정보를 담고 있는 DTO
     * @param authentication 인증된 사용자의 정보
     * @return ResponseEntity 업데이트된 비밀번호 문의 정보와 HTTP 상태 코드
     */
    @PutMapping("/managers/{inquiryId}/update")
    public ResponseEntity<PasswordInquiryResponseDTO> updateInquiry(@PathVariable Long inquiryId,
                                                                    @RequestBody PasswordInquiryUpdateStatusDTO updateRequest,
                                                                    HttpServletRequest request,
                                                                    Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PasswordInquiryResponseDTO responseDTO = passwordInquiryService.updateInquiryStatusAndMemo(inquiryId, updateRequest.getAdminMemo(), principal, request);
        return ResponseEntity.ok(responseDTO);
    }
}
