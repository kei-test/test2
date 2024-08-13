package GInternational.server.l_sport.info.dto.pre;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Setter
@Getter
public class PreMatchGetFixtureResponseDTO {

    private String matchId;
    private String leagueName;
    private String locationName;
    private String sportsName;
    private String startDate;
    private String status;
    private String homeName;
    private String awayName;
    private String leagueId;
    private String isPreMatch;
    private String isLive;
    private List<MarketDTO> odds;



    public static List<PreMatchGetFixtureResponseDTO> transform(List<PreMatchGetFixtureDTO> fixtures) {
        Map<String, List<PreMatchGetFixtureDTO>> groupedFixtures = fixtures.stream().collect(Collectors.groupingBy(PreMatchGetFixtureDTO::getMatchId));
        return groupedFixtures.entrySet().stream()
                .map(entry -> {
                    PreMatchGetFixtureDTO firstFixture = entry.getValue().get(0);

                    List<MarketDTO> markets = entry.getValue().stream()
                            .filter(fixture -> {
                                // 종료된 경기 제외
                                return fixture.getMarketId() != null;
                            })
                            .map(fixture -> {
                                MarketDTO market = new MarketDTO();
                                market.setMarketId(fixture.getMarketId());
                                market.setMarketName(fixture.getMarketName());

                                List<OddDTO> bets = entry.getValue().stream()
                                        .filter(bet -> {
                                            return bet.getMarketId() != null && bet.getMarketId().equals(fixture.getMarketId());
                                        })
                                        .map(bet -> {
                                            OddDTO betDTO = new OddDTO();
                                            betDTO.setIdx(bet.getIdx());
                                            betDTO.setBetName(bet.getBetName());
                                            betDTO.setLastUpdate(bet.getLastUpdate());
                                            betDTO.setLine(bet.getLine());
                                            betDTO.setBaseLine(bet.getBaseLine());
                                            betDTO.setPrice(bet.getPrice());
                                            betDTO.setBetStatus(bet.getBetStatus());
                                            return betDTO;
                                        })
                                        .distinct()
                                        .collect(Collectors.toList());

                                market.setBets(bets);
                                return market;
                            })
                            .distinct()
                            .collect(Collectors.toList());

                    PreMatchGetFixtureResponseDTO responseDTO = new PreMatchGetFixtureResponseDTO();
                        responseDTO.setMatchId(firstFixture.getMatchId());
                        responseDTO.setLeagueName(firstFixture.getLeagueName());
                        responseDTO.setLocationName(firstFixture.getLocationName());
                        responseDTO.setSportsName(firstFixture.getSportsName());
                        responseDTO.setStatus(firstFixture.getStatus());
                        responseDTO.setAwayName(firstFixture.getAwayName());
                        responseDTO.setHomeName(firstFixture.getHomeName());
                        responseDTO.setLeagueId(firstFixture.getLeagueId());
                        responseDTO.setStartDate(firstFixture.getStartDate());
                        responseDTO.setIsPreMatch(firstFixture.getIsPreMatch());
                        responseDTO.setIsLive(firstFixture.getIsLive());
                        responseDTO.setOdds(markets);
                        return responseDTO;
                })
                .collect(Collectors.toList());
    }
}
