package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonRechargeProcessedRequestDTO;
import GInternational.server.api.dto.AmazonRechargeRequestDTO;
import GInternational.server.api.entity.AmazonRechargeTransaction;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.mapper.AmazonRechargeRequestMapper;
import GInternational.server.api.mapper.AmazonRechargeResponseMapper;
import GInternational.server.api.repository.AmazonRechargeTransactionRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.vo.AmazonTransactionEnum;
import GInternational.server.api.vo.TradeLogCategory;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonRechargeService {

    private final UserRepository userRepository;
    private final AmazonRechargeTransactionRepository amazonRechargeTransactionRepository;
    private final AmazonRechargeRequestMapper amazonRechargeRequestMapper;
    private final AmazonRechargeResponseMapper amazonRechargeResponseMapper;
    private final AmazonBonusService amazonBonusService;
    private final WalletRepository walletRepository;
    private final TradeLogService tradeLogService;

    /**
     * 사용자의 충전 신청을 처리. 충전 금액이 유효할 경우 충전 기록을 생성하고, 충전 금액을 사용자 지갑에 반영.
     *
     * @param userId 사용자 ID
     * @param walletId 지갑 ID
     * @param amazonRechargeRequestDTO 충전 요청 데이터
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     */
    public void rechargeMoney(Long userId, Long walletId, AmazonRechargeRequestDTO amazonRechargeRequestDTO, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("유저 정보 없음"));
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(()-> new RuntimeException("금액 정보 없음"));

        String clientIp = request.getRemoteAddr();

        long rechargeAmount = amazonRechargeRequestDTO.getRechargeAmount();
        if (amazonRechargeRequestDTO.getRechargeAmount() > 0) {

            amazonBonusService.applyFirstRechargeBonus(userId, rechargeAmount);
            amazonRechargeRequestMapper.toEntity(amazonRechargeRequestDTO);

            //요청 기록 생성
            AmazonRechargeTransaction amazonRechargeTransaction = AmazonRechargeTransaction.builder()
                    .rechargeAmount(amazonRechargeRequestDTO.getRechargeAmount())
                    .user(user)
                    .lv(user.getLv())
                    .ip(clientIp)
                    .phone(user.getPhone())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .ownerName(wallet.getOwnerName())
                    .status(AmazonTransactionEnum.UNREAD)
                    .chargedCount((int) wallet.getChargedCount())
                    .createdAt(LocalDateTime.now())
                    .build();
            amazonRechargeTransactionRepository.save(amazonRechargeTransaction);

            amazonRechargeResponseMapper.toDto(wallet);
        } else {
            throw new RuntimeException("충전 금액이 0보다 커야하고 지갑 잔액이 충분해야 합니다.");
        }
    }

    /**
     * 충전 거래의 상태를 대기 중(WAITING)으로 변경. 대기 중 상태로 변경은 입금 미확인(UNREAD) 상태의 거래에만 적용.
     *
     * @param transactionId 거래 ID
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     */
    public void updateTransactionStatusToWaiting(Long transactionId, PrincipalDetails principalDetails) {
        AmazonRechargeTransaction amazonRechargeTransaction = amazonRechargeTransactionRepository.findById(transactionId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "내역 없음"));

        if (amazonRechargeTransaction.getStatus() != AmazonTransactionEnum.UNREAD) {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "UNREAD 상태의 신청건만 변경 가능합니다.");
        }

        amazonRechargeTransaction.setStatus(AmazonTransactionEnum.WAITING);
        amazonRechargeTransactionRepository.save(amazonRechargeTransaction);
    }

    /**
     * 관리자에 의한 충전 거래 승인 처리. 대기 중인 충전 거래에 대해 승인 처리를 하여 사용자의 충전 금액을 최종적으로 반영.
     *
     * @param transactionIds 승인할 거래 ID 목록
     * @param amazonRechargeProcessedRequestDTO 처리된 충전 요청 데이터
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     */
    public void updateMoney(List<Long> transactionIds, AmazonRechargeProcessedRequestDTO amazonRechargeProcessedRequestDTO, PrincipalDetails principalDetails) {

        for (Long transactionId : transactionIds) {
            AmazonRechargeTransaction originalAmazonRechargeTransaction = amazonRechargeTransactionRepository.findById(transactionId)
                    .orElseThrow(() -> new RuntimeException("내역 없음"));
            User user = originalAmazonRechargeTransaction.getUser();
            Wallet wallet = walletRepository.findById(user.getWallet().getId())
                    .orElseThrow(() -> new RuntimeException("금액 정보 없음"));

            if (originalAmazonRechargeTransaction.getStatus() == AmazonTransactionEnum.WAITING) {
                AmazonRechargeTransaction updatedAmazonRechargeTransaction = AmazonRechargeTransaction.builder()
                        .id(originalAmazonRechargeTransaction.getId())
                        .rechargeAmount(originalAmazonRechargeTransaction.getRechargeAmount())
                        .chargedCount(originalAmazonRechargeTransaction.getChargedCount() + 1)
                        .ownerName(wallet.getOwnerName())
                        .user(user)
                        .lv(user.getLv())
                        .ip(originalAmazonRechargeTransaction.getIp())
                        .phone(user.getPhone())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .processedAt(LocalDateTime.now())
                        .status(AmazonTransactionEnum.APPROVAL)
                        .build();

                amazonRechargeTransactionRepository.save(updatedAmazonRechargeTransaction);

                long rechargeAmount = originalAmazonRechargeTransaction.getRechargeAmount();
                double dailyFirstRechargeBonus = amazonBonusService.applyDailyFirstRechargeBonus(user.getId(), rechargeAmount);
                double rechargeBonus = amazonBonusService.applyRechargeBonus(user.getId(), rechargeAmount);

                wallet.setAmazonMoney(wallet.getAmazonMoney() + rechargeAmount);
                wallet.setAmazonPoint((long) (wallet.getAmazonPoint() + dailyFirstRechargeBonus + rechargeBonus));
                wallet.setChargedCount(updatedAmazonRechargeTransaction.getChargedCount());
                wallet.setLastRechargedAt(updatedAmazonRechargeTransaction.getProcessedAt());
                walletRepository.save(wallet);

                tradeLogService.recordTrade(originalAmazonRechargeTransaction.getId(),
                        originalAmazonRechargeTransaction.getRechargeAmount(),
                        wallet.getAmazonMoney(),
                        TradeLogCategory.MONEY, "충전");
            } else {
                throw new RuntimeException("입금 대기중인 내역만 승인가능합니다.");
            }
        }
    }

    /**
     * 충전 거래를 취소 처리. 대기 중(WAITING) 또는 미확인(UNREAD) 상태의 충전 거래를 취소 상태(CANCELLATION)로 변경.
     *
     * @param userId 사용자 ID
     * @param transactionIds 취소할 거래 ID 목록
     * @param principalDetails 현재 인증된 사용자의 세부 정보
     */
    public void cancelTransaction(Long userId, List<Long> transactionIds, PrincipalDetails principalDetails) {
        for (Long transactionId : transactionIds) {

            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("유저 정보 없음"));
            AmazonRechargeTransaction amazonRechargeTransaction = amazonRechargeTransactionRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("내역 없음"));

            if (amazonRechargeTransaction.getStatus() == AmazonTransactionEnum.WAITING || amazonRechargeTransaction.getStatus() == AmazonTransactionEnum.UNREAD) {

                amazonRechargeTransaction = AmazonRechargeTransaction.builder()
                        .id(amazonRechargeTransaction.getId())
                        .rechargeAmount(amazonRechargeTransaction.getRechargeAmount())
                        .user(user)
                        .lv(user.getLv())
                        .phone(user.getPhone())
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .ownerName(user.getWallet().getOwnerName())
                        .status(AmazonTransactionEnum.CANCELLATION)
                        .processedAt(LocalDateTime.now())
                        .build();

                AmazonRechargeTransaction savedAmazonRechargeTransaction = amazonRechargeTransactionRepository.save(amazonRechargeTransaction);
            }
        }
    }
}
