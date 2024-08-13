package GInternational.server.l_sport.info.dto.inplay;

import GInternational.server.l_sport.info.dto.pre.MarketDTO;
import GInternational.server.l_sport.info.dto.pre.OddDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
@Setter
@Getter
public class InPlayGetFixtureResponseDTO {
    private String matchId;
    private String leagueName;
    private String locationName;
    private String sportName;
    private String startDate;
    private String status;
    private String homeName;
    private String awayName;
    private String homeScore;
    private String awayScore;
    private List<InPlayPeriodResponseDTO> periods;
    private List<MarketDTO> markets;





    // Method to transform the original data into the desired structure
    public static List<InPlayGetFixtureResponseDTO> transform(List<InPlayGetFixtureDTO> fixtures) {
        // Group fixtures by fixtureId
        Map<String, List<InPlayGetFixtureDTO>> groupedFixtures = fixtures.stream()
                .filter(fixture -> {
                    return fixture.getMatchId() != null;}).collect(Collectors.groupingBy(InPlayGetFixtureDTO::getMatchId));

        // Transform grouped fixtures into the desired structure
        return groupedFixtures.entrySet().stream()
                .map(entry -> {
                    InPlayGetFixtureDTO firstFixture = entry.getValue().get(0);

                    List<MarketDTO> markets = entry.getValue().stream()
                            .filter(fixture -> {
                                // 진행중인 경기만
                                return fixture.getMarketId() != null;
                            })

                            .map(fixture -> {
                                MarketDTO market = new MarketDTO();
                                market.setMarketId(fixture.getMarketId());
                                market.setMarketName(fixture.getMarketName());

                                // Filter bets by marketId
                                List<OddDTO> bets = entry.getValue().stream()
                                        .filter(bet -> {
                                            return bet.getMarketId() != null && bet.getMarketId().equals(fixture.getMarketId());
                                        })
                                        .map(bet -> {
                                            OddDTO betDTO = new OddDTO();
                                            betDTO.setIdx(bet.getIdx());
                                            betDTO.setBetName(bet.getBetName());
                                            betDTO.setLine(bet.getLine());
                                            betDTO.setBaseLine(bet.getBaseLine());
                                            betDTO.setPrice(bet.getPrice());
                                            betDTO.setLastUpdate(bet.getLastUpdate());
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




                                List<InPlayPeriodResponseDTO> periods = entry.getValue().stream()
                                        .filter(period -> {
                                            //
                                            return period.getMatchId() != null && period.getMatchId().equals(firstFixture.getMatchId());
                                        })
                                        .map(period -> {
                                            InPlayPeriodResponseDTO inPlayPeriodResponseDTO = new InPlayPeriodResponseDTO();

                                            inPlayPeriodResponseDTO.setPeriod1(period.getPeriod1());
                                            inPlayPeriodResponseDTO.setPeriod1Home(period.getPeriod1Home());
                                            inPlayPeriodResponseDTO.setPeriod1Away(period.getPeriod1Away());

                                            inPlayPeriodResponseDTO.setPeriod2(period.getPeriod2());
                                            inPlayPeriodResponseDTO.setPeriod2Home(period.getPeriod2Home());
                                            inPlayPeriodResponseDTO.setPeriod2Away(period.getPeriod2Away());

                                            inPlayPeriodResponseDTO.setPeriod3(period.getPeriod3());
                                            inPlayPeriodResponseDTO.setPeriod3Home(period.getPeriod3Home());
                                            inPlayPeriodResponseDTO.setPeriod3Away(period.getPeriod3Away());

                                            inPlayPeriodResponseDTO.setPeriod4(period.getPeriod4());
                                            inPlayPeriodResponseDTO.setPeriod4Home(period.getPeriod4Home());
                                            inPlayPeriodResponseDTO.setPeriod4Away(period.getPeriod4Away());

                                            inPlayPeriodResponseDTO.setPeriod5(period.getPeriod5());
                                            inPlayPeriodResponseDTO.setPeriod5Home(period.getPeriod5Home());
                                            inPlayPeriodResponseDTO.setPeriod5Away(period.getPeriod5Away());

                                            inPlayPeriodResponseDTO.setPeriod6(period.getPeriod6());
                                            inPlayPeriodResponseDTO.setPeriod6Home(period.getPeriod6Home());
                                            inPlayPeriodResponseDTO.setPeriod6Away(period.getPeriod6Away());

                                            inPlayPeriodResponseDTO.setPeriod7(period.getPeriod7());
                                            inPlayPeriodResponseDTO.setPeriod7Home(period.getPeriod7Home());
                                            inPlayPeriodResponseDTO.setPeriod7Away(period.getPeriod7Away());

                                            inPlayPeriodResponseDTO.setPeriod8(period.getPeriod8());
                                            inPlayPeriodResponseDTO.setPeriod8Home(period.getPeriod8Home());
                                            inPlayPeriodResponseDTO.setPeriod8Away(period.getPeriod8Away());

                                            inPlayPeriodResponseDTO.setPeriod9(period.getPeriod9());
                                            inPlayPeriodResponseDTO.setPeriod9Home(period.getPeriod9Home());
                                            inPlayPeriodResponseDTO.setPeriod9Away(period.getPeriod9Away());

                                            inPlayPeriodResponseDTO.setPeriod10(period.getPeriod10());
                                            inPlayPeriodResponseDTO.setPeriod10Home(period.getPeriod10Home());
                                            inPlayPeriodResponseDTO.setPeriod10Away(period.getPeriod10Away());

                                            return inPlayPeriodResponseDTO;
                                        })
                                        .distinct()
                                        .collect(Collectors.toList());



                    InPlayGetFixtureResponseDTO responseDTO = new InPlayGetFixtureResponseDTO();
                    responseDTO.setMatchId(firstFixture.getMatchId());
                    responseDTO.setLeagueName(firstFixture.getLeagueName());
                    responseDTO.setLocationName(firstFixture.getLocationName());
                    responseDTO.setSportName(firstFixture.getSportsName());
                    responseDTO.setLeagueName(firstFixture.getLeagueName());
                    responseDTO.setStartDate(firstFixture.getStartDate());
                    responseDTO.setStatus(firstFixture.getStatus());
                    responseDTO.setHomeName(firstFixture.getHomeName());
                    responseDTO.setAwayName(firstFixture.getAwayName());
                    responseDTO.setHomeScore(firstFixture.getHomeScore());
                    responseDTO.setAwayScore(firstFixture.getAwayScore());
                    responseDTO.setPeriods(periods);
                    responseDTO.setMarkets(markets);
                    return responseDTO;
                })
                .distinct()
                .collect(Collectors.toList());
    }
}
