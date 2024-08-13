package GInternational.server.api.service;

import GInternational.server.api.entity.AmazonBonus;
import GInternational.server.api.repository.AmazonBonusRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonBonusService {

    private final AmazonBonusRepository amazonBonusRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    /**
     * 신규 파트너 첫 입금 시 보너스 적용.
     * 파트너가 첫 입금 시 받는 보너스를 계산하고 적용.
     *
     * @param userId 사용자 ID
     * @param depositAmount 입금액
     * @return 적용된 보너스 금액
     */
    public double applyFirstRechargeBonus(Long userId, double depositAmount) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RestControllerException(ExceptionCode.USER_NOT_FOUND)
        );
        Wallet wallet = walletRepository.findById(userId).orElseThrow(
                () -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND)
        );
        AmazonBonus amazonBonus = amazonBonusRepository.findFirstByOrderByIdDesc().orElseThrow(
                () -> new RestControllerException(ExceptionCode.SETTING_NOT_FOUND)
        );

        // 파트너가 첫 입금 보너스를 받지 않았다면 적용
        if (!wallet.hasReceivedFirstDepositBonus()) {
            double bonusRate = amazonBonus.getFirstRechargeRate();
            double bonusAmount = depositAmount * bonusRate / 100.0;

            wallet.setAmazonPoint((long) (wallet.getAmazonPoint() + bonusAmount));
            wallet.setReceivedFirstDepositBonus(true);
            walletRepository.save(wallet);
            return bonusAmount;
        }
        return 0;
    }

    /**
     * 일일 첫 입금 보너스 적용.
     * 사용자의 레벨에 따라 일일 첫 입금 보너스를 계산하고 적용.
     *
     * @param userId 사용자 ID
     * @param depositAmount 입금액
     * @return 적용된 보너스 금액
     */
    public double applyDailyFirstRechargeBonus(Long userId, double depositAmount) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RestControllerException(ExceptionCode.USER_NOT_FOUND)
        );
        Wallet wallet = walletRepository.findById(userId).orElseThrow(
                () -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND)
        );
        AmazonBonus amazonBonus = amazonBonusRepository.findFirstByOrderByIdDesc().orElseThrow(
                () -> new RestControllerException(ExceptionCode.SETTING_NOT_FOUND)
        );

        // 파트너가 일일 보너스를 받지 않았다면 적용
        if (!wallet.hasReceivedDailyBonus()) {
            long lv = user.getLv();
            Double dailyBonusRate = amazonBonus.getDailyFirstRechargeRate().get(lv);
            if (dailyBonusRate != null) {
                double bonusAmount = depositAmount * dailyBonusRate / 100.0;

                wallet.setAmazonPoint((long) (wallet.getAmazonPoint() + bonusAmount));
                wallet.setReceivedDailyBonus(true);
                walletRepository.save(wallet);
                return bonusAmount;
            }
        }
        return 0;
    }

    /**
     * 입금 보너스 적용.
     * 사용자의 레벨에 따른 입금 보너스를 계산하고 적용.
     *
     * @param userId 사용자 ID
     * @param depositAmount 입금액
     * @return 적용된 보너스 금액
     */
    public double applyRechargeBonus(Long userId, double depositAmount) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RestControllerException(ExceptionCode.USER_NOT_FOUND)
        );
        Wallet wallet = walletRepository.findById(userId).orElseThrow(
                () -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND)
        );
        AmazonBonus amazonBonus = amazonBonusRepository.findFirstByOrderByIdDesc().orElseThrow(
                () -> new RestControllerException(ExceptionCode.SETTING_NOT_FOUND)
        );

        long lv = user.getLv();
        Double rechargeBonusRate = amazonBonus.getRechargeRate().get(lv);
        if (rechargeBonusRate != null) {
            double bonusAmount = depositAmount * rechargeBonusRate / 100.0;
            // 일일 상한 검사
            Long dailyCap = amazonBonus.getDailyRechargeCap().get(lv);
            if (dailyCap != null) {
                bonusAmount = Math.min(bonusAmount, dailyCap);
            }

            wallet.setAmazonPoint((long) (wallet.getAmazonPoint() + bonusAmount));
            walletRepository.save(wallet);
            return bonusAmount;
        }
        return 0;
    }

    /**
     * 보너스 설정 적용 및 저장.
     * 첫 충전 보너스율, 일일 첫 충전 보너스율, 충전 보너스율, 일일 충전 한도를 설정하고 저장.
     *
     * @param firstRechargeRate 첫 충전 보너스율
     * @param dailyFirstRechargeRate 일일 첫 충전 보너스율
     * @param rechargeRate 충전 보너스율
     * @param dailyRechargeCap 일일 충전 한도
     */
    public void setBonusSettings(double firstRechargeRate,
                                 Map<Integer, Double> dailyFirstRechargeRate,
                                 Map<Integer, Double> rechargeRate,
                                 Map<Integer, Long> dailyRechargeCap,
                                 PrincipalDetails principalDetails) {
        AmazonBonus amazonBonus = new AmazonBonus();
        amazonBonus.setFirstRechargeRate(firstRechargeRate);
        amazonBonus.setDailyFirstRechargeRate(dailyFirstRechargeRate);
        amazonBonus.setRechargeRate(rechargeRate);
        amazonBonus.setDailyRechargeCap(dailyRechargeCap);
        amazonBonusRepository.save(amazonBonus);
    }

    /**
     * 매일 자정에 일일 첫 입금 보너스 수령 상태 초기화.
     * 모든 사용자의 일일 첫 입금 보너스 수령 상태를 false로 변경.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetDailyBonus() {
        List<Wallet> allWallets = walletRepository.findAll();
        allWallets.forEach(wallet -> wallet.setReceivedDailyBonus(false));
        walletRepository.saveAll(allWallets);
    }
}