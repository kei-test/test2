//package GInternational.server.l_sport.snapshot.pre.bet;
//
//import GInternational.server.l_sport.snapshot.pre.bet.entity.Bet;
//import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.sql.PreparedStatement;
//import java.sql.Types;
//import java.util.List;
//
//@Repository
//@RequiredArgsConstructor
//public class BulkService {
//
//
//    private final JdbcTemplate jdbcTemplate;
//
//    @Transactional
//    public void saveAll(List<Bet> bulkBet) {
//        String betSql = "INSERT INTO wind_bet (fixture_id, market_id, bet_id, bet_name, bet_player_name, bet_player_id, bet_start_price, bet_price, bet_provider_bet_id, bet_last_update, bet_line, bet_base_line, bet_status, bet_participant_id, bet_update_status) " +
//                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, )" +
//                "ON DUPLICATE KEY UPDATE " +
//                "bet_name = VALUES(bet_name), " +
//                "bet_player_name = VALUES(bet_player_name), " +
//                "bet_player_id = VALUES(bet_player_id), " +
//                "bet_start_price = VALUES(bet_start_price), " +
//                "bet_price = VALUES(bet_price), " +
//                "bet_provider_bet_id = VALUES(bet_provider_bet_id), " +
//                "bet_last_update = VALUES(bet_last_update), " +
//                "bet_line = VALUES(bet_line), " +
//                "bet_base_line = VALUES(bet_base_line), " +
//                "bet_status = VALUES(bet_status), " +
//                "bet_participant_id = VALUES(bet_participant_id), " +
//                "bet_update_status = VALUES(bet_update_status)";
//
//        jdbcTemplate.batchUpdate(betSql, bulkBet, bulkBet.size(),
//                (PreparedStatement ps, Bet bet) -> {
//                    ps.setLong(1, bet.getFixtureId());
//                    ps.setLong(2, bet.getMarketId());
//                    ps.setLong(3, bet.getBetId());
//                    ps.setString(4, bet.getBetName());
//                    ps.setString(5, bet.getBetPlayerName());
//                    ps.setString(6, bet.getBetPlayerId());
//                    ps.setString(7, bet.getBetStartPrice());
//                    ps.setString(8, bet.getBetPrice());
//                    ps.setString(9, bet.getBetProviderBetId());
//                    ps.setTimestamp(10, bet.getBetLastUpdate());
//                    ps.setString(11, bet.getBetLine());
//                    ps.setString(12, bet.getBetBaseLine());
//                    ps.setLong(13, bet.getBetStatus());
//
//                    // Set bet_participant_id allowing null
//                    if (bet.getBetParticipantId() != null) {
//                        ps.setLong(14, bet.getBetParticipantId());
//                    } else {
//                        ps.setNull(14, Types.BIGINT);
//                    }
//
//
//                    ps.setString(15, bet.getBetUpdateStatus());
//                }
//        );
//    }
//}