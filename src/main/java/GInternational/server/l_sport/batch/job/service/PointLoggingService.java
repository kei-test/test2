package GInternational.server.l_sport.batch.job.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional(transactionManager = "multiTransactionManager")
public class PointLoggingService {

    private static final Logger logger = LoggerFactory.getLogger(PointLoggingService.class);

    private final JdbcTemplate jdbcTemplate;

    public PointLoggingService(@Qualifier("jdbcTemplate") JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(cron = "0/15 * * * * *")
    public void failBetBonusProcess() {
        logger.info("Starting failBetBonusProcess");

        LinkedList<Object[]> userBatchArgs = new LinkedList<>();
        LinkedList<Object[]> userWalletArgs = new LinkedList<>();
        LinkedList<Object[]> refBatchArgs = new LinkedList<>();
        LinkedList<Object[]> refWalletArgs = new LinkedList<>();
        LinkedList<Object[]> updateBatchArgsWithReferer = new LinkedList<>();
        LinkedList<Object[]> updateBatchArgsWithoutReferer = new LinkedList<>();
        Set<Long> processedBetGroupIds = new HashSet<>();

        List<Map<String, Object>> selectRows = jdbcTemplate.queryForList(
                "SELECT bh.bet_history_id," +
                        " bh.bet_group_id," +
                        " bh.user_id," +
                        " bh.bet," +
                        " bh.order_status," +
                        " u.referred_by," +
                        " u.lv,u.ip," +
                        " u.username," +
                        " u.nickname," +
                        " lbp.loss_amount," +
                        " lbp.referrer_loss_amount, " +
                        " w.point " +
                        "FROM bet_history bh " +
                        "JOIN users u ON bh.user_id = u.user_id " +
                        "JOIN level_bonus_point_setting lbp ON u.lv = lbp.lv " +
                        "JOIN wallet w ON u.user_id = w.user_id " +
                        "WHERE bh.fail_bonus_col = 'N' " +
                        "AND bh.bet_type NOT IN ('PRE_MATCH','IN_PLAY')" +
                        "AND bh.bet_fold_type NOT IN ('SINGLE FOLDER')" +
                        "AND bh.order_status = 'FAIL' " +
                        "AND bh.bet_history_id = (" +
                        "    SELECT MIN(bh_inner.bet_history_id) " +
                        "    FROM bet_history bh_inner " +
                        "    WHERE bh_inner.fail_bonus_col = 'N' " +
                        "    AND bh.bet_type NOT IN ('PRE_MATCH','IN_PLAY')" +
                        "    AND bh.bet_fold_type NOT IN ('SINGLE FOLDER')" +
                        "    AND bh_inner.order_status = 'FAIL' " +
                        "    AND bh_inner.bet_group_id = bh.bet_group_id" +
                        ") " +
                        "ORDER BY bh.bet_start_time DESC");

        for (Map<String, Object> row : selectRows) {
            Long betGroupId = (Long) row.get("bet_group_id");

            if (processedBetGroupIds.contains(betGroupId)) {
                continue;
            }

            processedBetGroupIds.add(betGroupId);

            // 유저 정보
            Long userId = ((Number) row.get("user_id")).longValue();
            String ip = (String) row.get("ip");
            String username = (String) row.get("username");
            String nickname = (String) row.get("nickname");
            String referredBy = (String) row.get("referred_by");

            // 지갑 정보
            Long point = ((Number) row.get("point")).longValue();

            // 보너스 세팅 정보
            Double userBonus = (Double) row.get("loss_amount");
            Double referrerBonus = (Double) row.get("referrer_loss_amount");

            // 베팅 금액 정보
            long betAmount = Long.parseLong((String) row.get("bet"));

            // 적립 포인트
            Double userBonusAmount = betAmount * (userBonus / 100.0);
            long convertUserBonusAmount = userBonusAmount.longValue();

            // 최종 포인트
            Long finalUserPoint = point + convertUserBonusAmount;

            // 지갑 정보 업데이트 리스트
            userWalletArgs.add(new Object[]{
                    convertUserBonusAmount,
                    userId,
            });

            // 포인트 로그 저장 리스트
            userBatchArgs.add(new Object[]{
                    userId,
                    username,
                    nickname,
                    ip,
                    convertUserBonusAmount,
                    finalUserPoint
            });

            Map<String, Object> refererInfo = null;
            try {
                refererInfo = jdbcTemplate.queryForMap(
                        "SELECT u.user_id," +
                                " u.username," +
                                " u.nickname," +
                                " u.ip," +
                                " w.point " +
                                "FROM users u " +
                                "JOIN wallet w ON w.user_id = u.user_id " +
                                "WHERE username = ? AND u.partner_type IS NULL ",
                        new Object[]{referredBy}
                );
            } catch (EmptyResultDataAccessException e) {
                updateBatchArgsWithoutReferer.add(new Object[]{betGroupId});
                continue;
            }

            Long refererUserId = (Long) refererInfo.get("user_id");
            String refererUsername = (String) refererInfo.get("username");
            String refererNickname = (String) refererInfo.get("nickname");
            String refIp = (String) refererInfo.get("ip");
            Long refPoint = ((Number) refererInfo.get("point")).longValue();

            Double referrerBonusAmount = betAmount * (referrerBonus / 100.0);
            long convertReferrerBonusAmount = referrerBonusAmount.longValue();
            Long finalRefPoint = refPoint + convertReferrerBonusAmount;

            refWalletArgs.add(new Object[]{
                    convertReferrerBonusAmount,
                    refererUserId
            });

            refBatchArgs.add(new Object[]{
                    refererUserId,
                    refererUsername,
                    refererNickname,
                    refIp,
                    convertReferrerBonusAmount,
                    finalRefPoint
            });

            updateBatchArgsWithReferer.add(new Object[]{betGroupId});
        }

        // 지갑 업데이트 배치
        jdbcTemplate.batchUpdate(
                "UPDATE wallet SET point = point + ? WHERE user_id = ?",
                userWalletArgs
        );

        jdbcTemplate.batchUpdate(
                "UPDATE wallet SET point = point + ? WHERE user_id = ?",
                refWalletArgs
        );

        String insertUserPointSql = "INSERT INTO point_log (user_id, username, nickname, point, final_point, category, memo, ip, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CONVERT_TZ(NOW(), @@session.time_zone, '+09:00'))";

        LinkedList<Object[]> batchInsertUserPointLog = new LinkedList<>();
        for (Object[] args : userBatchArgs) {
            Long logUserId = (Long) args[0];
            String logUsername = (String) args[1];
            String logNickname = (String) args[2];
            String logIp = (String) args[3];
            Long logPoint = (Long) args[4];
            Long logFinalPoint = (Long) args[5];

            Object[] logData = new Object[]{
                    logUserId,
                    logUsername,
                    logNickname,
                    logPoint,
                    logFinalPoint,
                    "낙첨포인트",
                    null,
                    logIp
            };
            batchInsertUserPointLog.add(logData);
        }
        jdbcTemplate.batchUpdate(insertUserPointSql, batchInsertUserPointLog);

        String insertRefPointSql = "INSERT INTO point_log (user_id, username, nickname, point, final_point, category, memo, ip, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CONVERT_TZ(NOW(), @@session.time_zone, '+09:00'))";

        LinkedList<Object[]> batchInsertRefPointLog = new LinkedList<>();
        for (Object[] args : refBatchArgs) {
            Long logUserId = (Long) args[0];
            String logUsername = (String) args[1];
            String logNickname = (String) args[2];
            String logIp = (String) args[3];
            Long logPoint = (Long) args[4];
            Long logFinalPoint = (Long) args[5];

            Object[] logData = new Object[]{
                    logUserId,         // user_id
                    logUsername,       // username
                    logNickname,       // nickname
                    logPoint,          // point
                    logFinalPoint,     // final_point
                    "추천인낙첨포인트",     // category
                    null,              // memo
                    logIp              // ip
            };

            batchInsertRefPointLog.add(logData);
        }
        jdbcTemplate.batchUpdate(insertRefPointSql, batchInsertRefPointLog);

        // bet_history 업데이트 배치
        jdbcTemplate.batchUpdate("UPDATE bet_history SET fail_bonus_col = 'Y' WHERE bet_group_id = ?", updateBatchArgsWithoutReferer);
        jdbcTemplate.batchUpdate("UPDATE bet_history SET fail_bonus_col = 'Y' WHERE bet_group_id = ?", updateBatchArgsWithReferer);

        logger.info("Completed failBetBonusProcess");
    }
}























