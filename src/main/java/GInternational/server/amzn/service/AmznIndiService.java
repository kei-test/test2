package GInternational.server.amzn.service;





import GInternational.server.amzn.dto.indi.business.AmznPartnerWalletDTO;
import GInternational.server.amzn.dto.indi.calculate.AmznIndiTotalCalculateDTO;
import GInternational.server.amzn.dto.indi.calculate.UserBetweenCalculateDTO;
import GInternational.server.amzn.dto.indi.indi_response.AmznIndiPartnerResDTO;
import GInternational.server.amzn.repo.AmznIndiRepositoryImpl;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(value = "clientServerTransactionManager")
public class AmznIndiService {


    private final AmznIndiRepositoryImpl amznIndiRepositoryImpl;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());




    public AmznPartnerWalletDTO getPartnerWallet(PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        if (user.getId().equals(principalDetails.getUser().getId())) {
            return amznIndiRepositoryImpl.getPartnerWallet(user.getId());
        } return null;
    }





    public List<AmznIndiPartnerResDTO> getIndiResults(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        User user = userRepository.findById(principalDetails.getUser().getId()).orElse(null);

        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        List<AmznIndiPartnerResDTO> r1 = amznIndiRepositoryImpl.getResults(startDate, endDate);
        List<AmznIndiPartnerResDTO> responseList = new ArrayList<>();

        if (user.getRole().equals("ROLE_ADMIN") || user.getPartnerType().equals("대본사")) {
            return r1;
        } else if (user.getPartnerType().equals("본사")) {
            for (AmznIndiPartnerResDTO r : r1) {
                if (r.getId().equals(user.getId()) && r.getPartnerType().equals("본사")) {
                    responseList.add(r);
                }
                boolean isReferredByInResponseList = responseList.stream()
                        .anyMatch(res -> res.getUsername().equals(r.getReferredBy()));
                if (isReferredByInResponseList) {
                    responseList.add(r);
                }
            }
            return responseList;
        } else if (user.getPartnerType().equals("부본사")) {
            for (AmznIndiPartnerResDTO r : r1) {
                if (r.getId().equals(user.getId()) && r.getPartnerType().equals("부본사")) {
                    responseList.add(r);
                }
                boolean isReferredByInResponseList = responseList.stream()
                        .anyMatch(res -> res.getUsername().equals(r.getReferredBy()));
                if (isReferredByInResponseList) {
                    responseList.add(r);
                }
            }
            return responseList;
        } else if (user.getPartnerType().equals("총판")) {
            for (AmznIndiPartnerResDTO r : r1) {
                if (r.getId().equals(user.getId()) && r.getPartnerType().equals("총판")) {
                    responseList.add(r);
                }
                boolean isReferredByInResponseList = responseList.stream()
                        .anyMatch(res -> res.getUsername().equals(r.getReferredBy()));
                if (isReferredByInResponseList) {
                    responseList.add(r);
                }
            }
            return responseList;
        } else if (user.getPartnerType().equals("매장")) {
            for (AmznIndiPartnerResDTO r : r1) {
                if (r.getId().equals(user.getId()) && r.getPartnerType().equals("매장")) {
                    responseList.add(r);
                }
                boolean isReferredByInResponseList = responseList.stream()
                        .anyMatch(res -> res.getUsername().equals(r.getReferredBy()));
                if (isReferredByInResponseList) {
                    responseList.add(r);
                }
            }
            return responseList;
        } return null;
    }





    public AmznIndiTotalCalculateDTO getCalculate(LocalDate startDate, LocalDate endDate,PrincipalDetails principalDetails) {
        List<AmznIndiPartnerResDTO> objLists = getIndiResults(startDate,endDate,principalDetails);
        AmznIndiTotalCalculateDTO response = new AmznIndiTotalCalculateDTO();
        long totalRolling = 0L;

        //유저
        long totalUserSportsBalance = 0L;
        long totalUserPoint = 0L;
        long totalUserRecharge = 0L;
        long totalUserExchange = 0L;
        long totalUserSettlement = 0L;

        //파트너
        long totalPartnerAmazonMoney = 0L;
        long totalPartnerAmazonMileage = 0L;
        long totalPartnerAmazonRecharge = 0L;
        long totalPartnerAmazonExchange = 0L;
        long totalPartnerSettlement = 0L;

        //슬롯
        long totalSlotBetAmount = 0L;
        long totalSlotWinningAmount = 0L;
        long totalSlotSettlement = 0L;
        long totalSlotRollingAmount = 0L;

        //카지노
        long totalCasinoBetAmount = 0L;
        long totalCasinoWinningAmount = 0L;
        long totalCasinoSettlement = 0L;
        long totalCasinoRollingAmount = 0L;

        //스포츠
        long totalSportBetAmount = 0L;
        long totalSportWinningAmount = 0L;
        long totalSportSettlement = 0L;
        long totalSportRollingAmount = 0L;

        //아케이드
        long totalArcadeBetAmount = 0L;
        long totalArcadeWinningAmount = 0L;
        long totalArcadeSettlement = 0L;
        long totalArcadeRollingAmount = 0L;


        for (AmznIndiPartnerResDTO r : objLists) {
            totalRolling += r.getAmazonRollingAmount();

            totalUserSportsBalance += r.getUsers().getSportsBalance();
            totalUserPoint += r.getUsers().getPoint();
            totalUserRecharge += r.getUsers().getRechargeAmount();
            totalUserExchange += r.getUsers().getExchangeAmount();
            totalUserSettlement += r.getUsers().getTotalSettlement();

            totalPartnerAmazonMoney += r.getAmazonMoney();
            totalPartnerAmazonMileage += r.getAmazonMileage();
            totalPartnerAmazonRecharge += r.getRechargeAmount();
            totalPartnerAmazonMoney += r.getExchangeAmount();
            totalPartnerSettlement += r.getTotalSettlement();

            totalSlotBetAmount += r.getSlot().getDebitAmount();
            totalSlotWinningAmount += r.getSlot().getCreditAmount();
            totalSlotSettlement += r.getSlot().getTotalSettlement();
            totalSlotRollingAmount += r.getSlot().getPartnerRollingAmount();

            totalCasinoBetAmount += r.getCasino().getDebitAmount();
            totalCasinoWinningAmount += r.getCasino().getCreditAmount();
            totalCasinoSettlement += r.getCasino().getTotalSettlement();
            totalCasinoRollingAmount += r.getCasino().getPartnerRollingAmount();

            totalSportBetAmount += r.getSport().getCvtBetAmount();
            totalSportWinningAmount += r.getSport().getCvtBetReward();
            totalSportSettlement += r.getSport().getTotalSettlement();
            totalSportRollingAmount += r.getSport().getPartnerRollingAmount();

            totalArcadeBetAmount += r.getArcade().getDebitAmount();
            totalArcadeWinningAmount += r.getArcade().getCreditAmount();
            totalArcadeSettlement += r.getArcade().getTotalSettlement();
            totalArcadeRollingAmount += r.getArcade().getPartnerRollingAmount();
        }

        response.setTotalRolling(totalRolling);
        response.setTotalUserSportsBalance(totalUserSportsBalance);
        response.setTotalUserPoint(totalUserPoint);
        response.setTotalUserRecharge(totalUserRecharge);
        response.setTotalUserExchange(totalUserExchange);
        response.setTotalUserSettlement(totalUserSettlement);

        response.setTotalPartnerAmazonMoney(totalPartnerAmazonMoney);
        response.setTotalPartnerAmazonMileage(totalPartnerAmazonMileage);
        response.setTotalPartnerAmazonRecharge(totalPartnerAmazonRecharge);
        response.setTotalPartnerAmazonExchange(totalPartnerAmazonExchange);
        response.setTotalPartnerSettlement(totalPartnerSettlement);

        response.setTotalSlotBetAmount(totalSlotBetAmount);
        response.setTotalSlotWinningAmount(totalSlotWinningAmount);
        response.setTotalSlotSettlement(totalSlotSettlement);
        response.setTotalSlotRollingAmount(totalSlotRollingAmount);

        response.setTotalCasinoBetAmount(totalCasinoBetAmount);
        response.setTotalCasinoWinningAmount(totalCasinoWinningAmount);
        response.setTotalCasinoSettlement(totalCasinoSettlement);
        response.setTotalCasinoRollingAmount(totalCasinoRollingAmount);

        response.setTotalSportBetAmount(totalSportBetAmount);
        response.setTotalSportWinningAmount(totalSportWinningAmount);
        response.setTotalSportSettlement(totalSportSettlement);
        response.setTotalSportRollingAmount(totalSportRollingAmount);

        response.setTotalArcadeBetAmount(totalArcadeBetAmount);
        response.setTotalArcadeWinningAmount(totalArcadeWinningAmount);
        response.setTotalArcadeSettlement(totalArcadeSettlement);
        response.setTotalArcadeRollingAmount(totalArcadeRollingAmount);
        return response;
    }


    public UserBetweenCalculateDTO getUserCalculate(Long userId,LocalDate startDate, LocalDate endDate,PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        if (user.getRole().equals("ROLE_ADMIN")|| user.getPartnerType() != null) {
            return amznIndiRepositoryImpl.getUserCalculate(userId,startDate,endDate);
        } return null;
    }
}






