package GInternational.server.api.repository;

import GInternational.server.api.dto.MonthlyBetStatisticDailyAmountDTO;
import GInternational.server.api.entity.RechargeTransaction;
import GInternational.server.api.entity.User;
import GInternational.server.api.vo.TransactionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RechargeTransactionRepository extends JpaRepository<RechargeTransaction, Long>, JpaSpecificationExecutor<RechargeTransaction>, RechargeTransactionRepositoryCustom {

    List<RechargeTransaction> findAllByProcessedAtBetweenAndStatus(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            TransactionEnum status);

    // 특정 날짜에 처리된 트랜잭션 조회
    Page<RechargeTransaction> findByUserIdAndStatusAndProcessedAtBetween(
            Long userId,
            TransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable);

    List<RechargeTransaction> findByUserIdAndStatusAndProcessedAtBetween(
            Long userId,
            TransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

    List<RechargeTransaction> findByUserIdAndStatus(
            Long userId,
            TransactionEnum status);

    List<RechargeTransaction> findByStatusAndProcessedAtBetween(
            TransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

    // 각 일별 총 충전금액 계산
    @Query("SELECT new GInternational.server.api.dto.MonthlyBetStatisticDailyAmountDTO(FUNCTION('DAY', rt.processedAt), SUM(rt.rechargeAmount), 0L, 0L, 0L, 0L, 0L, 0L, 0L) " +
            "FROM recharge_transaction rt JOIN rt.user u " +
            "WHERE FUNCTION('MONTH', rt.processedAt) = :month AND FUNCTION('YEAR', rt.processedAt) = :year AND u.role = 'ROLE_USER' " +
            "GROUP BY FUNCTION('DAY', rt.processedAt)")
    List<MonthlyBetStatisticDailyAmountDTO> findDailyRechargeAmountSum(@Param("month") int month, @Param("year") int year);

    // 유저별, 일별 첫 충전 사용자 ID 조회
    @Query("SELECT DISTINCT rt.user.id, FUNCTION('DAY', rt.processedAt) as dayOfMonth " +
            "FROM recharge_transaction rt JOIN rt.user u " +
            "WHERE FUNCTION('MONTH', rt.processedAt) = :month AND FUNCTION('YEAR', rt.processedAt) = :year " +
            "AND rt.status = 'APPROVAL' AND u.role = 'ROLE_USER'")
    List<Object[]> findDistinctUserIdsByProcessedMonthAndYearAndRoleUser(@Param("month") int month, @Param("year") int year);


    // 월의 모든 충전금액의 합을 계산
    @Query("SELECT SUM(rt.rechargeAmount) " +
            "FROM recharge_transaction rt " +
            "JOIN rt.user u " +
            "WHERE FUNCTION('MONTH', rt.processedAt) = :month AND FUNCTION('YEAR', rt.processedAt) = :year " +
            "AND u.role = 'ROLE_USER'")
    Long sumRechargeAmountByMonthAndYearForRoleUser(@Param("month") int month, @Param("year") int year);

    // 월의 모든 충전건수의 합을 계산
    @Query("SELECT COUNT(rt) " +
            "FROM recharge_transaction rt " +
            "JOIN rt.user u " +
            "WHERE FUNCTION('MONTH', rt.processedAt) = :month " +
            "AND FUNCTION('YEAR', rt.processedAt) = :year " +
            "AND u.role = 'ROLE_USER'")
    int countRechargeTransactionsByMonthAndYearForRoleUser(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(rt) FROM recharge_transaction rt WHERE rt.user.role = 'ROLE_USER' AND rt.processedAt BETWEEN :startOfDay AND :endOfDay")
    int countFirstRechargesByProcessedAtBetweenAndUserRole(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT SUM(rt.rechargeAmount) " +
            "FROM recharge_transaction rt " +
            "JOIN rt.user u " +
            "WHERE rt.processedAt BETWEEN :startOfDay AND :endOfDay " +
            "AND u.role = 'ROLE_USER'")
    Long getDailyTotalRechargeAmountForRoleUser(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(rt) " +
            "FROM recharge_transaction rt " +
            "JOIN rt.user u " +
            "WHERE rt.processedAt >= :startOfDay AND rt.processedAt < :endOfDay " +
            "AND u.role = 'ROLE_USER'")
    int getDailyRechargeCountForRoleUser(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(DISTINCT rt.user.id) " +
            "FROM recharge_transaction rt " +
            "JOIN rt.user u " +
            "WHERE rt.processedAt BETWEEN :startOfDay AND :endOfDay " +
            "AND rt.status = 'APPROVAL' " +
            "AND rt.isFirstRecharge = true " +
            "AND u.role = 'ROLE_USER'")
    int countDistinctFirstRechargesByDayForRoleUser(@Param("startOfDay") LocalDateTime startOfDay,
                                                    @Param("endOfDay") LocalDateTime endOfDay);

    // 특정 사용자가 지정된 날짜 범위 내에 특정 상태의 거래가 있는지 확인
    @Query("SELECT COUNT(rt) > 0 FROM recharge_transaction rt " +
            "WHERE rt.user = :user AND rt.status = :status " +
            "AND rt.processedAt BETWEEN :startOfDay AND :endOfDay")
    boolean existsByUserAndStatusAndProcessedAtBetween(@Param("user") User user,
                                                       @Param("status") TransactionEnum status,
                                                       @Param("startOfDay") LocalDateTime startOfDay,
                                                       @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(DISTINCT FUNCTION('DAY', rt.processedAt)) " +
            "FROM recharge_transaction rt " +
            "JOIN rt.user u " +
            "WHERE FUNCTION('MONTH', rt.processedAt) = :month AND FUNCTION('YEAR', rt.processedAt) = :year " +
            "AND rt.isFirstRecharge = true AND u.role = 'ROLE_USER'")
    int countDaysWithFirstRechargeByUserRole(@Param("month") int month, @Param("year") int year);

    @Query("SELECT COUNT(DISTINCT FUNCTION('DAY', rt.processedAt)) " +
            "FROM recharge_transaction rt " +
            "JOIN rt.user u " +
            "WHERE FUNCTION('MONTH', rt.processedAt) = :month AND FUNCTION('YEAR', rt.processedAt) = :year " +
            "AND u.role = 'ROLE_USER'")
    int countDaysWithRechargeByUserRole(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(rt.rechargeAmount) FROM recharge_transaction rt JOIN rt.user u WHERE u.role = :role AND rt.processedAt BETWEEN :startDateTime AND :endDateTime AND rt.status = :status")
    Long sumRechargeAmountByUserRolesAndProcessedAtBetweenAndStatus(@Param("role") String role, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime, @Param("status") TransactionEnum status);

    @Query("SELECT COUNT(r) FROM recharge_transaction r WHERE r.status = :status")
    Long countByStatus(@Param("status") TransactionEnum status);

    @Query("SELECT COUNT(r) FROM recharge_transaction r WHERE r.status IN :statuses AND r.createdAt BETWEEN :start AND :end")
    Long countByStatusesAndDateBetween(Set<TransactionEnum> statuses, LocalDateTime start, LocalDateTime end);

    @Query("SELECT COUNT(r) FROM recharge_transaction r WHERE r.status IN :statuses AND r.createdAt BETWEEN :start AND :end")
    int countByCreatedAtBetweenAndStatus(LocalDateTime start, LocalDateTime end, Set<TransactionEnum> statuses);

    @Query(value = "SELECT SUM(r.recharge_amount) FROM recharge_transaction r WHERE r.user_id = :userId AND DATE(r.processed_at) = :transactionDate AND r.status = :status", nativeQuery = true)
    BigDecimal findSumByUserAndDateAndStatus(@Param("userId") Long userId, @Param("transactionDate") LocalDate transactionDate, @Param("status") String status);

    List<RechargeTransaction> findAllByStatusInAndCreatedAtAfter(List<TransactionEnum> statuses, LocalDateTime createdAt);
}