package GInternational.server.api.repository;

import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.api.entity.MoneyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MoneyLogRepository extends JpaRepository<MoneyLog, Long>, JpaSpecificationExecutor<MoneyLog> {

    // 특정 카테고리의 스포츠머니 합계 계산
    @Query("SELECT SUM(m.usedSportsBalance) FROM money_log m WHERE m.category = :category")
    Long sumByCategory(@Param("category") MoneyLogCategoryEnum category);

    // 특정 유저의 특정 카테고리 내 스포츠머니 합계 계산 (지정된 날짜 이후)
    @Query("SELECT SUM(m.usedSportsBalance) FROM money_log m WHERE m.category = :category AND m.user.id = :userId AND m.createdAt >= :sinceDate")
    Long sumByCategoryAndUserIdSince(@Param("category") MoneyLogCategoryEnum category, @Param("userId") Long userId, @Param("sinceDate") LocalDateTime sinceDate);

    // 특정 유저의 마지막 충전 날짜 가져오기
    @Query("SELECT MAX(m.createdAt) FROM money_log m WHERE m.category = '충전' AND m.user.id = :userId")
    LocalDateTime findLastRechargeDateByUserId(@Param("userId") Long userId);
}