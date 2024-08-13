package GInternational.server.l_sport.info.repository;





import GInternational.server.api.entity.QBetHistory;
import GInternational.server.l_sport.batch.job.dto.edit.EditMatchResultDTO;
import GInternational.server.l_sport.batch.job.dto.order.MatchScoreDTO;
import GInternational.server.l_sport.info.dto.admin.AdminPreMatchDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetFixtureDTO;
import GInternational.server.l_sport.info.dto.results.GameResultListDTO;
import GInternational.server.l_sport.info.dto.results.GameResultResponseDTO;
import GInternational.server.l_sport.info.dto.inplay.InPlayGetFixtureDTO;


import GInternational.server.l_sport.info.entity.QMatch;
import GInternational.server.l_sport.info.entity.QOddLive;
import GInternational.server.l_sport.info.entity.QSettlement;
import GInternational.server.l_sport.info.entity.QSettlementPrematch;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static GInternational.server.api.entity.QBetHistory.*;
import static GInternational.server.l_sport.info.entity.QMatch.match;
import static GInternational.server.l_sport.info.entity.QOdd.odd;
import static GInternational.server.l_sport.info.entity.QOddLive.oddLive;
import static GInternational.server.l_sport.info.entity.QSettlement.*;
import static GInternational.server.l_sport.info.entity.QSettlementPrematch.*;



@Repository
@RequiredArgsConstructor
public class FixtureRepositoryImpl implements FixtureRepositoryCustom {


    @Autowired
    @Qualifier("lsportEntityManager")  //각각의 엔티티매니저를 지정하여 주입한다.
    private final JPAQueryFactory queryFactory;



    @Override
    public List<PreMatchGetFixtureDTO> getPreMatchFixtures(String sportName,String startDate,String endDate) {
        List<PreMatchGetFixtureDTO> results = queryFactory.select(Projections.constructor(PreMatchGetFixtureDTO.class,
                        match.matchId,
                        match.leagueName,
                        match.locationName,
                        match.sportsName,
                        match.status,
                        match.homeName,
                        match.awayName,
                        match.leagueId,
                        match.startDate,
                        odd.marketId,
                        odd.marketName,
                        odd.idx,
                        odd.betName,
                        odd.line,
                        odd.baseLine,
                        odd.price,
                        odd.lastUpdate,
                        odd.betStatus,
                        match.isPrematch,
                        match.isLive))
                .from(match)
                .join(odd).on(odd.matchId.eq(match.matchId))
                .where((match.startDate.between(startDate, endDate))
                        .and(odd.marketName.in("1X2", "12 Including Overtime", "12"))
                        .and(match.status.in("1","9"))
                        .and(odd.betStatus.notIn("3"))
                        .and(sportNameEq(sportName)))
                .fetch();
        return results;
    }




