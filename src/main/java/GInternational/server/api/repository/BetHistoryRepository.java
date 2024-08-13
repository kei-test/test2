package GInternational.server.api.repository;


import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import GInternational.server.l_sport.batch.job.dto.order.DetailResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Repository
public interface BetHistoryRepository extends JpaRepository<BetHistory,Long>,BetHistoryRepositoryCustom {

    LinkedList<BetHistory> findDistinctByMatchIdAndMarketName(String matchId, String marketName);




    List<BetHistory> findAll();
//    List<BetHistory> findDistinctByBetGroupId(Long betGroupId);
    BetHistory findFirstDistinctByBetGroupId(Long betGroupId);

    List<BetHistory> findByUserId(Long userId);

    @Query("SELECT COUNT(b) FROM bet_history b WHERE b.betGroupId = :betGroupId")
    long countByBetGroupId(@Param("betGroupId") Long betGroupId);

    List<BetHistory> findByBetGroupId(Long betGroupId);

    List<BetHistory> findByBetGroupIdAndDeleted(Long betGroupId, boolean Deleted);

    List<BetHistory> findByOrderStatusAndBetStartTimeBetween(OrderStatusEnum status, LocalDateTime startDate, LocalDateTime endDate);

    List<BetHistory> findByUserIdAndDeletedFalse(Long userId);

    // 특정 베팅 그룹 ID와 사용자 ID에 해당하는 베팅 내역 조회
    List<BetHistory> findByBetGroupIdAndUserId(Long betGroupId, Long userId);

    @Query("SELECT b FROM bet_history b JOIN FETCH b.user u WHERE b.matchStatus IN ('1', '2') AND b.betStartTime BETWEEN :startDateTime AND :endDateTime")
    List<BetHistory> findByMatchStatusOneOrTwoAndDateBetweenWithUser(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT b FROM bet_history b WHERE b.matchStatus = '3' AND b.betStartTime BETWEEN :startDateTime AND :endDateTime")
    List<BetHistory> findByMatchStatusThreeAndDateBetween(@Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query(value = "SELECT SUM(CAST(b.bet AS DECIMAL(10, 2))) FROM bet_history b WHERE b.bet_start_time BETWEEN :startOfDay AND :endOfNow AND b.order_status = :waitingStatus", nativeQuery = true)
    Long sumTodayTotalBetForWaitingStatus(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfNow") LocalDateTime endOfNow, @Param("waitingStatus") String waitingStatus);

    @Query("SELECT b FROM bet_history b WHERE b.matchId = :matchId ORDER BY b.betGroupId DESC")
    List<BetHistory> findByMatchId(@Param("matchId") String matchId);

    List<BetHistory> findByBetGroupIdIn(Collection<Long> betGroupIds);

    @Query(value = "SELECT b.bet_start_time, SUM(b.bet_reward) FROM bet_history b INNER JOIN (SELECT MIN(bet_history_id) as bet_history_id FROM bet_history WHERE YEAR(bet_start_time) = :year AND MONTH(bet_start_time) = :month GROUP BY bet_group_id) as bb ON b.bet_history_id = bb.bet_history_id INNER JOIN users u ON b.user_id = u.user_id WHERE u.role = 'ROLE_USER' GROUP BY DAY(b.bet_start_time)", nativeQuery = true)
    List<Object[]> findDailyBetRewardSum(int year, int month);

    @Query("SELECT bh.user.username FROM bet_history bh WHERE bh.readStatus = '미확인' AND bh.user.monitoringStatus = :monitoringStatus ORDER BY bh.betStartTime ASC")
    List<String> findUsernameByMonitoringStatusAndUnread(@Param("monitoringStatus") UserMonitoringStatusEnum monitoringStatus, Pageable pageable);

    @Modifying
    @Transactional
    @Query("UPDATE bet_history b SET b.readAt = :readAt, b.readBy = :readBy, b.readStatus = :readStatus WHERE b.betGroupId = :betGroupId")
    void updateReadStatusByGroupId(LocalDateTime readAt, String readBy, String readStatus, Long betGroupId);

    @Query("SELECT COUNT(bh) FROM bet_history bh WHERE bh.readStatus = '미확인' AND (bh.user.monitoringStatus = :monitoringStatus1 OR bh.user.monitoringStatus = :monitoringStatus2)")
    Long countUnreadByMonitoringStatus(UserMonitoringStatusEnum monitoringStatus1, UserMonitoringStatusEnum monitoringStatus2);

    @Query("SELECT bh FROM bet_history bh WHERE bh.orderStatus IN :statuses AND bh.betStartTime BETWEEN :startDateTime AND :endDateTime")
    List<BetHistory> findBetsByStatusAndBetStartTimeBetween(@Param("statuses") List<OrderStatusEnum> statuses, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

}