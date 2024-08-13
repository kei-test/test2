package GInternational.server.l_sport.info.entity;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity(name = "api_game")
@Table(name = "api_game", indexes = {
        @Index(name = "status", columnList = "status"),
        @Index(name = "start_date", columnList = "start_date"),
        @Index(name = "sports_id", columnList = "sports_id"),
        @Index(name = "league_id", columnList = "league_id")})
public class Match {

        @Id
        @Column(name = "match_id", length = 50, nullable = false)
        private String matchId;

        @Column(name = "sports_id", length = 50, nullable = false)
        private String sportsId;

        @Column(name = "sports_name", length = 50, nullable = false)
        private String sportsName;

        @Column(name = "location_id", length = 50, nullable = false)
        private String locationId;

        @Column(name = "location_name", length = 250, nullable = false)
        private String locationName;

        @Column(name = "league_id", length = 50, nullable = false)
        private String leagueId;

        @Column(name = "league_name", length = 250, nullable = false)
        private String leagueName;

        @Column(name = "start_date", length = 14, nullable = false)
        private String startDate;

        @Column(name = "start_date_old", length = 14)
        private String startDateOld;

        @Column(name = "last_update", length = 14)
        private String lastUpdate;

        @Column(name = "status", length = 1, nullable = false,columnDefinition = "varchar(1) default '1'")
        private String status;

        @Column(name = "home_id", length = 50)
        private String homeId;

        @Column(name = "home_name", length = 250)
        private String homeName;

        @Column(name = "away_id", length = 50)
        private String awayId;

        @Column(name = "away_name", length = 250)
        private String awayName;

        @Column(name = "home_score", length = 50)
        private String homeScore;

        @Column(name = "away_score", length = 50)
        private String awayScore;

        @Column(name = "period", length = 50)
        private String period;

        @Column(name = "time", length = 50)
        private String time;

        @Column(name = "pre_count", nullable = false,columnDefinition = "int default 0")
        private int preCount;

        @Column(name = "live_count", nullable = false,columnDefinition = "int default 0")
        private int liveCount;

        @Column(name = "pre_total_amount", length = 13, nullable = false,columnDefinition = "varchar(13)  default '0'")
        private String preTotalAmount;

        @Column(name = "live_total_amount", length = 13, nullable = false,columnDefinition = "varchar(13)  default '0'")
        private String liveTotalAmount;

        @Column(name = "is_prematch", length = 5, nullable = false,columnDefinition = "varchar(5)  default '0'")
        private String isPrematch;

        @Column(name = "is_live", length = 5, nullable = false,columnDefinition = "varchar(5) default '0'")
        private String isLive;

//        @Column(name = "is_custom", length = 1, nullable = false,columnDefinition = "char(1) default 'N'")
//        private char isCustom;
//        @Column(name = "is_outright", length = 5, nullable = false,columnDefinition = "varchar(5) default '0'")
//        private String isOutright;
//        @Column(name = "is_test", length = 5, nullable = false,columnDefinition = "varchar(5) default '0'")
//        private String isTest;
//        @Column(name = "is_time_update", length = 1)
//        private char isTimeUpdate;
//
//        @Column(name = "is_admin_check", length = 1)
//        private char isAdminCheck;
//        @Column(name = "tracker", length = 25, nullable = false,columnDefinition = "varchar(25) default '-1'")
//        private String tracker;
//
//        @Column(name = "tv", length = 25, nullable = false,columnDefinition = "varchar(25) default '-1'")
//        private String tv;

        @Column(name = "period1", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period1;

        @Column(name = "period1_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period1Home;

        @Column(name = "period1_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period1Away;

        @Column(name = "period2", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period2;

        @Column(name = "period2_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period2Home;

        @Column(name = "period2_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period2Away;

        @Column(name = "period3", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period3;

        @Column(name = "period3_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period3Home;

        @Column(name = "period3_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period3Away;

        @Column(name = "period4", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period4;

        @Column(name = "period4_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period4Home;

        @Column(name = "period4_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period4Away;

        @Column(name = "period5", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period5;

        @Column(name = "period5_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period5Home;

        @Column(name = "period5_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period5Away;

        @Column(name = "period6", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period6;

        @Column(name = "period6_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period6Home;

        @Column(name = "period6_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period6Away;

        @Column(name = "period7", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period7;

        @Column(name = "period7_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period7Home;

        @Column(name = "period7_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period7Away;

        @Column(name = "period8", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period8;

        @Column(name = "period8_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period8Home;

        @Column(name = "period8_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period8Away;

        @Column(name = "period9", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period9;

        @Column(name = "period9_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period9Home;

        @Column(name = "period9_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period9Away;

        @Column(name = "period10", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period10;

        @Column(name = "period10_home", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period10Home;

        @Column(name = "period10_away", length = 25, nullable = false,columnDefinition = "int default 0")
        private String period10Away;
    }