    //더보기
    @Override
    public List<PreMatchGetFixtureDTO> getPreMatchFixturesDetail(String matchId,String type,String startDate,String endDate) {
        BooleanExpression condition = null;
        if (type.equals("Main")) {
            condition = odd.marketName.in(
                            //축구
                            "1X2", "12", "Under/Over",
                            "Asian Handicap",
                            "Odd/Even",
                            "Double Chance",
                            "European Handicap",
                            "Both Teams To Score",
                            //야구
                            "12 Including Overtime",
                            "Under/Over Including Overtime",
                            "Asian Handicap Including Overtime",
                            "Odd/Even Including Overtime",
                            "Correct Score Including Overtime",
                            //농구
                            "Correct Score",
                            "Asian Under/Over",
                            "European Handicap Including Overtime",
                            //하키 메인마켓은 위 내용과 동일함
                            //배구
                            "Asian Handicap Sets");
        } else if (type.equals("Period")) {
            condition = odd.marketName.in("Correct Score 1st Period",
                    "Under/Over 1st Period",
                    "1st Period Winner",
                    "2nd Period Winner",
                    "3rd Period Winner",
                    "4th Period Winner",
                    "Under/Over 2nd Period",
                    "Under/Over 3rd Period",
                    "Under/Over 4th Period",
                    "First Team To Score 1st Period",
                    "Asian Handicap 1st Period",
                    "Asian Handicap 2nd Period",
                    "Asian Handicap 3rd Period",
                    "Asian Handicap 4th Period",
                    "1st Period Odd/Even",
                    "2nd Period Odd/Even",
                    "3rd Period Odd/Even",
                    "4th Period Odd/Even",
                    "Under/Over 1st Period - Home Team",
                    "Under/Over 2nd Period - Home Team",
                    "Under/Over 1st Period - Away Team",
                    "Under/Over 2nd Period - Away Team",
                    "1st Period Winner Home/Away",
                    "2nd Period Winner Home/Away",
                    "3rd Period Winner Home/Away",
                    "4th Period Winner Home/Away",
                    "Under/Over 1st Period - Home Team",
                    "Under/Over 2nd Period - Home Team",
                    "Under/Over 3rd Period - Home Team",
                    "Under/Over 4th Period - Home Team",
                    "Under/Over 1st Period - Away Team",
                    "Under/Over 2nd Period - Away Team",
                    "Under/Over 3rd Period - Away Team",
                    "Under/Over 4th Period - Away Team",
                    "Under/Exactly/Over - 1st Period",
                    "Under/Exactly/Over - 2nd Period",
                    "Under/Exactly/Over - 3rd Period",
                    "Under/Exactly/Over - 4th Period",
                    "1st Period Race To",
                    "2nd Period Race To",
                    "3rd Period Race To",
                    "4th Period Race To",
                    "1st Period Both Teams To Score",
                    "2nd Period Both Teams To Score",
                    "3rd Period Both Teams To Score",
                    "Double Chance 1st Period",
                    "Double Chance 2nd Period",
                    "Double Chance 3rd Period",
                    "Correct Score 1st Period",
                    "Correct Score 2nd Period",
                    "Correct Score 3rd Period",
                    "Both Teams To Score 1st Half",
                    "Both Teams To Score 2nd Half",
                    "Away Team Number Of Goals In 1st Half",
                    "Away Team to Score 2nd Half",
                    "Home Team Number Of Goals In 1st Half",
                    "Home Team to Score 2nd Half",
                    "Under/Exactly/Over - 1st Period",
                    "Under/Exactly/Over - 2nd Period",
                    "Double Chance 1st Period",
                    "Double Chance 2nd Period",
                    "1X2 Offsides 1st Period",
                    "1X2 Offsides 2nd Period",
                    "Highest Scoring Period",
                    "European Handicap 1st Period",
                    "European Handicap 2nd Period",
                    "1X2 Offsides 1st Period",
                    "1X2 Offsides 2nd Period",
                    "Under/Over Offsides Home Team 1st Period",
                    "Under/Over Offsides Away Team 1st Period");
        } else if (type.equals("Optional")) {
            condition = odd.marketName.in("First Team To Score",
                    "Asian Handicap Halftime",
                    "Under/Over Halftime",
                    "Asian Handicap Sets",
                    "Away Team to Score",
                    "Home Team to Score",
                    "Last Team To Score",
                    "First Team To Score 2nd Half",
                    "Away Team Win To Nil",
                    "Home Team Win To Nil",
                    "Home Team To Keep A Clean Sheet",
                    "Away Team To Keep A Clean Sheet",
                    "Under/Over Offsides",
                    "Under/Over Throw-Ins",
                    "Odd/Even - Home Team",
                    "Odd/Even - Away Team",
                    "Both Teams To Score In Both Halves",
                    "Under/Exactly/Over",
                    "Under/Over Offsides - Home Team",
                    "Under/Over Offsides - Away Team",
                    "1X2 Offsides",
                    "Will The First Scoring Team Win?",
                    "Away Team Number Of Goals",
                    "Under/Over - Home Team",
                    "Under/Over - Away Team",
                    "Under/Over - Away Team Including Overtime",
                    "Under/Over - Home Team Including Overtime",
                    "Race To");
        }
        List<PreMatchGetFixtureDTO> results = queryFactory
                .select(Projections.constructor(PreMatchGetFixtureDTO.class,
                        match.matchId,
                        match.leagueName,
                        match.locationName,
                        match.sportsName,
                        match.status,
                        match.homeName,
                        match.awayName,
                        match.leagueId,
                        match.startDate,
                        odd.marketId,
                        odd.marketName,
                        odd.idx,
                        odd.betName,
                        odd.line,
                        odd.baseLine,
                        odd.price,
                        odd.lastUpdate,
                        odd.betStatus,
                        match.isPrematch,
                        match.isLive))
                .from(match)
                .join(odd).on(odd.matchId.eq(match.matchId))
                .where((condition.and(match.startDate.between(startDate, endDate)))
                        .and(match.startDate.goe(startDate))
                        .and(match.status.in("1","9"))
                        .and(odd.betStatus.in("1", "2"))
                        .and(matchIdEq(matchId)))
                .fetch();
        return results;
    }



