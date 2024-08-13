package GInternational.server.l_sport.batch.job.service;

import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.entity.MoneyLog;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.BetHistoryRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.l_sport.info.entity.Odd;
import GInternational.server.l_sport.info.entity.OddLive;
import GInternational.server.l_sport.info.entity.Settlement;
import GInternational.server.l_sport.info.entity.SettlementPrematch;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class JdbcBatchService {

    private final WalletRepository walletRepository;
    private final BetHistoryRepository betHistoryRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final JdbcTemplate lsportJdbcTemplate;


    public JdbcBatchService(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate,
                            @Qualifier("lsportJdbcTemplate") JdbcTemplate lsportJdbcTemplate,
                            BetHistoryRepository betHistoryRepository,
                            WalletRepository walletRepository,
                            UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.lsportJdbcTemplate = lsportJdbcTemplate;
        this.betHistoryRepository = betHistoryRepository;
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }


//    public void removeLs() {
//
//        String query1 = "DELETE FROM api_settlement";
//        String query2 = "DELETE FROM api_settlement_prematch";
//        String query3 = "DELETE FROM api_odds";
//        String query4 = "DELETE FROM api_odds_live";
//        String query5 = "DELETE FROM api_live_score";
//        String query6 = "DELETE FROM api_live_score_prematch";
//        String query7 = "DELETE FROM api_game";
//
//        lsportJdbcTemplate.batchUpdate(query1);
//        lsportJdbcTemplate.batchUpdate(query2);
//        lsportJdbcTemplate.batchUpdate(query3);
//        lsportJdbcTemplate.batchUpdate(query4);
//        lsportJdbcTemplate.batchUpdate(query5);
//        lsportJdbcTemplate.batchUpdate(query6);
//        lsportJdbcTemplate.batchUpdate(query7);
//    }








    // 개발단계 수동 배치삭제
    @Scheduled(cron = "0 0 15 * * *") // 매일 15:00에 실행
    public void deletePrecess() {

        LocalDateTime twoDaysAgo = LocalDateTime.now().minusDays(14);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedDate = twoDaysAgo.format(formatter);

        String query1 = "DELETE FROM api_settlement WHERE match_id IN (SELECT match_id FROM api_game WHERE status = '3' AND start_date < ?)";
        String query2 = "DELETE FROM api_settlement_prematch WHERE match_id IN (SELECT match_id FROM api_game WHERE status = '3' AND start_date < ?)";
        String query3 = "DELETE FROM api_odds WHERE match_id IN (SELECT match_id FROM api_game WHERE status = '3' AND start_date < ?)";
        String query4 = "DELETE FROM api_odds_live WHERE match_id IN (SELECT match_id FROM api_game WHERE status = '3' AND start_date < ?)";
        String query5 = "DELETE FROM api_live_score WHERE match_id IN (SELECT match_id FROM api_game WHERE status = '3' AND start_date < ?)";
        String query6 = "DELETE FROM api_live_score_prematch WHERE match_id IN (SELECT match_id FROM api_game WHERE status = '3' AND start_date < ?)";

        List<Object[]> batchArgs = new ArrayList<>();
        batchArgs.add(new Object[]{formattedDate});

        lsportJdbcTemplate.batchUpdate(query1, batchArgs);
        lsportJdbcTemplate.batchUpdate(query2, batchArgs);
        lsportJdbcTemplate.batchUpdate(query3, batchArgs);
        lsportJdbcTemplate.batchUpdate(query4, batchArgs);
        lsportJdbcTemplate.batchUpdate(query5, batchArgs);
        lsportJdbcTemplate.batchUpdate(query6, batchArgs);

    }



    /**
     * 기본 정산로직
     * 세틀멘트테이블에서 정산값을 가져온다
     * 베팅내역에 결과값을 넣는다
     *
     * 결과수정 시 로직
     * 세틀멘트 테이블에서 결과값을 수정한다
     * 배당 테이블에서 배당률과 결과값을 수정한다
     * 세틀멘트테이블에서 정산값을 가져온다
     *
     */


    @Scheduled(cron = "0/30 * * * * *") //30초마다 주기적인 반복
    public void rewardProcess() {

        String prematchSelectSql = "SELECT idx, settlement FROM api_odds WHERE settlement IN ('1','2','3','-1') AND is_modified = 'N'";
        List<Map<String, Object>> preRows = lsportJdbcTemplate.queryForList(prematchSelectSql);



        String inPlaySelectSql = "SELECT idx, settlement FROM api_odds_live WHERE settlement IN ('1','2','3','-1') AND is_modified = 'N'";
        List<Map<String, Object>> inPlayRows = lsportJdbcTemplate.queryForList(inPlaySelectSql);



        if (!preRows.isEmpty() || !inPlayRows.isEmpty()) {
            if (!preRows.isEmpty()) {
                // 프리매치 업데이트
                String preUpdateStatusSql = "UPDATE api_odds SET is_modified = 'Y' WHERE idx = ?";
                List<Odd> batchArgsForPreStatusUpdate = new ArrayList<>();

                for (Map<String, Object> row : preRows) {
                    String betIdx = (String) row.get("idx");
                    batchArgsForPreStatusUpdate.add(new Odd(betIdx));
                }

                lsportJdbcTemplate.batchUpdate(preUpdateStatusSql, batchArgsForPreStatusUpdate, batchArgsForPreStatusUpdate.size(),
                        (PreparedStatement ps, Odd settlement) -> {
                            ps.setString(1, settlement.getIdx());
                        });
            }

            if (!inPlayRows.isEmpty()) {
                // 인플레이 업데이트
                String inPlayUpdateStatusSql = "UPDATE api_odds_live SET is_modified = 'Y' WHERE idx = ?";
                List<OddLive> batchArgsForInPlayStatusUpdate = new ArrayList<>();

                for (Map<String, Object> row : inPlayRows) {
                    String betIdx = (String) row.get("idx");
                    batchArgsForInPlayStatusUpdate.add(new OddLive(betIdx));
                }

                lsportJdbcTemplate.batchUpdate(inPlayUpdateStatusSql, batchArgsForInPlayStatusUpdate, batchArgsForInPlayStatusUpdate.size(),
                        (PreparedStatement ps, OddLive settlement) -> {
                            ps.setString(1, settlement.getIdx());
                        });
            }

            String updateSql = "UPDATE bet_history SET settlement = ?, cron_api = 'Y' WHERE idx = ? ";
            List<BetHistory> batchArgs = new ArrayList<>();

            for (Map<String, Object> row : preRows) {
                String idx = (String) row.get("idx");
                String settlement = (String) row.get("settlement");
                batchArgs.add(new BetHistory(idx, settlement));
            }
            jdbcTemplate.batchUpdate(updateSql, batchArgs, batchArgs.size(),
                    (PreparedStatement ps, BetHistory order) -> {
                        ps.setString(1, order.getSettlement());
                        ps.setString(2, order.getIdx());
                    });


            String inPlayUpdateSql = "UPDATE bet_history SET settlement = ?, cron_api = 'Y' WHERE idx = ? ";
            List<BetHistory> inPlayBatchArgs = new ArrayList<>();

            for (Map<String, Object> row : inPlayRows) {
                String idx = (String) row.get("idx");
                String settlement = (String) row.get("settlement");
                inPlayBatchArgs.add(new BetHistory(idx, settlement));
            }
            jdbcTemplate.batchUpdate(inPlayUpdateSql, inPlayBatchArgs, inPlayBatchArgs.size(),
                    (PreparedStatement ps, BetHistory order) -> {
                        ps.setString(1, order.getSettlement());
                        ps.setString(2, order.getIdx());
                    });


            String orderSql = "SELECT settlement From bet_history WHERE settlement IS NOT NULL AND order_status = 'WAITING' ";
            List<Map<String, Object>> mainMethod = jdbcTemplate.queryForList(orderSql);

            String updateOrderSql = "UPDATE bet_history SET order_status = ?, processed_at = ? WHERE settlement = ?";
            List<BetHistory> updateList = new ArrayList<>();


            for (Map<String, Object> row : mainMethod) {
                String settlement = (String) row.get("settlement");
                LocalDateTime processedAt = LocalDateTime.now();
                OrderStatusEnum orderStatus = null;
                switch (settlement) {
                    case "1":
                        orderStatus = OrderStatusEnum.valueOf("FAIL");
                        break;
                    case "2":
                        orderStatus = OrderStatusEnum.valueOf("HIT");
                        break;
                    case "-1":
                        orderStatus = OrderStatusEnum.valueOf("CANCEL");
                        break;
                    case "3":
                        orderStatus = OrderStatusEnum.valueOf("CANCEL_HIT");
                        break;
                    default:
                        orderStatus = OrderStatusEnum.valueOf("NONE");
                        break;
                }
                updateList.add(new BetHistory(settlement, orderStatus, processedAt));
            }

            jdbcTemplate.batchUpdate(updateOrderSql, updateList, updateList.size(),
                    (PreparedStatement ps, BetHistory order) -> {
                        ps.setString(1, String.valueOf(order.getOrderStatus()));
                        ps.setString(2, String.valueOf(order.getProcessedAt()));
                        ps.setString(3, order.getSettlement());
                    });


            String rewardSql = "SELECT " +
                    "    bet_group_id, " +
                    "    user_id, " +
                    "    CASE " +
                    "        WHEN SUM(CASE WHEN order_status = 'CANCEL_HIT' THEN 1 ELSE 0 END) = COUNT(*) THEN " +
                    "            CAST(bet AS SIGNED) " +
                    "        WHEN SUM(CASE WHEN order_status IN ('HIT', 'CANCEL_HIT') THEN 1 ELSE 0 END) = COUNT(*) THEN " +
                    "            CASE " +
                    "                WHEN bet_fold_type IN ('SINGLE_FOLDER') THEN " +
                    "                    COALESCE(EXP(SUM(CASE WHEN order_status IN ('HIT') THEN LOG(price) ELSE 0 END)), 0) * bet " +
                    "                WHEN bet_fold_type IN ('THREE_FOLDER') THEN " +
                    "                    COALESCE(EXP(SUM(CASE WHEN order_status IN ('HIT') THEN LOG(price) ELSE 0 END)), 0) * 1.03 * bet " +
                    "                WHEN bet_fold_type IN ('FIVE_FOLDER') THEN " +
                    "                    COALESCE(EXP(SUM(CASE WHEN order_status IN ('HIT') THEN LOG(price) ELSE 0 END)), 0) * 1.05 * bet " +
                    "                WHEN bet_fold_type IN ('SEVEN_FOLDER') THEN " +
                    "                    COALESCE(EXP(SUM(CASE WHEN order_status IN ('HIT') THEN LOG(price) ELSE 0 END)), 0) * 1.07 * bet " +
                    "                ELSE 0 " +
                    "            END " +
                    "        ELSE 0 " +
                    "    END AS bet_reward " +
                    "FROM " +
                    "    bet_history " +
                    "WHERE " +
                    "    processed_at IS NOT NULL " +
                    "    AND settlement IS NOT NULL " +
                    "    AND bet_reward IS NULL " +
                    "    AND bet_group_id NOT IN (SELECT bet_group_id FROM bet_history WHERE order_status = 'FAIL' OR order_status = 'WAITING' ) " +
                    "GROUP BY " +
                    "    bet_group_id, user_id, bet_fold_type ";
            List<Map<String, Object>> rewards = jdbcTemplate.queryForList(rewardSql);
            String updateRewardSql = "UPDATE bet_history SET bet_reward = ? WHERE bet_group_id = ?";
            List<BetHistory> updateRewardList = new ArrayList<>();


            for (Map<String, Object> row : rewards) {
                double betReward = ((Number) row.get("bet_reward")).doubleValue();
                Long betGroupId = (Long) row.get("bet_group_id");
                updateRewardList.add(BetHistory.insertBetReward(String.valueOf(betReward), betGroupId));
            }
            jdbcTemplate.batchUpdate(updateRewardSql, updateRewardList, updateRewardList.size(),
                    (PreparedStatement ps, BetHistory reward) -> {
                        ps.setLong(2, reward.getBetGroupId());
                        ps.setString(1, reward.getBetReward());
                    });


            LinkedList<Wallet> walletsToUpdate = new LinkedList<>();
            for (Map<String, Object> row : rewards) {
                Long userId = ((Number) row.get("user_id")).longValue();
                double betReward = ((Number) row.get("bet_reward")).doubleValue();
                Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new EntityNotFoundException("Wallet not found for user id: " + userId));
                long currentSportsBalance = wallet.getSportsBalance();
                long updatedSportsBalance = currentSportsBalance + (long) betReward;
                wallet.setSportsBalance(updatedSportsBalance);
                walletsToUpdate.add(wallet);
            } walletRepository.saveAll(walletsToUpdate);

            // 결과 수정 시 로그 저장용 쿼리
            String betHistorySql = "SELECT DISTINCT " +
                    "bet_history_id, " +
                    "bet_history.user_id, " +
                    "users.username, " +
                    "users.nickname, " +
                    "bet_history.bet_group_id, " +
                    "bet_history.bet_reward, " +
                    "wallet.sports_balance, " +
                    "(bet_history.bet_reward + wallet.sports_balance) AS total_balance " +
                    "FROM bet_history " +
                    "JOIN wallet ON bet_history.user_id = wallet.user_id " +
                    "JOIN users ON bet_history.user_id = users.user_id " +
                    "WHERE bet_history.api = 'Y' OR bet_history.cron_api = 'Y' ";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(betHistorySql);


            Map<Long, Map<String, Object>> resultMap = new HashMap<>();
            for (Map<String, Object> item : list) {
                Long betGroupId = (Long) item.get("bet_group_id");
                String betReward = (String) item.get("bet_reward");
                if (!resultMap.containsKey(betGroupId) && (betReward != null && !betReward.equals("0"))) {
                    resultMap.put(betGroupId, item);
                }
            }

            String insertSql = "INSERT INTO money_log (user_id, username, nickname, used_sports_balance, final_sports_balance, bigo, category, site, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CONVERT_TZ(NOW(), @@session.time_zone, '+09:00'), CONVERT_TZ(NOW(), @@session.time_zone, '+09:00'))";

            LinkedList<Object[]> batchInsert = new LinkedList<>();
            for (Map.Entry<Long, Map<String, Object>> entry : resultMap.entrySet()) {
                Long betGroupId = entry.getKey();
                Map<String, Object> row = entry.getValue();
                String betReward = row.get("bet_reward").toString();
                String totalBalance = row.get("total_balance").toString();
                String bigo = betGroupId + "(SPORT)";
                batchInsert.add(new Object[]{
                        row.get("user_id"),
                        row.get("username"),
                        row.get("nickname"),
                        betReward,
                        totalBalance,
                        bigo,
                        "당첨",
                        "test"
                });
            }
            jdbcTemplate.batchUpdate(insertSql, batchInsert);

            String updateApiSql = "UPDATE bet_history SET api = 'N', cron_api = 'N' WHERE bet_history_id = ? ";
            List<Object[]> batchUpdate = new ArrayList<>();
            for (Map<String, Object> row : resultMap.values()) {
                Long betHistoryId = (Long) row.get("bet_history_id");
                batchUpdate.add(new Object[]{betHistoryId});
            } jdbcTemplate.batchUpdate(updateApiSql, batchUpdate);
        }
    }
}
