package GInternational.server.amzn.service;

import GInternational.server.amzn.dto.indi.business.AmznPartnerRollingInfo;
import GInternational.server.amzn.dto.indi.business.AmznRollingTransactionResDTO;
import GInternational.server.amzn.dto.indi.business.AmznTotalRollingAmountDTO;
import GInternational.server.amzn.repo.AmazonRollingTransactionRepository;
import GInternational.server.amzn.repo.AmznRollingTransactionRepositoryImpl;
import GInternational.server.api.entity.AmazonRollingTransaction;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(value = "clientServerTransactionManager")
public class AmznRollingTransactionService {


    private final AmznRollingTransactionRepositoryImpl amznRollingTransactionRepositoryImpl;
    private final AmazonRollingTransactionRepository amazonRollingTransactionRepository;
    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    //롤링지급 내역생성
    public AmazonRollingTransaction createTransaction(User player,
                                                      String bettingCategory,
                                                      int betAmount,
                                                      long cvtAmount,
                                                      Long partnerId,
                                                      Debit savedDebit,
                                                      Wallet partnerWallet) {
        User user = userRepository.findById(partnerId).orElse(null);
        AmazonRollingTransaction trs = new AmazonRollingTransaction();
        trs.setCategory(bettingCategory);
        trs.setUsername(player.getUsername());
        trs.setNickname(player.getNickname());
        trs.setBetAmount(betAmount);
        trs.setRollingAmount(cvtAmount);  //형변환하여 소수점 제거
        trs.setRemainingAmazonMoney(partnerWallet.getAmazonMoney());
        trs.setRemainingAmazonMileage(partnerWallet.getAmazonMileage());
        trs.setBetTime(LocalDateTime.now());
        trs.setProcessedAt(LocalDateTime.now());
        trs.setUser(user); //추천인
        trs.setWallet(user.getWallet()); //아마존 총판 지갑정보
        return amazonRollingTransactionRepository.save(trs);
    }


    //특정 파트너의 롤링 지급내역 조회
    public List<AmznRollingTransactionResDTO> getIndiRollingTransaction(Long userId, String category, LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        User betUser = userRepository.findById(userId).orElse(null);

        long tatalBetAmount = 0;
        long totalRollingAmount = 0;

        if (startDate == null) {startDate = LocalDate.now();}
        if (endDate == null) {endDate = LocalDate.now();}


        if (user.getRole().equals("ROLE_ADMIN") || user.getPartnerType() != null) {
            List<AmznRollingTransactionResDTO> response =  amznRollingTransactionRepositoryImpl.searchByRollingTransactions(userId, category, startDate, endDate);
            for (AmznRollingTransactionResDTO obj : response) {
                tatalBetAmount += (long) obj.getBetAmount();
                totalRollingAmount += obj.getRollingAmount();
                obj.setTotalBetAmount(totalRollingAmount); //총 베팅금
                obj.setTotalRollingAmount(totalRollingAmount); //총 롤링 지급금
                obj.setBetUserId(betUser.getId()); // v 추가된 부분
            }
            return response;
        }
        return null;
    }


    public AmznTotalRollingAmountDTO getTotalRollingTransaction(Long userId,String category,PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        if (user.getRole().equals("ROLE_ADMIN") || user.getPartnerType() != null) {
            AmznTotalRollingAmountDTO response = amznRollingTransactionRepositoryImpl.getTotalRollingTransaction(userId,category);
            response.setTotalBetAmount((long) response.getRawBetAmount());
            return response;
        }
        return null;
    }







    //특정 파트너의 롤링 지급내역 조회화면의 왼상단 파트너 요율 조회
    public AmznPartnerRollingInfo getPartnerRollingInfo(Long id, PrincipalDetails principalDetails) {
        User user = userRepository.findByUsername(principalDetails.getUsername());
        if (user.getRole().equals("ROLE_ADMIN") || user.getPartnerType() != null) {
            return amznRollingTransactionRepositoryImpl.searchByPartnerRollingInfo(id);
        } return null;
    }



}