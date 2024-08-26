package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonExchangeTransactionApprovedDTO;
import GInternational.server.api.dto.AmazonExchangeTransactionsSummaryDTO;
import GInternational.server.api.entity.AmazonExchangeTransaction;
import GInternational.server.api.entity.AmazonRechargeTransaction;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.AmazonExchangeRepository;
import GInternational.server.api.vo.AmazonTransactionEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
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

    public List<AmazonExchangeTransaction> findAllByCriteria(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                             AmazonTransactionEnum status, String username,
                                                             String nickname, String ownerName) {
        Specification<AmazonExchangeTransaction> spec = (root, query, criteriaBuilder) -> {
            // 기본 필터링: 날짜 범위와 상태
            Predicate predicate = criteriaBuilder.conjunction();
            predicate = criteriaBuilder.and(predicate,
                    criteriaBuilder.between(root.get("processedAt"), startDateTime, endDateTime),
                    criteriaBuilder.equal(root.get("status"), status));

            // User와 Wallet과 조인하고 필터 적용
            if (username != null || nickname != null || ownerName != null) {
                Join<AmazonRechargeTransaction, User> userJoin = root.join("user", JoinType.LEFT);
                Join<AmazonRechargeTransaction, Wallet> walletJoin = root.join("wallet", JoinType.LEFT);

                if (username != null) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.equal(userJoin.get("username"), username));
                }

                if (nickname != null) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.equal(userJoin.get("nickname"), nickname));
                }

                if (ownerName != null) {
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.equal(walletJoin.get("ownerName"), ownerName));
                }
            }

            return predicate;
        };

        return amazonExchangeRepository.findAll(spec);
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
