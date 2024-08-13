//package GInternational.server.management.bettingStatusByType.service;
//
//import GInternational.server.l_sport.betHistory.entity.BetHistory;
//import GInternational.server.l_sport.betHistory.repository.BetHistoryRepository;
//import GInternational.server.l_sport.betHistory.vo.OrderStatusEnum;
//import GInternational.server.management.bettingStatusByType.dto.BettingStatusByTypeDTO;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDate;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//public class BettingStatusByTypeService {
//
//    private final BetHistoryRepository betHistoryRepository;
//
//    public BettingStatusByTypeDTO calculateBetStats(LocalDate date) {
//        List<BetHistory> betHistories = betHistoryRepository.findAllByDate(date);
//
//        long Bet = betHistories.size();
//        long BetReward = betHistories.stream()
//                .filter(bet -> bet.getOrderStatus() == OrderStatusEnum.HIT)
//                .mapToLong(BetHistory::getBetReward)
//                .sum();
//
//        BettingStatusByTypeDTO dto = new BettingStatusByTypeDTO();
//        dto.setInPlayBet(Bet);
//        dto.setInPlayBetReward(BetReward);
//
//        return dto;
//    }
//}
