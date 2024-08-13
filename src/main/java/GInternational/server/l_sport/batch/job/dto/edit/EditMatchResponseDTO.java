package GInternational.server.l_sport.batch.job.dto.edit;

import GInternational.server.api.vo.BetTypeEnum;
import GInternational.server.l_sport.info.dto.pre.OddDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetFixtureDTO;
import GInternational.server.l_sport.info.dto.pre.PreMatchGetSportResponseDTO;
import GInternational.server.l_sport.info.dto.results.GameResultListDTO;
import GInternational.server.l_sport.info.dto.results.GameResultListResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class EditMatchResponseDTO {
    private String matchId;  //경기 id
    private String betType;  //클라이언트 진입 메뉴(해외배당,크로스,승무패,스폐셜 등등)
    private String marketName;  //마켓명
    private String startDate; //게임시간
    private String sportsName;  //종목명
    private String locationName;  //지역명
    private String leagueName;  //리그명
    private String homeName;  //홈팀
    private String awayName;  //원정팀
    private String winOrOverIdx;
    private String winBetName;
    private String winOrOver;  // 승 or 오버
    private String winOrOverSettlement;
    private String drawOrBaseLineIdx;
    private String drawBetName;
    private String drawOrBaseLine;  // 무 or 기준점
    private String drawOrBaseLineSettlement;
    private String loseOrUnderIdx;
    private String loseBetName;
    private String loseOrUnder;  // 패 or 언더
    private String loseOrUnderSettlement;
    private String totalOrders;  //총 개수
    private String homeScore;  //홈팀 점수
    private String period1Home; //1쿼터 점수
    private String awayScore;  //원정팀 점수
    private String period1Away; //1쿼터 점수
    private String result;  //경기 결과
    private String status;  //경기 상태


    public static List<EditMatchResponseDTO> transform(List<GameResultListDTO> matchList) {
        Map<String, Map<String, Map<String, List<GameResultListDTO>>>> groupedByFixtureMarketAndBaseLine = matchList.stream()
                .collect(Collectors.groupingBy(GameResultListDTO::getMatchId,
                        Collectors.groupingBy(GameResultListDTO::getMarketName,
                                Collectors.groupingBy(GameResultListDTO::getBaseLine))));
        List<EditMatchResponseDTO> responseList = new ArrayList<>();

        groupedByFixtureMarketAndBaseLine.forEach((matchId, marketMap) -> {
            marketMap.forEach((marketName, baseLineMap) -> {
                baseLineMap.forEach((baseLine, aList) -> {

                    EditMatchResponseDTO responseDTO = new EditMatchResponseDTO();


                    for (GameResultListDTO queryResult : aList) {
                        responseDTO.setMatchId(matchId);
                        responseDTO.setMarketName(marketName);
                        responseDTO.setSportsName(queryResult.getSportsName());
                        responseDTO.setLocationName(queryResult.getLocationName());
                        responseDTO.setLeagueName(queryResult.getLeagueName());
                        responseDTO.setHomeName(queryResult.getHomeName());
                        responseDTO.setAwayName(queryResult.getAwayName());
                        responseDTO.setHomeScore(queryResult.getHomeScore());
                        responseDTO.setAwayScore(queryResult.getAwayScore());
                        responseDTO.setPeriod1Home(queryResult.getPeriod1Home());
                        responseDTO.setPeriod1Away(queryResult.getPeriod1Away());
                        responseDTO.setStartDate(queryResult.getStartDate());
                        responseDTO.setStatus(queryResult.getStatus());

                        if ("1X2".equals(queryResult.getMarketName()) || "12".equals(queryResult.getMarketName()) || "12 Including Overtime".equals(queryResult.getMarketName())) {
                            responseDTO.setBetType("승무패");
                        } else if ("Under/Over".equals(queryResult.getMarketName()) || "Asian Handicap".equals(queryResult.getMarketName())) {
                            responseDTO.setBetType("핸디캡");
                        } else if ("1st Period Odd/Even".equals(queryResult.getMarketName()) || "Asian Handicap 1st Period".equals(queryResult.getMarketName()) ||
                                "Under/Over 1st Period".equals(queryResult.getMarketName()) || "1st 5 Innings Winner - 12".equals(queryResult.getMarketName())) {
                            responseDTO.setBetType("스폐셜1");
                        } else if ("Asian Handicap Halftime".equals(queryResult.getMarketName()) || "Under/Over Halftime".equals(queryResult.getMarketName()) ||
                                "Asian Handicap Sets".equals(queryResult.getMarketName())) {
                            responseDTO.setBetType("스폐셜2");
                        }

                        switch (marketName) {
                            case "Asian Handicap":
                            case "Asian Handicap Including Overtime":
                            case "Asian Handicap 1st Period":
                            case "Asian Handicap Halftime":
                            case "Asian Handicap Sets":
                                responseDTO.setDrawOrBaseLine(queryResult.getBaseLine());
                                responseDTO.setMarketName(marketName);
                                if (queryResult.getSettlement().equals("2")) {
                                    responseDTO.setResult(queryResult.getBetName());
                                }
                                if ("1".equals(queryResult.getBetName())) {
                                    responseDTO.setWinBetName(queryResult.getBetName());
                                    responseDTO.setWinOrOverIdx(queryResult.getIdx());
                                    responseDTO.setWinOrOver(queryResult.getPrice());
                                    responseDTO.setWinOrOverSettlement(queryResult.getSettlement());
                                } else if ("2".equals(queryResult.getBetName())) {
                                    responseDTO.setLoseBetName(queryResult.getBetName());
                                    responseDTO.setLoseOrUnderIdx(queryResult.getIdx());
                                    responseDTO.setLoseOrUnder(queryResult.getPrice());
                                    responseDTO.setLoseOrUnderSettlement(queryResult.getSettlement());
                                }
                                break;
                            case "Under/Over":
                            case "Under/Over Including Overtime":
                            case "Under/Over 1st Period":
                            case "Under/Over Halftime":
                            case "1st 5 Innings Winner - 12":
                            case "1st Period Odd/Even":
                                responseDTO.setMarketName(marketName);
                                responseDTO.setDrawOrBaseLine(queryResult.getBaseLine());
                                if (queryResult.getSettlement().equals("2")) {
                                    responseDTO.setResult(queryResult.getBetName());
                                }
                                if ("Odd".equals(queryResult.getBetName()) || "Over".equals(queryResult.getBetName())) {
                                    responseDTO.setWinBetName(queryResult.getBetName());
                                    responseDTO.setWinOrOverIdx(queryResult.getIdx());
                                    responseDTO.setWinOrOver(queryResult.getPrice());
                                    responseDTO.setWinOrOverSettlement(queryResult.getSettlement());
                                } else if ("Even".equals(queryResult.getBetName()) || "Under".equals(queryResult.getBetName())) {
                                    responseDTO.setLoseBetName(queryResult.getBetName());
                                    responseDTO.setLoseOrUnderIdx(queryResult.getIdx());
                                    responseDTO.setLoseOrUnder(queryResult.getPrice());
                                    responseDTO.setLoseOrUnderSettlement(queryResult.getSettlement());
                                }
                                break;
                            case "1X2":
                            case "12":
                            case "12 Including Overtime":
                                responseDTO.setMarketName(marketName);
                                if (queryResult.getSettlement().equals("2")) {
                                    responseDTO.setResult(queryResult.getBetName());
                                }
                                if ("1".equals(queryResult.getBetName())) {
                                    responseDTO.setWinBetName(queryResult.getBetName());
                                    responseDTO.setWinOrOverIdx(queryResult.getIdx());
                                    responseDTO.setWinOrOver(queryResult.getPrice());
                                    responseDTO.setWinOrOverSettlement(queryResult.getSettlement());
                                } else if ("X".equals(queryResult.getBetName())) {
                                    responseDTO.setDrawBetName(queryResult.getBetName());
                                    responseDTO.setDrawOrBaseLineIdx(queryResult.getIdx());
                                    responseDTO.setDrawOrBaseLine(queryResult.getPrice());
                                    responseDTO.setDrawOrBaseLineSettlement(queryResult.getSettlement());
                                } else if ("2".equals(queryResult.getBetName())) {
                                    responseDTO.setLoseBetName(queryResult.getBetName());
                                    responseDTO.setLoseOrUnderIdx(queryResult.getIdx());
                                    responseDTO.setLoseOrUnder(queryResult.getPrice());
                                    responseDTO.setLoseOrUnderSettlement(queryResult.getSettlement());
                                }
                                break;
                            default:
                                break;
                        }
                        responseList.add(responseDTO);
                    }
                });
            });
        });
        return responseList;
    }
}