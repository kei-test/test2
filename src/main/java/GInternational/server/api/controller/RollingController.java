package GInternational.server.api.controller;

import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.RollingResponseDTO;
import GInternational.server.api.service.RollingService;
import GInternational.server.api.dto.RollingTransactionResDTO;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 왼쪽메뉴 [14] 이벤트 관련, 72 슬롯 롤링 이벤트
 */
@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class RollingController {

    private final RollingService rollingService;

    /**
     * 롤링 적립 신청 처리. 사용자가 하루에 한 번만 신청할 수 있으며, 신청 시 바로 승인 처리됨.
     *
     * @param authentication 인증된 사용자의 세부 정보를 포함하는 PrincipalDetails 객체.
     * @return ResponseEntity 롤링 적립 신청의 결과를 담은 RollingResponseDTO를 반환.
     */
    @PostMapping("/users/rolling/apply")
    public ResponseEntity<?> applyForRolling(HttpServletRequest request,
                                             Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        try {
            RollingResponseDTO responseDTO = rollingService.applyForRolling(principal, request);
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", new SingleResponseDto<>(responseDTO));
            responseBody.put("message", "신청되었습니다.");
            return ResponseEntity.ok(responseDTO);
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
