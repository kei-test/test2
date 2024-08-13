package GInternational.server.api.repository;

import GInternational.server.api.entity.RollingTransaction;
import GInternational.server.api.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RollingTransactionRepository extends JpaRepository<RollingTransaction, Long>, JpaSpecificationExecutor<RollingTransaction> {

    boolean existsByUserAndCreatedAtGreaterThanEqual(User user, LocalDateTime createdAt);

    @Query("SELECT SUM(r.calculatedReward) " +
            "FROM rolling_transaction r " +
            "JOIN r.user u " +
            "WHERE FUNCTION('MONTH', r.processedAt) = :month " +
            "AND FUNCTION('YEAR', r.processedAt) = :year " +
            "AND u.role = 'ROLE_USER'")
    Long sumRollingPointsByMonthAndYearForRoleUser(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(rt.calculatedReward) " +
            "FROM rolling_transaction rt " +
            "JOIN rt.user u " +
            "WHERE rt.processedAt BETWEEN :startOfDay AND :endOfDay " +
            "AND u.role = 'ROLE_USER'")
    Long getRollingPointByDateForRoleUser(@Param("startOfDay") LocalDateTime startOfDay,
                                          @Param("endOfDay") LocalDateTime endOfDay);

    @EntityGraph(attributePaths = {"user"})
    List<RollingTransaction> findAll(Specification<RollingTransaction> spec, Sort sort);
}
