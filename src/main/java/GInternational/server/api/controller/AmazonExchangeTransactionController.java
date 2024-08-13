package GInternational.server.api.controller;

import GInternational.server.api.dto.AmazonExchangeTransactionApprovedDTO;
import GInternational.server.api.dto.AmazonExchangeTransactionsSummaryDTO;
import GInternational.server.api.entity.AmazonExchangeTransaction;
import GInternational.server.api.mapper.AmazonExchangeTransactionAdminResponseMapper;
import GInternational.server.api.mapper.AmazonExchangeTransactionResponseMapper;
import GInternational.server.api.service.AmazonExchangeTransactionService;
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
public class AmazonExchangeTransactionController {


    private final AmazonExchangeTransactionService amazonExchangeTransactionService;
    private final AmazonExchangeTransactionResponseMapper mapper;
    private final AmazonExchangeTransactionAdminResponseMapper amazonExchangeTransactionAdminResponseMapper;

    /**
     * 회원 ID별 트랜잭션을 페이지네이션과 함께 검색.
     *
     * @param userId 검색할 회원의 ID
     * @param page 페이지 번호
     * @param size 페이지 당 크기
     * @param authentication 사용자의 인증 정보가 담긴 객체
     * @return 회원의 트랜잭션 페이지를 담은 ResponseEntity를 반환.
     */
    @GetMapping("/managers/{userId}/exchange/transaction")
    public ResponseEntity getTransaction(@PathVariable("userId") @Positive Long userId,
                                         @RequestParam int page,
                                         @RequestParam int size,
                                         Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Page<AmazonExchangeTransaction> transactions = amazonExchangeTransactionService.getExchangeTransactionsByUserId(userId,page,size,principal);
        List<AmazonExchangeTransaction> list = transactions.getContent();
        return new ResponseEntity<>(new MultiResponseDto<>(mapper.toDto(list), transactions), HttpStatus.OK);
    }

    /**
     * 지정된 상태와 처리된 날짜 범위에 따라 트랜잭션을 검색.
     *
     * @param startDate 검색 시작일
     * @param endDate 검색 종료일
     * @param status 트랜잭션 상태
     * @param authentication 사용자의 인증 정보
     * @return 조건에 맞는 트랜잭션 목록을 담은 ResponseEntity를 반환.
     */
    @GetMapping("/managers/et")
    public ResponseEntity getET(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                @RequestParam AmazonTransactionEnum status,
                                Authentication authentication) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmazonExchangeTransaction> transactionList = amazonExchangeTransactionService.findByStatusAndProcessedAtBetween(status, startDateTime, endDateTime, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(amazonExchangeTransactionAdminResponseMapper.toDto(transactionList)), HttpStatus.OK);
    }

    /**
     * 생성된 트랜잭션을 검색. (상태 및 날짜 조건 적용)
     *
     * @param startDate 검색 시작일
     * @param endDate 검색 종료일
     * @param status 트랜잭션 상태
     * @param authentication 사용자의 인증 정보
     * @return 조건에 맞는 생성된 트랜잭션 목록을 담은 ResponseEntity를 반환.
     */
    @GetMapping("/managers/et/created")
    public ResponseEntity getCreatedET(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                       @RequestParam AmazonTransactionEnum status,
                                       Authentication authentication) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        List<AmazonExchangeTransaction> amazonExchangeTransactionList = amazonExchangeTransactionService.findAllTransactionsByCreatedAtBetweenDatesWithStatus(startDateTime, endDateTime, status, principal);
        return new ResponseEntity<>(new MultiResponseDto<>(amazonExchangeTransactionAdminResponseMapper.toDto(amazonExchangeTransactionList)), HttpStatus.OK);
    }

    /**
     * 승인된 트랜잭션을 조회.
     *
     * @param userId 회원 ID
     * @param startDate 검색 시작일
     * @param endDate 검색 종료일
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param authentication 사용자의 인증 정보
     * @return 승인된 트랜잭션 페이지를 담은 ResponseEntity를 반환.
     */
    @GetMapping("/managers/ex-approved")
    public ResponseEntity<Page<AmazonExchangeTransactionApprovedDTO>> getApprovedTransactions(@RequestParam Long userId,
                                                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                                              @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                                              @RequestParam(defaultValue = "1") int page,
                                                                                              @RequestParam(defaultValue = "10") int size,
                                                                                              Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Page<AmazonExchangeTransactionApprovedDTO> pagedTransactions = amazonExchangeTransactionService.getApprovedTransactionsWithPagination(
                userId, startDateTime, endDateTime, page, size, principal);
        return ResponseEntity.ok(pagedTransactions);
    }

    /**
     * 기간별 총 충전금액과 평균 충전금액을 조회.
     *
     * @param userId 회원 ID
     * @param startDate 검색 시작일
     * @param endDate 검색 종료일
     * @param authentication 사용자의 인증 정보
     * @return 기간별 총 및 평균 충전금액을 담은 ResponseEntity를 반환.
     */
    @GetMapping("/managers/ex-approved/summary")
    public ResponseEntity<AmazonExchangeTransactionsSummaryDTO> getTransactionsSummary(@RequestParam Long userId,
                                                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
                                                                                       Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        AmazonExchangeTransactionsSummaryDTO summary = amazonExchangeTransactionService.getTransactionsSummary(userId, startDateTime, endDateTime, principal);
        return ResponseEntity.ok(summary);
    }
}
