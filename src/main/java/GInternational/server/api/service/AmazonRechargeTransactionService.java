package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonRechargeTransactionApprovedDTO;
import GInternational.server.api.dto.AmazonRechargeTransactionsSummaryDTO;
import GInternational.server.api.entity.AmazonRechargeTransaction;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.AmazonRechargeTransactionRepository;
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
public class AmazonRechargeTransactionService {

    private final AmazonRechargeTransactionRepository amazonRechargeTransactionRepository;

    /**
     * 사용자 ID로 충전 트랜잭션 조회
     * @param userId 사용자 ID
     * @param page 페이지 번호
     * @param size 페이지당 표시할 항목 수
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 조회된 충전 트랜잭션 페이지
     */
    public Page<AmazonRechargeTransaction> getTransactionsByUserId(Long userId, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("userId").descending());
        Page<AmazonRechargeTransaction> transactions = amazonRechargeTransactionRepository.findByUserId(userId, pageable);
        long totalElements = amazonRechargeTransactionRepository.countByUserId(userId);
        return new PageImpl<>(transactions.getContent(),pageable,totalElements);
    }

    public List<AmazonRechargeTransaction> findAllByCriteria(LocalDateTime startDateTime, LocalDateTime endDateTime,
                                                             AmazonTransactionEnum status, String username,
                                                             String nickname, String ownerName) {
        Specification<AmazonRechargeTransaction> spec = (root, query, criteriaBuilder) -> {
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

        return amazonRechargeTransactionRepository.findAll(spec);
    }

    /**
     * 생성된 날짜와 상태로 모든 충전 트랜잭션 조회
     * @param startDateTime 시작 날짜와 시간
     * @param endDateTime 종료 날짜와 시간
     * @param status 트랜잭션 상태
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 조회된 충전 트랜잭션 목록
     */
    public List<AmazonRechargeTransaction> findAllTransactionsByCreatedAtBetweenDatesWithStatus(
            LocalDateTime startDateTime, LocalDateTime endDateTime, AmazonTransactionEnum status, PrincipalDetails principalDetails) {
        return amazonRechargeTransactionRepository.findAllByCreatedAtBetweenAndStatus(startDateTime, endDateTime, status);
    }

    /**
     * 승인된 거래 조회
     * @param userId 사용자 ID
     * @param startDateTime 시작 날짜와 시간
     * @param endDateTime 종료 날짜와 시간
     * @param page 페이지 번호
     * @param size 페이지당 표시할 항목 수
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 조회된 승인된 거래 페이지
     */
    public Page<AmazonRechargeTransactionApprovedDTO> getApprovedTransactionsWithPagination(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return amazonRechargeTransactionRepository.findByUserIdAndStatusAndProcessedAtBetween(
                        userId, AmazonTransactionEnum.APPROVAL, startDateTime, endDateTime, pageable)
                .map(transaction -> new AmazonRechargeTransactionApprovedDTO(transaction.getId(), transaction.getRechargeAmount(), transaction.getProcessedAt()));
    }

    /**
     * 기간별 총 충전금액과 평균 충전금액 조회
     * @param userId 사용자 ID
     * @param startDateTime 시작 날짜와 시간
     * @param endDateTime 종료 날짜와 시간
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 조회된 총 충전금액과 평균 충전금액 정보
     */
    public AmazonRechargeTransactionsSummaryDTO getTransactionsSummary(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, PrincipalDetails principalDetails) {
        List<AmazonRechargeTransaction> approvedAmazonRechargeTransactions = amazonRechargeTransactionRepository.findByUserIdAndStatusAndProcessedAtBetween(
                userId, AmazonTransactionEnum.APPROVAL, startDateTime, endDateTime);

        long totalRechargeAmount = approvedAmazonRechargeTransactions.stream()
                .mapToLong(AmazonRechargeTransaction::getRechargeAmount)
                .sum();

        BigDecimal averageRechargeAmount = approvedAmazonRechargeTransactions.isEmpty() ? BigDecimal.ZERO :
                BigDecimal.valueOf(approvedAmazonRechargeTransactions.stream()
                        .mapToLong(AmazonRechargeTransaction::getRechargeAmount)
                        .average()
                        .orElse(0));

        return new AmazonRechargeTransactionsSummaryDTO(totalRechargeAmount, averageRechargeAmount);
    }
}