    @Override
    public List<Tuple> getGameCount(Long type,String startDate,String endDate) {
        // 조건을 동적으로 생성하기 위한 BooleanExpression
        BooleanExpression condition = null;

        // type에 따라 다른 조건 추가
        if (type == 1) {
            // 인플레이
            condition = oddLive.marketName.in("1X2", "12 Including Overtime", "12");  // 추후 추가예정
        } else if (type == 2) {
            // 프리매치
            condition = odd.marketName.in("1X2", "12 Including Overtime", "12").and((match.status.in("1","9"))).and(odd.betStatus.in("1", "2"));  // 추후 추가예정
        } else if (type == 4) {
            // 크로스 (fix)
            condition = odd.marketName.in("1X2", "12 Including Overtime", "12", "Under/Over", "Asian Handicap","Under/Over Including Overtime", "Asian Handicap Including Overtime").and((match.status.in("1","9"))).and(odd.betStatus.in("1", "2"));
        } else if (type == 5) { // 여기에 더 많은 조건을 추가할 수 있습니다.
            // 승무패 (fix)
            condition = odd.marketName.in("1X2", "12 Including Overtime", "12").and((match.status.in("1","9"))).and(odd.betStatus.in("1", "2"));
        } else if (type == 6) {
            // 핸디캡 (fix)
            condition = odd.marketName.in("Under/Over", "Asian Handicap","Under/Over Including Overtime", "Asian Handicap Including Overtime").and((match.status.in("1","9"))).and(odd.betStatus.in("1", "2"));
        } else if (type == 7) {
            // 스페셜1
            condition = odd.marketName.in("1st Period Odd/Even", "Asian Handicap 1st Period", "Under/Over 1st Period","1st 5 Innings Winner - 12")
                    .and((match.leagueName.in( "Bundesliga", "LaLiga", "Serie A", "Ligue 1", "Premier League",  //축구
                            "NHL",               //아이스 하키
                            "V-League - Women",  //여자 배구
                            "V-League - Men",    //남자 배구
                            "WKBL W",            //여자 농구
                            "NBA","WNBA","KBL","WKBL"//농구
                                      ))).and((match.status.in("1","9"))).and(odd.betStatus.in("1", "2"));
        } else if (type == 8) {
            // 스페셜2
            condition = odd.marketName.in("Asian Handicap Halftime", "Under/Over Halftime", "Asian Handicap Sets")
                    .and((match.leagueName.in("Bundesliga", "LaLiga", "Serie A", "Ligue 1", "Premier League",  //축구
                            "NHL",               //아이스 하키
                            "V-League - Women",  //여자 배구
                            "V-League - Men",    //남자 배구
                            "WKBL W",            //여자 농구
                            "NBA","WNBA","KBL","WKBL"//농구
                    ))).and((match.status.in("1","9"))).and(odd.betStatus.in("1", "2"));
        }
        JPQLQuery<Tuple> query = null;

        if (type == 1) {
            query = queryFactory
                    .select(match.sportsName, match.sportsName.count())
                    .from(match)
                    .join(oddLive).on(oddLive.matchId.eq(match.matchId))
                    .where(condition
                            .and((match.sportsName.in("Football", "Ice Hockey", "Basketball", "volleyball", "Baseball")))
                            .and((match.status.in("2"))))
                    .groupBy(match.sportsName)
                    .distinct();

        } else {
            query = queryFactory
                    .select(match.sportsName, match.sportsName.count())
                    .from(match)
                    .join(odd).on(odd.matchId.eq(match.matchId))
                    .where((match.startDate.between(startDate, endDate))
                            .and((condition)))
                    .groupBy(match.sportsName)
                    .distinct();
        }
        return query.fetch();
    }



