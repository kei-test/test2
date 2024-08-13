//package GInternational.server.l_sport.lsports.info;
//
//
//import GInternational.server.l_sport.lsports.pack.sport.Sport;
//import GInternational.server.l_sport.lsports.pack.sport.SportRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//import org.springframework.stereotype.Repository;
//import org.springframework.stereotype.Service;
//
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Map;
//
//@Repository
//@RequiredArgsConstructor
//public class JdbcTemplates {
//
//
//    private final JdbcTemplate jdbcTemplate;
//
//
//    //단건 객체 조회
//    public Sport findBySport(String name) {
//
//        String sql = "SELECT * FROM wind_pre_sport where name = ?";
//
//        RowMapper<Sport> sportRowMapper = new RowMapper<Sport>() {
//            @Override
//            public Sport mapRow(ResultSet rs, int rowNum) throws SQLException {
//                Sport sport = new Sport();
//                sport.setId(rs.getLong("wind_pre_sport_id"));
//                sport.setSportId(rs.getLong("sport_id"));
//                sport.setName(rs.getString("name"));
//                return sport;
//            }
//        };
//        Sport sport = jdbcTemplate.queryForObject(sql,sportRowMapper,name);
//        return sport;
//    }
//
//
//
//    public List<Sport> findAllSports() {
//        String sql = "SELECT * FROM wind_pre_sport";
//
//        RowMapper<Sport> sportRowMapper = new RowMapper<Sport>() {
//            @Override
//            public Sport mapRow(ResultSet rs, int rowNum) throws SQLException {
//                Sport sport = new Sport();
//                sport.setId(rs.getLong("wind_pre_sport_id"));
//                sport.setSportId(rs.getLong("sport_id"));
//                sport.setName(rs.getString("name"));
//                return sport;
//            }
//        };
//
//        return jdbcTemplate.query(sql,sportRowMapper);
//    }
//
//
//
//    public List<Map<String, Long>> findAllSettlement() {
//        String sql = "SELECT bet_id, bet_settlement FROM wind_bet where ";
//
//        RowMapper<Map<String, Long>> rowMapper = (rs, rowNum) -> {
//            Map<String, Long> resultMap = Map.of(
//                    "bet_id", rs.getLong("bet_id"),
//                    "bet_settlement", rs.getLong("bet_settlement")
//            );
//            return resultMap;
//        };
//        return jdbcTemplate.query(sql,rowMapper);
//    }
//
//
//
//    //특정 열의 데이터만 가져온다
//    public List<String> findAllSportNames() {
//        String sql = "SELECT name FROM wind_pre_sport";
//        return jdbcTemplate.queryForList(sql, String.class);
//    }
//
//
////    public String findSportNameById(String name) {
////        String sql = "SELECT * FROM wind_pre_sport WHERE name = ?";
////        return jdbcTemplate.queryForObject(sql, String.class, name);
////    }
//}
//
