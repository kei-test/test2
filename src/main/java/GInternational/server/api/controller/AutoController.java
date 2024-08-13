package GInternational.server.api.controller;

import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.api.entity.AutoTransaction;
import GInternational.server.api.service.AutoTransactionService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AutoController {


    private final AutoTransactionService autoTransactionService;

    /**
     * 특정 회원의 자동승인 문자 수신 내역을 조회.
     *
     * @param userId 조회할 사용자의 ID
     * @param page 페이지 번호
     * @param size 페이지 당 항목 수
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 특정 회원의 자동승인 문자 수신 내역과 HTTP 상태 코드 OK
     */
    @GetMapping("/managers/{userId}/auto/message")
    public ResponseEntity getMessageTransaction(@PathVariable ("userId") Long userId,
                                                @RequestParam int page,
                                                @RequestParam int size,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AutoTransaction> pages = autoTransactionService.getAutoTransactionByUserId(userId, page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(pages.getContent(),pages),HttpStatus.OK);
    }

    /**
     * 모든 자동승인 문자 수신 내역을 조회.
     *
     * @param page 페이지 번호
     * @param size 페이지 당 항목 수
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 모든 자동승인 문자 수신 내역과 HTTP 상태 코드 OK
     */
    @GetMapping("/managers/auto/messages")
    public ResponseEntity findAll(@RequestParam int page,
                                  @RequestParam int size,
                                  Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AutoTransaction> pages = autoTransactionService.findAll(page, size, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(pages.getContent(),pages),HttpStatus.OK);
    }
}
