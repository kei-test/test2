package GInternational.server.api.controller;

import GInternational.server.api.dto.AddPointRequestDTO;
import GInternational.server.api.service.AddPointService;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AddPointController {

    private final AddPointService addPointService;

    /**
     * 사용자에게 포인트를 추가하는 메서드.
     * @param userId 포인트를 추가할 사용자의 ID
     * @param requestDTO 포인트 추가 요청 정보
     * @param authentication 현재 사용자의 인증 정보
     * @return 포인트 추가 성공 메시지
     */
    @PatchMapping("/managers/add-points/{userId}/{walletId}")
    public ResponseEntity<?> modifyUserPoints(@PathVariable("userId") @Positive Long userId,
                                              @PathVariable("walletId") @Positive Long walletId,
                                              @RequestBody AddPointRequestDTO requestDTO,
                                              HttpServletRequest request,
                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            addPointService.modifyPoints(userId, walletId, requestDTO, request, principal);
            String operationMessage = requestDTO.getOperation().equals("지급") ? "지급" : "차감";
            return ResponseEntity.ok("포인트가 성공적으로 " + operationMessage + "되었습니다.");
        } catch (RestControllerException e) {
            if (e.getExceptionCode() == ExceptionCode.USER_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            } else if (e.getExceptionCode() == ExceptionCode.WALLET_NOT_FOUND) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생");
        }
    }
}
