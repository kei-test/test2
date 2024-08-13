package GInternational.server.api.controller;

import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.api.dto.PointRequestDTO;
import GInternational.server.api.dto.PointResponseDTO;
import GInternational.server.api.service.PointService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    /**
     * 사용자가 포인트를 스포츠 머니로 교환하는 요청을 처리.
     *
     * @param userId 포인트를 교환할 사용자 ID
     * @param walletId 교환할 지갑 ID
     * @param pointRequestDTO 포인트 교환 요청 데이터
     * @param request 클라이언트의 요청 정보
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 교환 후의 스포츠 머니 잔액 정보와 함께 HTTP 상태 코드 OK 반환
     */
    @PostMapping("/users/{userId}/{walletId}/point")
    public ResponseEntity point(@PathVariable ("userId") @Positive Long userId,
                                @PathVariable ("walletId") @Positive Long walletId,
                                @RequestBody PointRequestDTO pointRequestDTO,
                                HttpServletRequest request,
                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        PointResponseDTO response = pointService.exchangedPoint(userId,walletId,pointRequestDTO,request,principal);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "포인트가 스포츠머니로 교환되었습니다.");
        return new ResponseEntity<>(new SingleResponseDto<>(response),HttpStatus.OK);
    }
}
