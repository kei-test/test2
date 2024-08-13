package GInternational.server.api.controller;

import GInternational.server.common.dto.MultiResponseDto;
import GInternational.server.api.entity.AutoDepositTransaction;
import GInternational.server.api.mapper.AutoDepositTransactionMapper;
import GInternational.server.api.service.AutoDepositTransactionService;
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
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class AutoDepositTransactionController {

    private final AutoDepositTransactionService autoDepositTransactionService;
    private final AutoDepositTransactionMapper autoDepositTransactionMapper;

    /**
     * 지정된 기간 동안의 자동 입금 거래 조회.
     *
     * @param startDate 조회 시작 날짜, 선택 사항
     * @param endDate 조회 종료 날짜, 선택 사항
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 자동 입금 거래 목록과 HTTP 상태 코드 OK
     */
    @GetMapping("/managers/adt")
    public ResponseEntity getADT(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AutoDepositTransaction> list = autoDepositTransactionService.autoDepositTransaction(startDate, endDate, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(autoDepositTransactionMapper.toDto(list)), HttpStatus.OK);
    }

    /**
     * 지정된 기간 동안의 자동 입금 거래 조회.
     *
     * @param startDate 조회 시작 날짜, 선택 사항
     * @param endDate 조회 종료 날짜, 선택 사항
     * @param authentication 현재 인증된 사용자의 인증 정보
     * @return 자동 입금 거래 목록과 HTTP 상태 코드 OK
     */
    @GetMapping("/managers/ads")
    public ResponseEntity getADS(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                 @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                 Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AutoDepositTransaction> list = autoDepositTransactionService.findAllADT(startDate, endDate, principal);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }
}
