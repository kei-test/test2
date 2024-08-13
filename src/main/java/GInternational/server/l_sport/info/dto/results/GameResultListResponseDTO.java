package GInternational.server.l_sport.info.dto.results;

import GInternational.server.l_sport.info.dto.pre.OddDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Max;
import java.security.cert.CertSelector;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Setter
@Getter
public class GameResultListResponseDTO {

    private String matchId;
    private String leagueName;
    private String locationName;
    private String sportsName;
    private String startDate;
    private String status;
    private String homeName;
    private String awayName;
    private String gameType;
    private String winRate;
    private String drawRate;
    private String loseRate;
    private String homeScore;
    private String awayScore;
    private String period1Home;
    private String period1Away;
    private String period2Home;
    private String period2Away;
    private int halfTimeScoreHome;
    private int halfTimeScoreAway;
    private String result;



    public static List<GameResultListResponseDTO> transform(List<GameResultListDTO> preMatchFixtures) {
        Map<String, Map<String, Map<String, List<GameResultListDTO>>>> groupedByFixtureMarketAndBaseLine = preMatchFixtures.stream()
                .collect(Collectors.groupingBy(GameResultListDTO::getMatchId,
                        Collectors.groupingBy(GameResultListDTO::getMarketId,
                                Collectors.groupingBy(GameResultListDTO::getBaseLine))));

        List<GameResultListResponseDTO> responseList = new ArrayList<>();

        groupedByFixtureMarketAndBaseLine.forEach((matchId, marketMap) -> {
            marketMap.forEach((marketId, baseLineMap) -> {
                baseLineMap.forEach((baseLine, aList) -> {
                    GameResultListResponseDTO responseDTO = new GameResultListResponseDTO();

                    for (GameResultListDTO gameResultListDTO : aList) {
                        if (gameResultListDTO == aList.get(0)) {
                            responseDTO.setMatchId(gameResultListDTO.getMatchId());
                            responseDTO.setStartDate(gameResultListDTO.getStartDate());
                            responseDTO.setSportsName(gameResultListDTO.getSportsName());
                            responseDTO.setLocationName(gameResultListDTO.getLocationName());
                            responseDTO.setLeagueName(gameResultListDTO.getLeagueName());
                            responseDTO.setStatus(gameResultListDTO.getStatus());
                            responseDTO.setHomeName(gameResultListDTO.getHomeName());
                            responseDTO.setHomeScore(gameResultListDTO.getHomeScore());
                            responseDTO.setAwayName(gameResultListDTO.getAwayName());
                            responseDTO.setAwayScore(gameResultListDTO.getAwayScore());
                            responseDTO.setPeriod1Home(gameResultListDTO.getPeriod1Home());
                            responseDTO.setPeriod1Away(gameResultListDTO.getPeriod1Away());
                            responseDTO.setPeriod2Home(gameResultListDTO.getPeriod2Home());
                            responseDTO.setPeriod2Away(gameResultListDTO.getPeriod2Away());
                            int period1HomeScore = Integer.parseInt(responseDTO.getPeriod1Home());
                            int period1AwayScore = Integer.parseInt(responseDTO.getPeriod1Away());
                            int period2HomeScore = Integer.parseInt(responseDTO.getPeriod2Home());
                            int period2AwayScore = Integer.parseInt(responseDTO.getPeriod2Away());
                            responseDTO.setHalfTimeScoreHome(period1HomeScore + period2HomeScore);
                            responseDTO.setHalfTimeScoreAway(period1AwayScore + period2AwayScore);
                        }

                        switch (gameResultListDTO.getMarketName()) {
                            case "Asian Handicap Including Overtime":
                            case "Asian Handicap":
                                responseDTO.setDrawRate(gameResultListDTO.getBaseLine());
                                responseDTO.setGameType("Asian Handicap");
                                if ("1".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("2".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                break;

                            case "1st Period Odd/Even":
                                responseDTO.setGameType("1st Period Odd/Even");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("Odd".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("Even".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            case "Asian Handicap 1st Period":
                                responseDTO.setDrawRate(gameResultListDTO.getBaseLine());
                                responseDTO.setGameType("Asian Handicap 1st Period");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("1".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("2".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            case "Asian Handicap Halftime":
                                responseDTO.setDrawRate(gameResultListDTO.getBaseLine());
                                responseDTO.setGameType("Asian Handicap Halftime");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("1".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("2".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            case "Asian Handicap Sets":
                                responseDTO.setDrawRate(gameResultListDTO.getBaseLine());
                                responseDTO.setGameType("Asian Handicap Sets");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("1".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("2".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            case "1X2":
                                responseDTO.setGameType("1X2");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("1".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("X".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setDrawRate(gameResultListDTO.getPrice());
                                } else if ("2".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            case "12":
                            case "12 Including Overtime":
                                responseDTO.setGameType("12");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("1".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("2".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;
                            case "Under/Over Including Overtime":
                            case "Under/Over":
                                responseDTO.setDrawRate(gameResultListDTO.getBaseLine());
                                responseDTO.setGameType("Under/Over");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("Over".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("Under".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            case "Under/Over 1st Period":
                                responseDTO.setDrawRate(gameResultListDTO.getBaseLine());
                                responseDTO.setGameType("Under/Over 1st Period");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("Over".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("Under".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            case "Under/Over Halftime":
                                responseDTO.setDrawRate(gameResultListDTO.getBaseLine());
                                responseDTO.setGameType("Under/Over Halftime");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("Over".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("Under".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            case "1st 5 Innings Winner - 12":
                                responseDTO.setDrawRate(gameResultListDTO.getBaseLine());
                                responseDTO.setGameType("1st 5 Innings Winner - 12");
                                if ("2".equals(gameResultListDTO.getSettlement())) {
                                    responseDTO.setResult(gameResultListDTO.getBetName());
                                }
                                if ("1".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setWinRate(gameResultListDTO.getPrice());
                                } else if ("2".equals(gameResultListDTO.getBetName())) {
                                    responseDTO.setLoseRate(gameResultListDTO.getPrice());
                                }
                                break;

                            default:
                                break;
                        }
                    }
                    responseList.add(responseDTO);
                });
            });
        });
        return responseList;
    }
}