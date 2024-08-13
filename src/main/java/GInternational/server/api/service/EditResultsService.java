package GInternational.server.api.service;

import GInternational.server.api.dto.EditPreMatchDataDTO;
import GInternational.server.api.dto.EditPreMatchDataList;
import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.entity.MoneyLog;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.BetHistoryRepository;
import GInternational.server.api.repository.MoneyLogRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.l_sport.batch.job.dto.edit.EditMatchResponseDTO;
import GInternational.server.l_sport.info.dto.results.GameResultListDTO;
import GInternational.server.l_sport.info.entity.*;
import GInternational.server.l_sport.info.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class EditResultsService {

    private final FixtureRepository fixtureRepository;
    private final OddRepository oddRepository;
    private final OddLiveRepository oddLiveRepository;
    private final BetHistoryRepository betHistoryRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final MoneyLogRepository moneyLogRepository;




    public List<EditMatchResponseDTO> searchByEditData(Long type) {

        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter currentFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedStartTime = currentDateTime.format(currentFormatter);

        LocalDateTime endDateDateTime = currentDateTime.minusDays(3);
        DateTimeFormatter endFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String formattedEndTime = endDateDateTime.format(endFormatter);

        List<GameResultListDTO> pages = fixtureRepository.searchByEndedMatchData(type,formattedEndTime,formattedStartTime);
        List<EditMatchResponseDTO> transformedPages = EditMatchResponseDTO.transform(pages);
        return transformedPages.stream().distinct().collect(Collectors.toList());
    }




    //결과 수정
    public void updatePreMatchData(List<EditPreMatchDataList> editMatchDataRequestDTO) {
        List<Odd> oddUpdateList = new ArrayList<>();
        List<OddLive> oddLiveUpdateList = new ArrayList<>();
        List<Match> matchUpdateList = new ArrayList<>();
        LinkedList<BetHistory> betHistories = new LinkedList<>();
        LinkedList<BetHistory> betGroupIds = new LinkedList<>();
        Set<Long> existingBetGroupIds = new HashSet<>();


        for (EditPreMatchDataList editMatchListDataDTO : editMatchDataRequestDTO) {
            String matchId = editMatchListDataDTO.getMatchId();
            String windOrOverBetIdx = editMatchListDataDTO.getWinOrOverIdx();
            String drawOrBaseLineBetIdx = editMatchListDataDTO.getDrawOrBaseLineIdx();
            String loseOrUnderBetIdx = editMatchListDataDTO.getLoseOrUnderIdx();
            String marketName = editMatchListDataDTO.getMarketName();

            //1개의 경기의 특정 마켓에 해당하는 모든 배당의 결과값을 전부 초기화시켜야 중복된 settlement 가 발생하지않는다.
            Odd winOdd = oddRepository.findByIdxAndMatchId(windOrOverBetIdx, matchId);
            if (winOdd != null) {
                winOdd.setIsModified("N");  //운영에 따라 변경되는 파라미터 1-2
                winOdd.setSettlement(editMatchListDataDTO.getWinOrOverSettlement());
                oddUpdateList.add(winOdd);
            }
            Odd drawOdd = oddRepository.findByIdxAndMatchId(drawOrBaseLineBetIdx,matchId);
            if (drawOdd != null) {
                drawOdd.setIsModified("N");  //운영에 따라 변경되는 파라미터 1-2
                drawOdd.setSettlement(editMatchListDataDTO.getDrawOrBaseLineSettlement());
                oddUpdateList.add(drawOdd);
            }
            Odd loseOdd = oddRepository.findByIdxAndMatchId(loseOrUnderBetIdx,matchId);
            if (loseOdd != null) {
                loseOdd.setIsModified("N");  //운영에 따라 변경되는 파라미터 1-2
                loseOdd.setSettlement(editMatchListDataDTO.getLoseOrUnderSettlement());
                oddUpdateList.add(loseOdd);
            }

            OddLive winOddLive = oddLiveRepository.findByIdxAndMatchId(windOrOverBetIdx, matchId);
            if (winOddLive != null) {
                winOddLive.setIsModified("N");  //운영에 따라 변경되는 파라미터 1-2
                winOddLive.setSettlement(editMatchListDataDTO.getWinOrOverSettlement());
                oddLiveUpdateList.add(winOddLive);
            }
            OddLive drawOddLive = oddLiveRepository.findByIdxAndMatchId(drawOrBaseLineBetIdx,matchId);
            if (drawOddLive != null) {
                drawOddLive.setIsModified("N");  //운영에 따라 변경되는 파라미터 1-2
                drawOddLive.setSettlement(editMatchListDataDTO.getDrawOrBaseLineSettlement());
                oddLiveUpdateList.add(drawOddLive);
            }
            OddLive loseOddLive = oddLiveRepository.findByIdxAndMatchId(loseOrUnderBetIdx,matchId);
            if (loseOddLive != null) {
                loseOddLive.setIsModified("N");  //운영에 따라 변경되는 파라미터 1-2
                loseOddLive.setSettlement(editMatchListDataDTO.getLoseOrUnderSettlement());
                oddLiveUpdateList.add(loseOddLive);
            }


            Match match = fixtureRepository.findByMatchId(matchId).orElse(null);
            if (match != null) {
                String homeScore = editMatchListDataDTO.getHomeScore();
                String awayScore = editMatchListDataDTO.getAwayScore();
                if (homeScore != null) match.setHomeScore(homeScore);
                if (awayScore != null) match.setAwayScore(awayScore);
                match.setStatus(editMatchListDataDTO.getStatus());
                matchUpdateList.add(match);


                LinkedList<BetHistory> betHistory = betHistoryRepository.findDistinctByMatchIdAndMarketName(matchId, marketName);
                long tempProfit = 0;
                for (BetHistory history : betHistory) {
                    if (!existingBetGroupIds.contains(history.getBetGroupId())) {
                        if (history.getBetReward() == null) {
                            double value = 0.0;
                            tempProfit = 0;
                        } else if (history.getBetReward() != null) {
                            double value = Double.parseDouble(history.getBetReward());
                            tempProfit = (long) value;
                        }
                        Wallet wallet = walletRepository.findByUserId(history.getUser().getId()).orElse(null);
                        wallet.setSportsBalance(wallet.getSportsBalance() - tempProfit);
                        walletRepository.save(wallet);
                        existingBetGroupIds.add(history.getBetGroupId());
                        if (history.getBetReward() != null) {
                            MoneyLog moneyLog = MoneyLog.builder()
                                    .username(wallet.getUser().getUsername())
                                    .nickname(wallet.getUser().getNickname())
                                    .user(wallet.getUser())
                                    .category(MoneyLogCategoryEnum.회수)
                                    .usedSportsBalance(tempProfit)
                                    .finalSportsBalance(wallet.getSportsBalance())
                                    .bigo(history.getBetGroupId() + "(SPORT)")
                                    .site("test")
                                    .build();
                            moneyLogRepository.save(moneyLog);
                        }
                    } else System.out.println("이미 중복된 group_id가 있습니다 : "+existingBetGroupIds);
                    history.setProcessedAt(null);
                    history.setSettlement(null);
                    Long bgId = history.getBetGroupId();

                    List<BetHistory> betOrders = betHistoryRepository.findByBetGroupId(bgId);
                    for (BetHistory betObject : betOrders) {
                        betObject.setBetReward(null);
                        betGroupIds.add(betObject);
                    }
                    history.setOrderStatus(OrderStatusEnum.WAITING);
                    history.setApi("Y");
                    history.setMatchStatus(editMatchListDataDTO.getStatus());
                    betHistories.add(history);
                }
            }
        }
        oddLiveRepository.saveAll(oddLiveUpdateList);
        oddRepository.saveAll(oddUpdateList);
        fixtureRepository.saveAll(matchUpdateList);
        betHistoryRepository.saveAll(betHistories);
        betHistoryRepository.saveAll(betGroupIds);
    }
}