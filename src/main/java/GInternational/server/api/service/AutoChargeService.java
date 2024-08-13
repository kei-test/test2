package GInternational.server.api.service;


import GInternational.server.api.dto.AutoChargeUserReqDTO;
import GInternational.server.api.entity.*;
import GInternational.server.api.repository.*;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.api.vo.TransactionGubunEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AutoChargeService {

    private final AutoTransactionRepository autoTransactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AutoDepositTransactionRepository autoDepositTransactionRepository;
    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final LevelBonusPointSettingRepository levelBonusPointSettingRepository;

    @Value("${secret.api-key}")
    private String secretApiKey;

    /**
     * 사용자에게 자동 충전을 진행.
     *
     * @param userId 사용자 ID
     * @param autoChargeReqDTO 자동 충전 요청 데이터
     */
    public void autoCharge(Long userId, AutoChargeUserReqDTO autoChargeReqDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
        Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow(
                () -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑 정보 없음"));
        String clientIp = request.getRemoteAddr();

        if (autoChargeReqDTO.getAmount() > 0) {
            // 오늘 날짜에 대한 첫 충전 여부 확인
            LocalDateTime startOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
            boolean isFirstRechargeToday = isFirstRecharge(user, startOfDay, endOfDay);

            // 보너스 설정 가져오기
            LevelBonusPointSetting bonusSetting = levelBonusPointSettingRepository.findByLv(user.getLv())
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND));

            double bonusPercentage;
            if (isFirstRechargeToday) {
                // 첫 충전인 경우 항상 firstRecharge 보너스 적용
                bonusPercentage = bonusSetting.getFirstRecharge();
            } else if (bonusSetting.isBonusActive()) {
                // 첫 충전이 아니고, bonusActive가 true인 경우에만 todayRecharge 보너스 적용
                bonusPercentage = bonusSetting.getTodayRecharge();
            } else {
                // 그 외의 경우 보너스 없음
                bonusPercentage = 0;
            }

            int calculatedBonus = (int) (autoChargeReqDTO.getAmount() * (bonusPercentage / 100));

            // 요청 바디에 bonus 필드가 있는 경우 해당 값 사용
            if (autoChargeReqDTO.getBonus() != null) {
                calculatedBonus = autoChargeReqDTO.getBonus();
            }

            RechargeTransaction rechargeTransaction = RechargeTransaction.builder()
                    .rechargeAmount(autoChargeReqDTO.getAmount())
                    .remainingSportsBalance(wallet.getSportsBalance() + autoChargeReqDTO.getAmount())
                    .bonus(calculatedBonus)
                    .remainingPoint((int) (wallet.getPoint() + calculatedBonus))
                    .user(user)
                    .lv(user.getLv())
                    .ip(clientIp)
                    .phone(user.getPhone())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .ownerName(wallet.getOwnerName())
                    .gubun(TransactionGubunEnum.SPORTS)
                    .status(TransactionEnum.UNREAD)
                    .site("test")
                    .chargedCount((int) wallet.getChargedCount())
                    .createdAt(LocalDateTime.now())
                    .isFirstRecharge(isFirstRechargeToday)
                    .build();
            rechargeTransactionRepository.save(rechargeTransaction);
        }
    }

    public void autoApproval(Long transactionId, String apiKey) {

        if (!secretApiKey.equals(apiKey)) {
            throw new RestControllerException(ExceptionCode.UNAUTHORIZED_ACCESS, "Invalid API Key");
        }

        RechargeTransaction rechargeTransaction = rechargeTransactionRepository.findById(transactionId).orElseThrow
                (() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "신청 내역이 없습니다."));
        User user = userRepository.findById(rechargeTransaction.getUser().getId()).orElseThrow
                (() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
        Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                (() -> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "금액 정보 없음"));

            if (!(rechargeTransaction.getStatus() == TransactionEnum.CANCELLATION)) {
                RechargeTransaction updatedRechargeTransaction = RechargeTransaction.builder()
                        .id(rechargeTransaction.getId())
                        .rechargeAmount(rechargeTransaction.getRechargeAmount())
                        .remainingSportsBalance(wallet.getSportsBalance() + rechargeTransaction.getRechargeAmount())
                        .ownerName(wallet.getOwnerName())
                        .user(user)
                        .lv(user.getLv())
                        .ip(rechargeTransaction.getIp())
                        .phone(user.getPhone())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .processedAt(LocalDateTime.now())
                        .gubun(TransactionGubunEnum.SPORTS)
                        .status(TransactionEnum.AUTO_APPROVAL)
                        .bonus(rechargeTransaction.getBonus())
                        .site("test")
                        .remainingPoint((int) (wallet.getPoint() + rechargeTransaction.getBonus()))
                        .chargedCount((int) (user.getWallet().getChargedCount() + 1))
                        .isFirstRecharge(rechargeTransaction.isFirstRecharge())
                        .build();

                rechargeTransactionRepository.save(updatedRechargeTransaction);

                //해당 유저의 금액정보 업데이트
                wallet.setSportsBalance(wallet.getSportsBalance() + rechargeTransaction.getRechargeAmount());
                wallet.setPoint(wallet.getPoint() + rechargeTransaction.getBonus());
                wallet.setChargedCount(wallet.getChargedCount() + 1);
                walletRepository.save(wallet);
            } else {
                throw new RestControllerException(ExceptionCode.INVALID_REQUEST);
            }
        }

    /**
     * 지정된 트랜잭션의 상태를 업데이트.
     *
     * @param transactionId 상태를 업데이트할 트랜잭션의 ID
     * @param newStatus 새로운 상태 값
     * @throws RestControllerException 해당 ID를 가진 트랜잭션이 존재하지 않을 경우 예외 발생
     */
    @AuditLogService.Audit("자동충전 신청건 상태 업데이트")
    public void updateTransactionStatus(Long transactionId, TransactionEnum newStatus, PrincipalDetails principalDetails, HttpServletRequest request) {
        AutoTransaction transaction = autoTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.APPLIED_TRANSACTION_NOT_FOUNT, "트랜잭션을 찾을 수 없습니다."));

        transaction.setStatus(newStatus);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(transaction.getUser().getId()));
        context.setUsername(transaction.getUser().getUsername());
        context.setDetails("자동충전 신청건 상태 업데이트 - 신청자 ID: " + transaction.getUser().getUsername());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        autoTransactionRepository.save(transaction);
    }

    /**
     * 주어진 조건에 따라 처리되지 않은 자동 거래들의 상태를 업데이트.
     */
    @Scheduled(fixedDelay = 1800000) // 30분 마다 상태 체크 후 상태값 업데이트
    public void unProcessedAutoTransactions() {
        List<AutoTransaction> unprocessedAutoTransaction = getWaitStatusAutoTransactions();
        updateStatusToTimeout(unprocessedAutoTransaction);
    }

    /**
     * 처리되지 않은 자동 거래들을 조회.
     *
     * @return 대기 상태인 자동 거래 목록
     */
    private List<AutoTransaction> getWaitStatusAutoTransactions() {
        return autoTransactionRepository.searchByAutoTransactionCondition();
    }

    /**
     * 대기 상태인 자동 거래들의 상태를 타임아웃으로 업데이트.
     *
     * @param autoTransactions 타임아웃으로 변경할 자동 거래 목록
     */
    private void updateStatusToTimeout(List<AutoTransaction> autoTransactions) {
        for (AutoTransaction autoTransaction : autoTransactions) {
            autoTransaction.setStatus(TransactionEnum.TIMEOUT);
            autoTransactionRepository.save(autoTransaction);
        }
    }

    /**
     * 처리되지 않은 자동 예금 거래들의 상태를 체크하고 업데이트.
     */
    @Scheduled(fixedDelay = 1800000) // 30분 마다 상태 체크 후 상태값 업데이트
    public void unProcessedAutoDepositTransactions() {
        List<AutoDepositTransaction> unprocessedAutoDepositTransaction = getWaitStatusAutoDepositTransactions();
        updateDepositStatusToTimeout(unprocessedAutoDepositTransaction);
    }

    /**
     * 대기 상태인 자동 예금 거래들을 조회.
     *
     * @return 대기 상태인 자동 예금 거래 목록
     */
    private List<AutoDepositTransaction> getWaitStatusAutoDepositTransactions() {
        return autoDepositTransactionRepository.searchByAutoDepositTransactionCondition();
    }

    /**
     * 대기 상태인 자동 예금 거래들의 상태를 타임아웃으로 업데이트.
     *
     * @param autoDepositTransactions 타임아웃으로 변경할 자동 예금 거래 목록
     */
    private void updateDepositStatusToTimeout(List<AutoDepositTransaction> autoDepositTransactions) {
        for (AutoDepositTransaction autoDepositTransaction : autoDepositTransactions) {
            autoDepositTransaction.setStatus(TransactionEnum.TIMEOUT);
            autoDepositTransactionRepository.save(autoDepositTransaction);
        }
    }

    /**
     * 해당 사용자의 첫 충전 여부를 확인.
     *
     * @param user 사용자 엔티티
     * @param startOfDay 오늘의 시작 시간
     * @param endOfDay 오늘의 종료 시간
     * @return 첫 충전 여부
     */
    private boolean isFirstRecharge(User user, LocalDateTime startOfDay, LocalDateTime endOfDay) {
        return !rechargeTransactionRepository.existsByUserAndStatusAndProcessedAtBetween(
                user,
                TransactionEnum.APPROVAL,
                startOfDay,
                endOfDay
        );
    }
}