    @Override
    public List<PreMatchGetFixtureDTO> getSport(Long type,String startDate,String endDate) {
        BooleanExpression condition = null;

        if (type == 4) {
            // 크로스
            condition = (odd.marketName.in("1X2", "12 Including Overtime", "12", "Under/Over", "Asian Handicap","Under/Over Including Overtime", "Asian Handicap Including Overtime"))
                    .and((match.status.in("1","9")));
        } else if (type == 5) { // 여기에 더 많은 조건을 추가할 수 있습니다.
            // 승무패
            condition = (odd.marketName.in("1X2", "12 Including Overtime", "12"))
                    .and((match.status.in("1","9")));
        } else if (type == 6) {
            // 핸디캡
            condition = (odd.marketName.in("Under/Over", "Asian Handicap","Under/Over Including Overtime", "Asian Handicap Including Overtime"))
                    .and((match.status.in("1","9")));
        } else if (type == 7) {
            // 스페셜1
            condition = odd.marketName.in("1st Period Odd/Even", "Asian Handicap 1st Period", "Under/Over 1st Period","1st 5 Innings Winner - 12")
                    .and((match.leagueName.in( "Bundesliga", "LaLiga", "Serie A", "Ligue 1", "Premier League",  //축구
                            "NHL",                    //아이스 하키
                            "V-League - Women",       //여자 배구
                            "V-League - Men",         //남자 배구
                            "WKBL W",                 //여자 농구
                            "NBA","WNBA","KBL","WKBL" //농구
                    ))).and((match.status.in("1","9")));
        } else if (type == 8) {
            // 스페셜2
            condition = odd.marketName.in("Asian Handicap Halftime", "Under/Over Halftime", "Asian Handicap Sets")
                    .and((match.leagueName.in("Bundesliga", "LaLiga", "Serie A", "Ligue 1", "Premier League",  //축구
                            "NHL",                    //아이스 하키
                            "V-League - Women",       //여자 배구
                            "V-League - Men",         //남자 배구
                            "WKBL W",                 //여자 농구
                            "NBA","WNBA","KBL","WKBL" //농구
                    ))).and((match.status.in("1","9")));
        }
        return queryFactory
                .select(Projections.constructor(PreMatchGetFixtureDTO.class,
                        match.matchId,
                        match.leagueName,
                        match.locationName,
                        match.sportsName,
                        match.status,
                        match.homeName,
                        match.awayName,
                        match.leagueId,
                        match.startDate,
                        odd.marketId,
                        odd.marketName,
                        odd.idx,
                        odd.betName,
                        odd.line,
                        odd.baseLine,
                        odd.price,
                        odd.lastUpdate,
                        odd.betStatus,
                        match.isPrematch,
                        match.isLive))
                .from(match)
                .join(odd).on(odd.matchId.eq(match.matchId))
                .where((match.startDate.between(startDate, endDate)) //변경함
                        .and(condition)
                        .and(odd.betStatus.in("1","2")))
                .fetch();
    }

    //승무패, 핸디캡, 스페셜, 스페셜2 경기결과
    @Override
    public List<GameResultListDTO> getGameResultList(Long type,String formattedEndTime,String formattedStartTime) {
        BooleanExpression condition = null;

        // type에 따라 다른 조건 추가
        if (type == 4) {
            // 크로스
            condition = odd.marketName.in("1X2", "12 Including Overtime", "12", "Under/Over", "Asian Handicap","Under/Over Including Overtime", "Asian Handicap Including Overtime");
        } else if (type == 5) {
            // 승무패 (승패,승무패)
            condition = odd.marketName.in("1X2", "12 Including Overtime", "12");
        } else if (type == 6) {
            // 핸디캡
            condition = odd.marketName.in("Under/Over", "Asian Handicap","Under/Over Including Overtime", "Asian Handicap Including Overtime");
        } else if (type == 7) {
            // 스페셜1
            condition = odd.marketName.in("1st Period Odd/Even", "Asian Handicap 1st Period", "Under/Over 1st Period", "1st 5 Innings Winner - 12")
                    .and((match.leagueName.in("Bundesliga", "LaLiga", "Serie A", "Ligue 1", "Premier League",  //축구
                            "NHL",               //아이스 하키
                            "V-League - Women",  //여자 배구
                            "V-League - Men",    //남자 배구
                            "WKBL W",            //여자 농구
                            "NBA","WNBA","KBL","WKBL"//농구
                    )));
        } else if (type == 8) {
            // 스페셜2
            condition = odd.marketName.in("Asian Handicap Halftime", "Under/Over Halftime", "Asian Handicap Sets")
                    .and((match.leagueName.in("Bundesliga", "LaLiga", "Serie A", "Ligue 1", "Premier League",  //축구
                            "NHL",               //아이스 하키
                            "V-League - Women",  //여자 배구
                            "V-League - Men",    //남자 배구
                            "WKBL W",            //여자 농구
                            "NBA","WNBA","KBL","WKBL"//농구
                    )));
        }

        List<GameResultListDTO> results = queryFactory
                .select(Projections.constructor(GameResultListDTO.class,
                        match.matchId,
                        match.leagueName,
                        match.locationName,
                        match.sportsName,
                        match.status,
                        match.homeName,
                        match.homeScore,
                        match.awayName,
                        match.awayScore,
                        match.period1Home,
                        match.period1Away,
                        match.period2Home,
                        match.period2Away,
                        odd.marketId,
                        odd.marketName,
                        odd.idx,
                        odd.betName,
                        odd.baseLine,
                        odd.price,
                        odd.settlement,
                        match.startDate
                        ))
                .from(match)
                .join(odd).on(odd.matchId.eq(match.matchId))
                .where((condition)
                        .and(match.isPrematch.in("1"))
                        .and(match.status.in("3"))
                        .and(odd.betStatus.in("3"))
                        .and((match.startDate.between(formattedEndTime,formattedStartTime))))
                .fetch();
        return results;
    }


