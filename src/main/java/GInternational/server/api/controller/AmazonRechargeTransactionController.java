package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonRechargeTransactionApprovedDTO;
import GInternational.server.api.dto.AmazonRechargeTransactionsSummaryDTO;
import GInternational.server.api.entity.AmazonRechargeTransaction;
import GInternational.server.api.mapper.AmazonRechargeTransactionAdminResponseMapper;
import GInternational.server.api.mapper.AmazonRechargeTransactionResponseMapper;
import GInternational.server.api.service.AmazonRechargeTransactionService;
import GInternational.server.api.vo.AmazonTransactionEnum;
import GInternational.server.common.dto.MultiResponseDto;
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
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/amazon/api/v2")
@RequiredArgsConstructor
public class AmazonRechargeTransactionController {


    private final AmazonRechargeTransactionService amazonRechargeTransactionService;
    private final AmazonRechargeTransactionResponseMapper mapper;
    private final AmazonRechargeTransactionAdminResponseMapper amazonRechargeTransactionAdminResponseMapper;

    /**
     * 사용자 ID 별 충전 트랜잭션 조회.
     * 사용자 ID를 기반으로 해당 사용자의 충전 트랜잭션 목록을 페이지 단위로 조회.
     *
     * @param userId 조회할 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지 당 트랜잭션 수
     * @param authentication 인증 정보
     * @return 페이징 처리된 충전 트랜잭션 목록
     */
    @GetMapping("/managers/{userId}/recharge/transaction")
    public ResponseEntity getTransaction(@PathVariable("userId") @Positive Long userId,
                                         @RequestParam int page,
                                         @RequestParam int size,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AmazonRechargeTransaction> transactions = amazonRechargeTransactionService.getTransactionsByUserId(userId,page,size,principal);
        List<AmazonRechargeTransaction> list = transactions.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(mapper.toDto(list), transactions), HttpStatus.OK);
    }

    /**
     * 충전 트랜잭션 상태 및 생성 날짜 범위 조회.
     * 주어진 상태와 날짜 범위에 해당하는 충전 트랜잭션 목록을 조회.
     *
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param status 트랜잭션 상태
     * @param authentication 인증 정보
     * @return 조회된 충전 트랜잭션 목록
     */
    @GetMapping("/managers/rt")
    public ResponseEntity getRT(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                @RequestParam AmazonTransactionEnum status,
                                Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AmazonRechargeTransaction> amazonRechargeTransactionList = amazonRechargeTransactionService.findAllByProcessedAtBetweenAndStatus(startDateTime, endDateTime, status, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(amazonRechargeTransactionAdminResponseMapper.toDto(amazonRechargeTransactionList)), HttpStatus.OK);
    }

    /**
     * 생성된 날짜 기준으로 충전 트랜잭션 조회.
     * 지정된 날짜 범위 내에서 생성된 충전 트랜잭션을 조회.
     *
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param status 트랜잭션 상태
     * @param authentication 인증 정보
     * @return 조회된 충전 트랜잭션 목록
     */
    @GetMapping("/managers/rt/created")
    public ResponseEntity getCreatedRT(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                       @RequestParam AmazonTransactionEnum status,
                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<AmazonRechargeTransaction> amazonRechargeTransactionList = amazonRechargeTransactionService.findAllTransactionsByCreatedAtBetweenDatesWithStatus(startDateTime, endDateTime, status, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(amazonRechargeTransactionAdminResponseMapper.toDto(amazonRechargeTransactionList)), HttpStatus.OK);
    }

    /**
     * 승인된 충전 트랜잭션 조회.
     * 사용자 ID와 날짜 범위를 기준으로 승인된 충전 트랜잭션을 페이징 처리하여 조회.
     *
     * @param userId 사용자 ID
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param page 페이지 번호
     * @param size 페이지 당 트랜잭션 수
     * @param authentication 인증 정보
     * @return 페이징 처리된 승인된 충전 트랜잭션 목록
     */
    @GetMapping("/managers/approved")
    public ResponseEntity<Page<AmazonRechargeTransactionApprovedDTO>> getApprovedTransactions(@RequestParam Long userId,
                                                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                                              @RequestParam(defaultValue = "1") int page,
                                                                                              @RequestParam(defaultValue = "10") int size,
                                                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Page<AmazonRechargeTransactionApprovedDTO> pagedTransactions = amazonRechargeTransactionService.getApprovedTransactionsWithPagination(
                userId, startDateTime, endDateTime, page, size, principal);
        return ResponseEntity.ok(pagedTransactions);
    }

    /**
     * 기간별 총 충전금액과 평균 충전금액 조회.
     * 주어진 기간 동안 사용자의 총 충전금액과 평균 충전금액을 계산하여 반환.
     *
     * @param userId 사용자 ID
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param authentication 인증 정보
     * @return 총 충전금액과 평균 충전금액이 포함된 요약 정보
     */
    @GetMapping("/managers/approved/summary")
    public ResponseEntity<AmazonRechargeTransactionsSummaryDTO> getTransactionsSummary(@RequestParam Long userId,
                                                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        AmazonRechargeTransactionsSummaryDTO summary = amazonRechargeTransactionService.getTransactionsSummary(userId, startDateTime, endDateTime, principal);
        return ResponseEntity.ok(summary);
    }
}
