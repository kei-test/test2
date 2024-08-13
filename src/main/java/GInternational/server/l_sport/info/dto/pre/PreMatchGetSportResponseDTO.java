package GInternational.server.l_sport.info.dto.pre;

import GInternational.server.l_sport.info.dto.results.GameResultListDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Setter
@Getter
public class PreMatchGetSportResponseDTO {

    private String matchId;
    private String startDate;
    private String sportName;
    private String locationName;
    private String leagueName;
    private String homeName;
    private String awayName;
    private String winRate;
    private String winRateBetId;
    private String winBetStatus;
    private String drawRate;
    private String drawRateBetId;
    private String drawBetStatus;
    private String loseRate;
    private String loseRateBetId;
    private String loseBetStatus;
    private String status;
    private String type;
    private String isPreMatch;
    private String isLive;



    public static List<PreMatchGetSportResponseDTO> transform(List<PreMatchGetFixtureDTO> preMatchFixtures) {
        // fixtureId와 marketId를 기준으로 그룹화
        Map<String, Map<String,Map<String, List<PreMatchGetFixtureDTO>>>> groupedByFixtureAndMarket = preMatchFixtures.stream().
                collect(Collectors.groupingBy(PreMatchGetFixtureDTO::getMatchId,
                        Collectors.groupingBy(PreMatchGetFixtureDTO::getMarketId,
                                Collectors.groupingBy(PreMatchGetFixtureDTO::getBaseLine))));
        List<PreMatchGetSportResponseDTO> responseList = new ArrayList<>();


        groupedByFixtureAndMarket.forEach((matchId, marketMap) -> {
            marketMap.forEach((marketId, baseLineMap) -> {
                baseLineMap.forEach((baseLine, aList) -> {
                    PreMatchGetSportResponseDTO responseDTO = new PreMatchGetSportResponseDTO();

                    for (PreMatchGetFixtureDTO preMatchGetFixtureDTO : aList) {
                        if (preMatchGetFixtureDTO == aList.get(0)) {
                            String combinedFixtureId = matchId + "_" + marketId;
                            responseDTO.setMatchId(combinedFixtureId);
                            responseDTO.setStartDate(preMatchGetFixtureDTO.getStartDate());
                            responseDTO.setSportName(preMatchGetFixtureDTO.getSportsName());
                            responseDTO.setLocationName(preMatchGetFixtureDTO.getLocationName());
                            responseDTO.setLeagueName(preMatchGetFixtureDTO.getLeagueName());
                            responseDTO.setHomeName(preMatchGetFixtureDTO.getHomeName());
                            responseDTO.setAwayName(preMatchGetFixtureDTO.getAwayName());
                            responseDTO.setStatus(preMatchGetFixtureDTO.getStatus());
                            responseDTO.setType(preMatchGetFixtureDTO.getMarketName());
                            responseDTO.setIsPreMatch(preMatchGetFixtureDTO.getIsPreMatch());
                            responseDTO.setIsLive(preMatchGetFixtureDTO.getIsLive());
                            responseDTO.setStartDate(preMatchGetFixtureDTO.getStartDate());
                        }

                        // betName에 따라 winRate, drawRate, loseRate 설정
                        if (preMatchGetFixtureDTO.getMarketName().equals("Asian Handicap") || preMatchGetFixtureDTO.getMarketName().equals("Asian Handicap Including Overtime")) {
                            responseDTO.setDrawRate(preMatchGetFixtureDTO.getBaseLine());
                            if ("1".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("2".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("Odd/Even") || preMatchGetFixtureDTO.getMarketName().equals("1st Period Odd/Even")) {
                            if ("Even".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("Odd".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("Asian Handicap 1st Period")) {
                            responseDTO.setDrawRate(preMatchGetFixtureDTO.getBaseLine());
                            if ("1".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("2".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("Asian Handicap Halftime")) {
                            responseDTO.setDrawRate(preMatchGetFixtureDTO.getBaseLine());
                            if ("1".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("2".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("Asian Handicap Sets")) {
                            responseDTO.setDrawRate(preMatchGetFixtureDTO.getBaseLine());
                            if ("1".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("2".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("1X2")) {
                            if ("1".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("X".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setDrawRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setDrawRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setDrawBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("2".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("12") || preMatchGetFixtureDTO.getMarketName().equals("12 Including Overtime")) {
                            if ("1".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("2".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("Under/Over 1st Period")) {
                            responseDTO.setDrawRate(preMatchGetFixtureDTO.getBaseLine());
                            if ("Over".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("Under".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("Under/Over Halftime")) {
                            responseDTO.setDrawRate(preMatchGetFixtureDTO.getBaseLine());
                            if ("Over".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("Under".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("1st 5 Innings Winner - 12")) {
                            responseDTO.setDrawRate(preMatchGetFixtureDTO.getBaseLine());
                            if ("1".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("2".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        } else if (preMatchGetFixtureDTO.getMarketName().equals("Under/Over") || preMatchGetFixtureDTO.getMarketName().equals("Under/Over Including Overtime")) {
                            responseDTO.setDrawRate(preMatchGetFixtureDTO.getBaseLine());
                            if ("Over".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setWinRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setWinRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setWinBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            } else if ("Under".equals(preMatchGetFixtureDTO.getBetName())) {
                                responseDTO.setLoseRate(preMatchGetFixtureDTO.getPrice());
                                responseDTO.setLoseRateBetId(preMatchGetFixtureDTO.getIdx());
                                responseDTO.setLoseBetStatus(preMatchGetFixtureDTO.getBetStatus());
                            }
                        }
                    }
                    responseList.add(responseDTO);
                });
            });
        });
        return responseList;
    }
}