    //인플레이, 프리매치 경기결과
    @Override
    public Page<GameResultResponseDTO> getGameResult(Long type,String formattedEndTime,String formattedStartTime, Pageable pageable) {
        // 프리매치
        if (type == 2) {
            List<GameResultResponseDTO> results = queryFactory
                    .select(Projections.constructor(GameResultResponseDTO.class,
                            match.leagueName,
                            match.locationName,
                            match.sportsName,
                            match.startDate,
                            match.status,
                            match.homeName,
                            match.homeScore,
                            match.awayName,
                            match.awayScore))
                    .from(match)
                    .where(match.status.eq("3")
                            .and(match.isPrematch.in("1"))
                            .and((match.startDate.between(formattedEndTime,formattedStartTime))))
                    .orderBy(match.startDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            long totalElements = queryFactory.select(match.count())
                    .from(match)
                    .where(match.status.eq("3")
                            .and(match.isPrematch.in("1"))
                            .and((match.startDate.between(formattedEndTime,formattedStartTime))))
                    .fetchOne();

            return new PageImpl<>(results, pageable, totalElements);

        } else if (type == 3) {
            // 인플레이
            List<GameResultResponseDTO> results = queryFactory
                    .select(Projections.constructor(GameResultResponseDTO.class,
                            match.leagueName,
                            match.locationName,
                            match.sportsName,
                            match.startDate,
                            match.status,
                            match.homeName,
                            match.homeScore,
                            match.awayName,
                            match.awayScore))
                    .from(match)
                    .where(match.status.eq("3")
                            .and(match.isLive.in("1"))
                            .and((match.startDate.between(formattedEndTime,formattedStartTime))))
                    .orderBy(match.startDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();


            long totalElements = queryFactory.select(match.count())
                    .from(match)
                    .where(match.status.eq("3")
                            .and(match.isLive.in("1"))
                            .and((match.startDate.between(formattedEndTime,formattedStartTime))))
                    .fetchOne();

            return new PageImpl<>(results, pageable, totalElements);
        }
        return null;
    }

    //베팅 내역 반환 시 스코어 값만 가져오기
    @Override
    public List<MatchScoreDTO> searchByScoreData(List<String> matchId) {
        List<MatchScoreDTO> results = queryFactory.select(Projections.constructor(MatchScoreDTO.class,
                match.matchId,
                match.homeScore,
                match.awayScore,
                match.status))
                .from(match)
                .where(match.matchId.in(matchId))
                .fetch();
        return results;
    }

    @Override
    public List<GameResultListDTO> searchByEndedMatchData(Long type,String endDate,String startDate) {
        BooleanExpression condition = null;

        if (type != null) {
            // type에 따라 다른 조건 추가
            if (type == 1) {
                condition = oddLive.marketName.in("1X2", "12", "12 Including Overtime")
                        .and((match.startDate.between(endDate, startDate)));
            } else if (type == 2) {
                condition = odd.marketName.in("1X2","12 Including Overtime","12")
                        .and((match.startDate.between(endDate, startDate)));
            } else if (type == 4) {
                // 크로스
                condition = odd.marketName.in("1X2","12 Including Overtime", "12", "Under/Over", "Asian Handicap","Under/Over Including Overtime", "Asian Handicap Including Overtime")
                        .and((match.startDate.between(endDate, startDate)));
            } else if (type == 5) {
                // 승무패 (승패,승무패)
                condition = odd.marketName.in("1X2","12 Including Overtime", "12")
                        .and((match.startDate.between(endDate, startDate)));
            } else if (type == 6) {
                // 핸디캡
                condition = odd.marketName.in("Under/Over", "Asian Handicap","Under/Over Including Overtime", "Asian Handicap Including Overtime")
                        .and((match.startDate.between(endDate, startDate)));
            } else if (type == 7) {
                // 스페셜1
                condition = odd.marketName.in("1st Period Odd/Even", "Asian Handicap 1st Period", "Under/Over 1st Period", "1st 5 Innings Winner - 12")
                        .and((match.leagueName.in("Bundesliga", "LaLiga", "Serie A", "Ligue 1", "Premier League",  //축구
                                "NHL",                      //아이스 하키
                                "V-League - Women",         //여자 배구
                                "V-League - Men",           //남자 배구
                                "WKBL W",                   //여자 농구
                                "NBA", "WNBA", "KBL", "WKBL"//농구
                        ))).and((match.startDate.between(endDate, startDate)));
            } else if (type == 8) {
                // 스페셜2
                condition = odd.marketName.in("Asian Handicap Halftime", "Under/Over Halftime", "Asian Handicap Sets")
                        .and((match.leagueName.in("Bundesliga", "LaLiga", "Serie A", "Ligue 1", "Premier League",  //축구
                                "NHL",                      //아이스 하키
                                "V-League - Women",         //여자 배구
                                "V-League - Men",           //남자 배구
                                "WKBL W",                   //여자 농구
                                "NBA", "WNBA", "KBL", "WKBL"//농구
                        ))).and((match.startDate.between(endDate, startDate)));
            }
        }
        if (type == 1) {
            List<GameResultListDTO> results = queryFactory
                    .select(Projections.constructor(GameResultListDTO.class,
                            match.matchId,
                            match.leagueName,
                            match.locationName,
                            match.sportsName,
                            match.status,
                            match.homeName,
                            match.homeScore,
                            match.awayName,
                            match.awayScore,
                            match.period1Home,
                            match.period1Away,
                            oddLive.marketId,
                            oddLive.marketName,
                            oddLive.idx,
                            oddLive.betName,
                            oddLive.baseLine,
                            oddLive.price,
                            oddLive.settlement,
                            match.startDate))
                    .from(match)
                    .join(oddLive).on(oddLive.matchId.eq(match.matchId))
                    .where((condition != null ? condition : match.isLive.eq("1"))
                            .and(match.status.in("3", "4", "7"))
                            .and(oddLive.betStatus.in("3")))
                    .fetch();
            return results;
        } else {
            List<GameResultListDTO> results = queryFactory
                    .select(Projections.constructor(GameResultListDTO.class,
                            match.matchId,
                            match.leagueName,
                            match.locationName,
                            match.sportsName,
                            match.status,
                            match.homeName,
                            match.homeScore,
                            match.awayName,
                            match.awayScore,
                            match.period1Home,
                            match.period1Away,
                            odd.marketId,
                            odd.marketName,
                            odd.idx,
                            odd.betName,
                            odd.baseLine,
                            odd.price,
                            odd.settlement,
                            match.startDate
                    ))
                    .from(match)
                    .join(odd).on(odd.matchId.eq(match.matchId))
                    .where((condition != null ? condition : match.isPrematch.eq("1"))
                            .and(match.status.in("3", "4", "7"))
                            .and(odd.betStatus.in("3")))
                    .fetch();
            return results;
        }
    }


    // 인플레이
    @Override
    public List<InPlayGetFixtureDTO> getInPlayFixtures(String sportsName) {

        List<InPlayGetFixtureDTO> results = queryFactory
                .select(Projections.constructor(InPlayGetFixtureDTO.class,
                        match.matchId,
                        match.leagueName,
                        match.locationName,
                        match.sportsName,
                        match.status,
                        match.homeName,
                        match.awayName,
                        match.startDate,
                        match.homeScore,
                        match.awayScore,
                        oddLive.marketId,
                        oddLive.marketName,
                        oddLive.idx,
                        oddLive.betName,
                        oddLive.line,
                        oddLive.baseLine,
                        oddLive.price,
                        oddLive.startPrice,
                        oddLive.lastUpdate,
                        oddLive.betStatus,
                        match.period,
                        match.period1,
                        match.period1Home,
                        match.period1Away,
                        match.period2,
                        match.period2Home,
                        match.period2Away,
                        match.period3,
                        match.period3Home,
                        match.period3Away,
                        match.period4,
                        match.period4Home,
                        match.period4Away,
                        match.period5,
                        match.period5Home,
                        match.period5Away,
                        match.period6,
                        match.period6Home,
                        match.period6Away,
                        match.period7,
                        match.period7Home,
                        match.period7Away,
                        match.period8,
                        match.period8Home,
                        match.period8Away,
                        match.period9,
                        match.period9Home,
                        match.period9Away,
                        match.period10,
                        match.period10Home,
                        match.period10Away))
                .from(match)
                .join(oddLive).on(oddLive.matchId.eq(match.matchId))
                .where(((match.status.in("2")))
                                .and((match.sportsName.in("Football", "Ice Hockey", "Basketball", "Volleyball", "Baseball")))
                                .and((oddLive.marketName.in("1X2", "12 including overtime", "12")))
                                .and(match.isLive.in("1")).and(oddLive.betStatus.in("1", "2"))
                                .and(sportNameEq(sportsName)))
                .distinct()
                .fetch();
        return results;
    }


    @Override
    public List<InPlayGetFixtureDTO> getInPlayFixturesDetail(String matchId,String type) {

        BooleanExpression condition = null;

        if (type.equals("Main")) {
            condition = oddLive.marketName.in(
                    //축구
                    "1X2", "12", "Under/Over",
                    "Asian Handicap",
                    "Odd/Even",
                    "Double Chance",
                    "European Handicap",
                    "Both Teams To Score",
                    //야구
                    "12 Including Overtime",
                    "Under/Over Including Overtime",
                    "Asian Handicap Including Overtime",
                    "Odd/Even Including Overtime",
                    "Correct Score Including Overtime",
                    //농구
                    "Correct Score",
                    "Asian Under/Over",
                    "European Handicap Including Overtime",
                    //하키 메인마켓은 위 내용과 동일함
                    //배구
                    "Asian Handicap Sets");
        } else if (type.equals("Period")) {
            condition = oddLive.marketName.in("Correct Score 1st Period",
                    "Under/Over 1st Period",
                    "1st Period Winner",
                    "2nd Period Winner",
                    "3rd Period Winner",
                    "4th Period Winner",
                    "Under/Over 2nd Period",
                    "Under/Over 3rd Period",
                    "Under/Over 4th Period",
                    "First Team To Score 1st Period",
                    "Asian Handicap 1st Period",
                    "Asian Handicap 2nd Period",
                    "Asian Handicap 3rd Period",
                    "Asian Handicap 4th Period",
                    "1st Period Odd/Even",
                    "2nd Period Odd/Even",
                    "3rd Period Odd/Even",
                    "4th Period Odd/Even",
                    "Under/Over 1st Period - Home Team",
                    "Under/Over 2nd Period - Home Team",
                    "Under/Over 1st Period - Away Team",
                    "Under/Over 2nd Period - Away Team",
                    "1st Period Winner Home/Away",
                    "2nd Period Winner Home/Away",
                    "3rd Period Winner Home/Away",
                    "4th Period Winner Home/Away",
                    "Under/Over 1st Period - Home Team",
                    "Under/Over 2nd Period - Home Team",
                    "Under/Over 3rd Period - Home Team",
                    "Under/Over 4th Period - Home Team",
                    "Under/Over 1st Period - Away Team",
                    "Under/Over 2nd Period - Away Team",
                    "Under/Over 3rd Period - Away Team",
                    "Under/Over 4th Period - Away Team",
                    "Under/Exactly/Over - 1st Period",
                    "Under/Exactly/Over - 2nd Period",
                    "Under/Exactly/Over - 3rd Period",
                    "Under/Exactly/Over - 4th Period",
                    "1st Period Race To",
                    "2nd Period Race To",
                    "3rd Period Race To",
                    "4th Period Race To",
                    "1st Period Both Teams To Score",
                    "2nd Period Both Teams To Score",
                    "3rd Period Both Teams To Score",
                    "Double Chance 1st Period",
                    "Double Chance 2nd Period",
                    "Double Chance 3rd Period",
                    "Correct Score 1st Period",
                    "Correct Score 2nd Period",
                    "Correct Score 3rd Period",
                    "Both Teams To Score 1st Half",
                    "Both Teams To Score 2nd Half",
                    "Away Team Number Of Goals In 1st Half",
                    "Away Team to Score 2nd Half",
                    "Home Team Number Of Goals In 1st Half",
                    "Home Team to Score 2nd Half",
                    "Under/Exactly/Over - 1st Period",
                    "Under/Exactly/Over - 2nd Period",
                    "Double Chance 1st Period",
                    "Double Chance 2nd Period",
                    "1X2 Offsides 1st Period",
                    "1X2 Offsides 2nd Period",
                    "Highest Scoring Period",
                    "European Handicap 1st Period",
                    "European Handicap 2nd Period",
                    "1X2 Offsides 1st Period",
                    "1X2 Offsides 2nd Period",
                    "Under/Over Offsides Home Team 1st Period",
                    "Under/Over Offsides Away Team 1st Period");
        } else if (type.equals("Optional")) {
            condition = oddLive.marketName.in("First Team To Score",
                    "First Team To Score 2nd Half",
                    "Away Team to Score",
                    "Home Team to Score",
                    "Last Team To Score",
                    "Away Team Win To Nil",
                    "Home Team Win To Nil",
                    "Home Team To Keep A Clean Sheet",
                    "Away Team To Keep A Clean Sheet",
                    "Under/Over Offsides",
                    "Under/Over Throw-Ins",
                    "Odd/Even - Home Team",
                    "Odd/Even - Away Team",
                    "Both Teams To Score In Both Halves",
                    "Under/Exactly/Over",
                    "Under/Over Offsides - Home Team",
                    "Under/Over Offsides - Away Team",
                    "1X2 Offsides",
                    "Will The First Scoring Team Win?",
                    "Away Team Number Of Goals",
                    "Under/Over - Home Team",
                    "Under/Over - Away Team",
                    "Under/Over - Away Team Including Overtime",
                    "Under/Over - Home Team Including Overtime",
                    "Race To");
        }
        List<InPlayGetFixtureDTO> results = queryFactory
                .select(Projections.constructor(InPlayGetFixtureDTO.class,
                        match.matchId,
                        match.leagueName,
                        match.locationName,
                        match.sportsName,
                        match.status,
                        match.homeName,
                        match.awayName,
                        match.startDate,
                        match.homeScore,
                        match.awayScore,
                        oddLive.marketId,
                        oddLive.marketName,
                        oddLive.idx,
                        oddLive.betName,
                        oddLive.line,
                        oddLive.baseLine,
                        oddLive.price,
                        oddLive.startPrice,
                        oddLive.lastUpdate,
                        oddLive.betStatus,
                        match.period,
                        match.period1,
                        match.period1Home,
                        match.period1Away,
                        match.period2,
                        match.period2Home,
                        match.period2Away,
                        match.period3,
                        match.period3Home,
                        match.period3Away,
                        match.period4,
                        match.period4Home,
                        match.period4Away,
                        match.period5,
                        match.period5Home,
                        match.period5Away,
                        match.period6,
                        match.period6Home,
                        match.period6Away,
                        match.period7,
                        match.period7Home,
                        match.period7Away,
                        match.period8,
                        match.period8Home,
                        match.period8Away,
                        match.period9,
                        match.period9Home,
                        match.period9Away,
                        match.period10,
                        match.period10Home,
                        match.period10Away
                ))
                .from(match)
                .join(oddLive).on(oddLive.matchId.eq(match.matchId))
                .where(
                        ((match.status.in("2")))
                                .and(condition.and(match.sportsName.in("football", "Ice Hockey", "basketball", "volleyball", "Baseball")))
                                .and(match.isLive.in("1")).and(oddLive.betStatus.in("1", "2"))
                                .and(matchIdEq(matchId)))
                .fetch();
        return results;
    }




    @Override
    public Page<AdminPreMatchDTO> searchByAdminMatch(String type,String status,String sportsName,String leagueName, Pageable pageable) {

        BooleanExpression condition = null;

        if (type != null) {
            if (type.equals("pre")) {
                condition = match.isPrematch.in("1");
            } else if (type.equals("live")) {
                condition = match.isLive.in("1");
            }
        }
        if (condition == null) {
            condition = Expressions.asBoolean(true).isTrue();
        }

        List<AdminPreMatchDTO> results = queryFactory.select(Projections.constructor(AdminPreMatchDTO.class,
                match.matchId,
                match.startDate,
                match.sportsName,
                match.locationName,
                match.leagueName,
                match.homeName,
                match.homeScore,
                match.awayName,
                match.awayScore,
                match.status
                ))
                .from(match)
                .where(condition
                        .and(statusEq(status))
                        .and(leagueNameEq(leagueName))
                        .and(sportNameEq(sportsName)))
                .orderBy(match.startDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.select(match.count())
                .from(match)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(results,pageable,totalElements);
    }


    private BooleanExpression statusEq(String statusCond) {
        return statusCond != null ? match.status.eq(statusCond) : null;
    }
    private BooleanExpression leagueNameEq(String leagueNameCond) {
        return leagueNameCond != null ? match.leagueName.eq(leagueNameCond) : null;
    }
    private BooleanExpression sportNameEq(String sportNameCond) {
        return sportNameCond != null ? match.sportsName.eq(sportNameCond) : null;
    }
    private BooleanExpression matchIdEq(String matchId) {
        return matchId != null ? match.matchId.eq(matchId) : null;
    }


}