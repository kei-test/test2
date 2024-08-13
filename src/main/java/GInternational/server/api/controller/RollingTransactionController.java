package GInternational.server.api.controller;

import GInternational.server.api.dto.RollingTransactionResDTO;
import GInternational.server.api.entity.RollingTransaction;
import GInternational.server.api.service.RollingTransactionService;
import GInternational.server.api.vo.RollingTransactionEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2/managers/rolling/transaction")
@RequiredArgsConstructor
public class RollingTransactionController {

    private final RollingTransactionService rollingTransactionService;

    /**
     * 주어진 조건에 따라 롤링 거래 정보 조회.
     *
     * @param startDate       조회 시작 날짜 (옵션)
     * @param endDate         조회 종료 날짜 (옵션)
     * @param status          거래 상태 (옵션)
     * @param username        사용자 이름 (옵션)
     * @param nickname        닉네임 (옵션)
     * @param userIp          사용자 IP (옵션)
     * @param authentication  사용자 인증 정보, 현재 로그인된 사용자를 식별하기 위해 사용
     * @return                조회된 롤링 거래 정보의 DTO 리스트를 담은 응답 엔터티
     */
    @GetMapping
    public ResponseEntity<List<RollingTransactionResDTO>> getTransactions(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                                          @RequestParam(required = false) RollingTransactionEnum status,
                                                                          @RequestParam(required = false) String username,
                                                                          @RequestParam(required = false) String nickname,
                                                                          @RequestParam(required = false) String userIp,
                                                                          Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        List<RollingTransaction> transactions = rollingTransactionService.findTransactions(startDate, endDate, status, username, nickname, userIp, principal);
        List<RollingTransactionResDTO> dtos = rollingTransactionService.toDTOList(transactions);

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}


