package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonExchangeTransactionApprovedDTO;
import GInternational.server.api.dto.AmazonExchangeTransactionsSummaryDTO;
import GInternational.server.api.entity.AmazonExchangeTransaction;
import GInternational.server.api.repository.AmazonExchangeRepository;
import GInternational.server.api.vo.AmazonTransactionEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonExchangeTransactionService {

    private final AmazonExchangeRepository amazonExchangeRepository;

    /**
     * 회원 ID 별 환전 트랜잭션 조회.
     * 페이징 처리를 적용하여 회원이 수행한 환전 트랜잭션 목록을 조회.
     *
     * @param userId 조회할 회원의 ID
     * @param page 요청된 페이지 번호
     * @param size 페이지 당 표시할 트랜잭션 수
     * @param principalDetails 요청을 수행하는 사용자의 인증 정보
     * @return 페이징 처리된 환전 트랜잭션 목록
     */
    public Page<AmazonExchangeTransaction> getExchangeTransactionsByUserId(Long userId, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("userId").descending());
        Page<AmazonExchangeTransaction> transactions = amazonExchangeRepository.findByUserId(userId, pageable);
        long totalElements = amazonExchangeRepository.countByUserId(userId);
        return new PageImpl<>(transactions.getContent(),pageable,totalElements);
    }

    /**
     * 상태와 처리된 날짜 범위에 따라 환전 트랜잭션을 조회.
     * 환전 트랜잭션 상태(예: 승인, 대기 등)와 날짜 범위를 기준으로 필터링하여 결과를 반환.
     *
     * @param status 조회할 트랜잭션의 상태
     * @param startDateTime 조회 시작 날짜 및 시간
     * @param endDateTime 조회 종료 날짜 및 시간
     * @param principalDetails 요청을 수행하는 사용자의 인증 정보
     * @return 해당 조건에 맞는 환전 트랜잭션 목록
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public List<AmazonExchangeTransaction> findByStatusAndProcessedAtBetween(AmazonTransactionEnum status, LocalDateTime startDateTime, LocalDateTime endDateTime, PrincipalDetails principalDetails) {
        return amazonExchangeRepository.findByStatusAndProcessedAtBetween(status, startDateTime, endDateTime);
    }

    /**
     * 생성된 날짜 범위와 상태에 따라 모든 환전 트랜잭션을 조회.
     * 특정 기간 동안 특정 상태(예: 승인, 대기 등)에 있는 모든 환전 트랜잭션을 조회.
     *
     * @param startDateTime 조회 시작 날짜 및 시간
     * @param endDateTime 조회 종료 날짜 및 시간
     * @param status 조회할 트랜잭션의 상태
     * @param principalDetails 요청을 수행하는 사용자의 인증 정보
     * @return 해당 조건에 맞는 환전 트랜잭션 목록
     */
    public List<AmazonExchangeTransaction> findAllTransactionsByCreatedAtBetweenDatesWithStatus(LocalDateTime startDateTime, LocalDateTime endDateTime, AmazonTransactionEnum status, PrincipalDetails principalDetails) {
        return amazonExchangeRepository.findAllByCreatedAtBetweenAndStatus(startDateTime, endDateTime, status);
    }

    /**
     * 승인된 거래를 페이징 처리하여 조회.
     * 사용자 ID, 날짜 범위를 기준으로 승인된 환전 트랜잭션을 페이징 처리하여 조회.
     *
     * @param userId 조회할 사용자 ID
     * @param startDateTime 조회 시작 날짜 및 시간
     * @param endDateTime 조회 종료 날짜 및 시간
     * @param page 요청된 페이지 번호
     * @param size 페이지 당 표시할 트랜잭션 수
     * @param principalDetails 요청을 수행하는 사용자의 인증 정보
     * @return 페이징 처리된 승인된 환전 트랜잭션 목록
     */
    public Page<AmazonExchangeTransactionApprovedDTO> getApprovedTransactionsWithPagination(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return amazonExchangeRepository.findByUserIdAndStatusAndProcessedAtBetween(
                        userId, AmazonTransactionEnum.APPROVAL, startDateTime, endDateTime, pageable)
                .map(ExchangeTransaction -> new AmazonExchangeTransactionApprovedDTO(ExchangeTransaction.getId(), ExchangeTransaction.getExchangeAmount(), ExchangeTransaction.getProcessedAt()));
    }

    /**
     * 기간별 총 충전금액과 평균 충전금액을 조회.
     * 특정 사용자에 대해 지정된 기간 동안의 총 및 평균 충전금액을 계산.
     *
     * @param userId 조회할 사용자 ID
     * @param startDateTime 조회 시작 날짜 및 시간
     * @param endDateTime 조회 종료 날짜 및 시간
     * @param principalDetails 요청을 수행하는 사용자의 인증 정보
     * @return 기간별 총 충전금액과 평균 충전금액 정보를 담은 객체
     */
    public AmazonExchangeTransactionsSummaryDTO getTransactionsSummary(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, PrincipalDetails principalDetails) {
        List<AmazonExchangeTransaction> approvedTransactions = amazonExchangeRepository.findByUserIdAndStatusAndProcessedAtBetween(
                userId, AmazonTransactionEnum.APPROVAL, startDateTime, endDateTime);

        long totalRechargeAmount = approvedTransactions.stream()
                .mapToLong(AmazonExchangeTransaction::getExchangeAmount)
                .sum();

        BigDecimal averageRechargeAmount = approvedTransactions.isEmpty() ? BigDecimal.ZERO :
                BigDecimal.valueOf(approvedTransactions.stream()
                        .mapToLong(AmazonExchangeTransaction::getExchangeAmount)
                        .average()
                        .orElse(0));

        return new AmazonExchangeTransactionsSummaryDTO(totalRechargeAmount, averageRechargeAmount);
    }
}
