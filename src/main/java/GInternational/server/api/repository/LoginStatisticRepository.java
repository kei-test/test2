package GInternational.server.api.repository;

import GInternational.server.api.dto.LoginStatisticDTO;
import GInternational.server.api.entity.LoginStatistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface LoginStatisticRepository extends JpaRepository<LoginStatistic, Long> {

    @Query("SELECT new GInternational.server.api.dto.LoginStatisticDTO" +
            "(s.date, SUM(s.visitCount), SUM(s.rechargedCount), SUM(s.exchangeCount), SUM(s.debitCount), SUM(s.createUserCount))" +
            "FROM login_statistic s WHERE s.date BETWEEN :startDate AND :endDate GROUP BY s.date")
    List<LoginStatisticDTO> findAllStatisticsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT s.aasId FROM login_statistic s WHERE s.date = :date")
    Set<Integer> findDistinctAasIdByDate(LocalDate date);
}