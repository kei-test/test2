package GInternational.server.api.repository;

import GInternational.server.api.entity.User;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.api.dto.MonthlyBetStatisticDailyAmountDTO;
import GInternational.server.api.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PointLogRepository extends JpaRepository<PointLog, Long>, JpaSpecificationExecutor<PointLog> {

    @Query("SELECT new GInternational.server.api.dto.MonthlyBetStatisticDailyAmountDTO(FUNCTION('DAY', p.createdAt), 0L, 0L, 0L, SUM(p.point), 0L, 0L, 0L, 0L) " +
            "FROM point_log p " +
            "JOIN p.userId u " +
            "WHERE FUNCTION('MONTH', p.createdAt) = :month AND FUNCTION('YEAR', p.createdAt) = :year AND u.role = 'ROLE_USER' " +
            "GROUP BY FUNCTION('DAY', p.createdAt)")
    List<MonthlyBetStatisticDailyAmountDTO> findDailyPointAmountSum(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(p.point) " +
            "FROM point_log p " +
            "JOIN p.userId u " +
            "WHERE FUNCTION('MONTH', p.createdAt) = :month " +
            "AND FUNCTION('YEAR', p.createdAt) = :year " +
            "AND u.role = 'ROLE_USER'")
    Long sumPointsByMonthAndYearForRoleUser(@Param("month") int month, @Param("year") int year);

    @Query("SELECT SUM(pl.point) " +
            "FROM point_log pl " +
            "JOIN pl.userId u " +
            "WHERE pl.createdAt BETWEEN :startOfDay AND :endOfDay " +
            "AND u.role = 'ROLE_USER'")
    Long getPointByDateForRoleUser(@Param("startOfDay") LocalDateTime startOfDay,
                                   @Param("endOfDay") LocalDateTime endOfDay);

    List<PointLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<PointLog> findByUserId(User user);

    @Query("SELECT SUM(pl.point) FROM point_log pl WHERE pl.createdAt BETWEEN :startDateTime AND :endDateTime AND pl.category IN :categories")
    Long sumPointsByCategoriesAndCreatedAtBetween(@Param("categories") List<PointLogCategoryEnum> categories, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}