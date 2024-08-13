package GInternational.server.api.controller;

import GInternational.server.common.dto.SingleResponseDto;
import GInternational.server.api.dto.CasinoRequestDTO;
import GInternational.server.api.dto.CasinoResponseDTO;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.service.CasinoService;
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
public class CasinoController {

    private final CasinoService casinoService;

    /**
     * 사용자의 스포츠 머니를 카지노 머니로 전환.
     *
     * @param userId 스포츠 머니를 카지노 머니로 전환할 사용자의 ID
     * @param casinoRequestDTO 전환할 금액 정보를 담은 요청 DTO
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 전환된 후의 카지노 및 스포츠 머니 잔액 정보를 담은 응답 DTO와 함께 HTTP 상태 코드 OK 반환
     */
    @PostMapping("/users/{userId}/sports-to-casino")
    public ResponseEntity sportsToCasino(@PathVariable ("userId") @Positive Long userId,
                                         @RequestBody CasinoRequestDTO casinoRequestDTO,
                                         HttpServletRequest request,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        CasinoResponseDTO response = casinoService.exchangedSports(userId, casinoRequestDTO, principal, request);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "카지노머니로 전환되었습니다.");
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }

    /**
     * 사용자의 카지노 머니를 스포츠 머니로 전환.
     *
     * @param userId 카지노 머니를 스포츠 머니로 전환할 사용자의 ID
     * @param casinoRequestDTO 전환할 금액 정보를 담은 요청 DTO
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 전환된 후의 스포츠 및 카지노 머니 잔액 정보를 담은 응답 DTO와 함께 HTTP 상태 코드 OK 반환
     */
    @PostMapping("/users/{userId}/casino-to-sports")
    public ResponseEntity casinoToSports(@PathVariable("userId") @Positive Long userId,
                                         @RequestBody CasinoRequestDTO casinoRequestDTO,
                                         HttpServletRequest request,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        CasinoResponseDTO response = casinoService.exchangedCasino(userId, casinoRequestDTO, principal, request);
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("data", new SingleResponseDto<>(response));
        responseBody.put("message", "스포츠머니로 전환되었습니다.");
        return new ResponseEntity<>(new SingleResponseDto<>(response), HttpStatus.OK);
    }
}
