package GInternational.server.api.controller;

import GInternational.server.api.dto.CasinoTransactionResponseDTO;
import GInternational.server.common.dto.MultiResponseDto;

import GInternational.server.api.entity.CasinoTransaction;
import GInternational.server.api.mapper.CasinoTransactionResponseMapper;
import GInternational.server.api.service.CasinoTransactionService;

import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v2")
@RequiredArgsConstructor
public class CasinoTransactionController {

    private final CasinoTransactionService casinoTransactionService;
    private final CasinoTransactionResponseMapper mapper;

    /**
     * 특정 사용자의 카지노 트랜잭션 조회.
     *
     * @param userId       사용자 ID
     * @param description  트랜잭션 설명
     * @param page         페이지 번호
     * @param size         페이지 크기
     * @param authentication 인증 객체
     * @return ResponseEntity 객체
     */
    @GetMapping("/users/{userId}/casino/transaction")
    public ResponseEntity getCasinoTransaction(@PathVariable("userId") @Positive Long userId,
                                               @RequestParam(value = "description", required = false) String description,
                                               @RequestParam int page,
                                               @RequestParam int size,
                                               Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<CasinoTransaction> transactions = casinoTransactionService.getCasinoTransactionsByUserId(userId, description, page, size, principal);
        List<CasinoTransaction> list = transactions.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(list, transactions), HttpStatus.OK);
    }

    /**
     * 카지노 트랜잭션 조회.
     *
     * @param description  트랜잭션 설명
     * @param startDate    시작 날짜
     * @param endDate      종료 날짜
     * @param username     사용자 이름
     * @param nickname     닉네임
     * @param ip           IP 주소
     * @param authentication 인증 객체
     * @return ResponseEntity 객체
     */
    @GetMapping("/managers/ct")
    public ResponseEntity getCasinoTransactions(@RequestParam(name = "description", required = false) String description,
                                                @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                                @RequestParam(name = "username", required = false) String username,
                                                @RequestParam(name = "nickname", required = false) String nickname,
                                                @RequestParam(name = "ip", required = false) String ip,
                                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<CasinoTransactionResponseDTO> dtoList = casinoTransactionService.findByCasinoTransaction(description, startDate, endDate, username, nickname, ip, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(dtoList), HttpStatus.OK);
    }
}