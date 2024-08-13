package GInternational.server.api.service;

import GInternational.server.api.dto.CompResponseDTO;
import GInternational.server.api.dto.RollingResponseDTO;
import GInternational.server.api.entity.*;
import GInternational.server.api.repository.*;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.api.vo.RollingTransactionEnum;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.kplay.debit.repository.DebitRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class CompService {

    private final DebitRepository debitRepository;
    private final CompTransactionRepository compTransactionRepository;
    private final WalletRepository walletRepository;
    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final PointLogService pointLogService;
    private final UserRepository userRepository;
    private final CompRewardRateRepository compRewardRateRepository;

    private static final BigDecimal MINIMUM_BET_AMOUNT = new BigDecimal("50000"); // 최소 베팅 금액 상수화
    public static final List<Integer> VALID_PRD_IDS = IntStream.rangeClosed(1, 99)
            .boxed()
            .collect(Collectors.toList());

    /**
     * 롤링 적립 신청을 처리. 사용자의 역할을 검증한 후 하루에 한 번만 신청 가능한지 확인.
     * 이후 사용자의 어제의 총 베팅 금액과 충전 금액을 계산하고, 베팅 금액과 사용자 레벨에 따라 보상을 계산.
     * 그런 다음 롤링 트랜잭션을 생성하고 승인 상태로 저장. 최종적으로 사용자의 지갑에 보상 포인트를 업데이트.
     *
     * @param principalDetails 인증된 사용자의 세부 정보를 포함하는 PrincipalDetails 객체.
     * @return RollingResponseDTO 롤링 적립 신청에 대한 응답 데이터.
     */
    public CompResponseDTO applyForRolling(PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(principalDetails.getUser().getId()).orElseThrow(()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        Wallet wallet = walletRepository.findByUserId(user.getId()).orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND));

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        boolean alreadyApplied = compTransactionRepository.existsByUserAndCreatedAtGreaterThanEqual(user, today.atStartOfDay());
        if (alreadyApplied) {
            throw new RestControllerException(ExceptionCode.ALREADY_APPLIED_TODAY, "오늘 이미 신청했습니다.");
        }

        String clientIp = request.getRemoteAddr();

        BigDecimal totalBalanceRechargedYesterday = calculateYesterdayTotalRechargedBalance();
        BigDecimal totalBetAmountFromYesterday = calculateYesterdayTotalBetAmount(user.getAasId());
        if (totalBetAmountFromYesterday.compareTo(MINIMUM_BET_AMOUNT) < 0) {
            throw new RestControllerException(ExceptionCode.BET_AMOUNT_TOO_LOW, "어제 카지노 게임에서 배팅한 총 금액이 5만원 미만이면 신청 불가합니다.");
        }
        if (user.getLv() == 8 || user.getLv() == 9) {
            throw new RestControllerException(ExceptionCode.USER_LEVEL_NOT_ALLOWED, "8,9 레벨은 신청 불가합니다.");
        }

        Optional<CompRewardRate> rewardRateOpt = compRewardRateRepository.findById(user.getLv());

        if (!rewardRateOpt.isPresent()) {
            throw new RestControllerException(ExceptionCode.INVALID_USER_LEVEL, "접근 권한이 없는 레벨입니다.");
        }

        BigDecimal rewardPercentage = rewardRateOpt.get().getRate();
        BigDecimal rawReward = totalBetAmountFromYesterday.multiply(rewardPercentage);

        BigDecimal calculatedReward = rawReward
                .setScale(0, RoundingMode.DOWN)
                .divide(new BigDecimal("10"), 0, RoundingMode.DOWN)
                .multiply(new BigDecimal("10"));

        CompTransaction savedTransaction = createAndApproveCompTransaction(user, totalBetAmountFromYesterday, calculatedReward, totalBalanceRechargedYesterday, rewardPercentage, clientIp);

        wallet.setPoint(wallet.getPoint() + calculatedReward.intValue());
        walletRepository.save(wallet);

        pointLogService.recordPointLog(user.getId(), calculatedReward.longValue(), PointLogCategoryEnum.슬롯롤링적립, clientIp, "");

        return new CompResponseDTO(
                savedTransaction.getId(),
                user.getId(),
                user.getLv(),
                user.getUsername(),
                user.getNickname(),
                savedTransaction.getCreatedAt(),
                savedTransaction.getProcessedAt(),
                totalBalanceRechargedYesterday,
                calculatedReward,
                rewardPercentage,
                totalBetAmountFromYesterday,
                wallet.getSportsBalance(),
                wallet.getCasinoBalance(),
                savedTransaction.getStatus().toString(),
                clientIp
        );
    }

    /**
     * 사용자의 베팅 금액, 계산된 보상, 어제의 충전 금액을 기반으로 롤링 트랜잭션을 생성하고 바로 승인 상태로 저장.
     *
     * @param user 사용자 객체.
     * @param betAmount 어제의 총 베팅 금액.
     * @param calculatedReward 계산된 보상 금액.
     * @param lastDayChargeBalance 어제의 충전 금액.
     * @return RollingTransaction 저장된 롤링 트랜잭션 객체.
     */
    private CompTransaction createAndApproveCompTransaction(User user, BigDecimal betAmount, BigDecimal calculatedReward, BigDecimal lastDayChargeBalance, BigDecimal rate, String clientIp) {
        CompTransaction compTransaction = new CompTransaction();
        compTransaction.setUserId(user.getId());
        compTransaction.setLv(user.getLv());
        compTransaction.setLastDayAmount(betAmount);
        compTransaction.setCalculatedReward(calculatedReward);
        compTransaction.setRate(rate);
        compTransaction.setLastDayChargeSportsBalance(lastDayChargeBalance);
        compTransaction.setStatus(RollingTransactionEnum.APPROVAL);
        compTransaction.setCasinoBalance(user.getWallet().getCasinoBalance());
        compTransaction.setSportsBalance(user.getWallet().getSportsBalance());
        compTransaction.setUserIp(clientIp);
        compTransaction.setUsername(user.getUsername());
        compTransaction.setNickname(user.getNickname());
        compTransaction.setCreatedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));
        compTransaction.setProcessedAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")));

        return compTransactionRepository.save(compTransaction);
    }

    /**
     * 주어진 사용자 ID에 해당하는 사용자가 어제 날짜에 베팅한 총 금액을 계산.
     *
     * @param aasId 사용자 ID.
     * @return BigDecimal 어제 날짜에 베팅한 총 금액.
     */
    public BigDecimal calculateYesterdayTotalBetAmount(Integer aasId) {
        LocalDateTime startOfYesterday = RollingDateUtils.getStartOfYesterday("Asia/Seoul");
        LocalDateTime endOfYesterday = RollingDateUtils.getEndOfYesterday("Asia/Seoul");

        List<Debit> yesterdaysDebits = debitRepository.findAllByUserIdAndCreatedDateBetween(aasId, startOfYesterday, endOfYesterday)
                .stream()
                .filter(debit -> VALID_PRD_IDS.contains(debit.getPrd_id()))
                .collect(Collectors.toList());

        return yesterdaysDebits.stream()
                .map(debit -> BigDecimal.valueOf(debit.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 어제 날짜에 승인된 거래를 찾아 충전된 총 balance 계산.
     *
     * @return BigDecimal 어제 날짜에 충전된 총 balance.
     */
    public BigDecimal calculateYesterdayTotalRechargedBalance() {
        LocalDateTime startOfYesterday = RollingDateUtils.getStartOfYesterday("Asia/Seoul");
        LocalDateTime endOfYesterday = RollingDateUtils.getEndOfYesterday("Asia/Seoul");

        List<RechargeTransaction> yesterdaysRechargeTransactions = rechargeTransactionRepository.findAllByProcessedAtBetweenAndStatus(
                startOfYesterday, endOfYesterday, TransactionEnum.APPROVAL);

        return yesterdaysRechargeTransactions.stream()
                .map(transaction -> BigDecimal.valueOf(transaction.getRechargeAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
