package GInternational.server.api.repository;

import GInternational.server.api.dto.MonthlyBetStatisticDailyAmountDTO;
import GInternational.server.api.entity.ExchangeTransaction;
import GInternational.server.api.entity.RechargeTransaction;
import GInternational.server.api.vo.TransactionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ExchangeRepository extends JpaRepository<ExchangeTransaction,Long>, JpaSpecificationExecutor<ExchangeTransaction>, ExchangeRepositoryCustom{

    // 특정 날짜에 처리된 트랜잭션 조회
    Page<ExchangeTransaction> findByUserIdAndStatusAndProcessedAtBetween(
            Long userId,
            TransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable);

    List<ExchangeTransaction> findByUserIdAndStatusAndProcessedAtBetween(
            Long userId,
            TransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

    List<ExchangeTransaction> findByUserIdAndStatus(
            Long userId,
            TransactionEnum status);

    List<ExchangeTransaction> findByStatusAndProcessedAtBetween(
            TransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

    @Query("SELECT new GInternational.server.api.dto.MonthlyBetStatisticDailyAmountDTO(FUNCTION('DAY', e.processedAt), 0L, SUM(e.exchangeAmount), 0L, 0L, 0L, 0L, 0L, 0L) " +
            "FROM exchange_transaction e JOIN e.user u " +
            "WHERE FUNCTION('MONTH', e.processedAt) = :month AND FUNCTION('YEAR', e.processedAt) = :year AND u.role = 'ROLE_USER' " +
            "GROUP BY FUNCTION('DAY', e.processedAt)")
    List<MonthlyBetStatisticDailyAmountDTO> findDailyExchangeAmountSum(@Param("month") int month, @Param("year") int year);

    // 월별 총 환전 금액을 계산하는 메서드
    @Query("SELECT SUM(et.exchangeAmount) " +
            "FROM exchange_transaction et " +
            "JOIN et.user u " +
            "WHERE FUNCTION('MONTH', et.processedAt) = :month " +
            "AND FUNCTION('YEAR', et.processedAt) = :year " +
            "AND u.role = 'ROLE_USER'")
    Long sumExchangeAmountByMonthAndYearForRoleUser(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(e) " +
            "FROM exchange_transaction e " +
            "JOIN e.user u " +
            "WHERE FUNCTION('MONTH', e.processedAt) = :month " +
            "AND FUNCTION('YEAR', e.processedAt) = :year " +
            "AND u.role = 'ROLE_USER'")
    Integer countExchangeTransactionsByMonthAndYearForRoleUser(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(e.exchangeAmount) " +
            "FROM exchange_transaction e " +
            "JOIN e.user u " +
            "WHERE e.processedAt BETWEEN :startOfDay AND :endOfDay " +
            "AND u.role = 'ROLE_USER'")
    Long getDailyTotalExchangeAmountForRoleUser(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(e) " +
            "FROM exchange_transaction e " +
            "JOIN e.user u " +
            "WHERE e.processedAt BETWEEN :startOfDay AND :endOfDay " +
            "AND u.role = 'ROLE_USER'")
    int getDailyExchangeCountForRoleUser(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(DISTINCT FUNCTION('DAY', et.processedAt)) " +
            "FROM exchange_transaction et " +
            "JOIN et.user u " +
            "WHERE FUNCTION('MONTH', et.processedAt) = :month AND FUNCTION('YEAR', et.processedAt) = :year " +
            "AND u.role = 'ROLE_USER'")
    int countDaysWithExchangeByUserRole(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(et.exchangeAmount) FROM exchange_transaction et JOIN et.user u WHERE u.role = :role AND et.processedAt BETWEEN :startDateTime AND :endDateTime AND et.status = :status")
    Long sumExchangeAmountByUserRolesAndProcessedAtBetweenAndStatus(@Param("role") String role, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("status") TransactionEnum status);

    @Query("SELECT COUNT(e) FROM exchange_transaction e WHERE e.status = :status")
    Long countByStatus(@Param("status") TransactionEnum status);

    @Query("SELECT COUNT(e) FROM exchange_transaction e WHERE e.status = :status AND e.createdAt BETWEEN :startOfDay AND :endOfDay")
    Long countApprovalByStatus(@Param("status") TransactionEnum status, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    int countByCreatedAtBetweenAndStatus(LocalDateTime startOfDay, LocalDateTime endOfDay, TransactionEnum status);
}