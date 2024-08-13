package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonExchangeRequestDTO;
import GInternational.server.api.mapper.AmazonExchangeRequestMapper;
import GInternational.server.api.mapper.AmazonExchangeResponseMapper;
import GInternational.server.api.entity.AmazonExchangeTransaction;
import GInternational.server.api.repository.AmazonExchangeRepository;
import GInternational.server.api.vo.AmazonTransactionEnum;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonExchangeService {

    private final UserRepository userRepository;
    private final AmazonExchangeRepository amazonExchangeRepository;
    private final AmazonExchangeRequestMapper amazonExchangeRequestMapper;
    private final WalletRepository walletRepository;
    private final LoginStatisticService loginStatisticService;
    private final MoneyLogService moneyLogService;

    /**
     * 사용자가 환전을 신청.
     *
     * @param userId 환전을 신청하는 사용자의 ID
     * @param amazonExchangeRequestDTO 환전 요청에 대한 세부 정보를 담고 있는 DTO
     * @param request HTTP 요청 정보, 클라이언트의 IP 주소 등을 포함
     * @param principalDetails 인증된 사용자의 세부 정보
     * @throws RuntimeException 유저 정보 또는 금액 정보를 찾을 수 없을 때 발생
     */
    public void exchangeAmazonMoney(Long userId, AmazonExchangeRequestDTO amazonExchangeRequestDTO, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("유저 정보 없음"));
        Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow(()-> new RuntimeException("금액 정보 없음"));

        String clientIp = request.getRemoteAddr();

        if (amazonExchangeRequestDTO.getExchangeAmount() <= wallet.getAmazonMoney()) {

            amazonExchangeRequestMapper.toEntity(amazonExchangeRequestDTO);

            //요청 기록 생성
            AmazonExchangeTransaction transaction = AmazonExchangeTransaction.builder()
                    .exchangeAmount(amazonExchangeRequestDTO.getExchangeAmount())
                    .user(user)
                    .phone(user.getPhone())
                    .distributor(user.getDistributor())
                    .wallet(wallet)
                    .bankName(wallet.getBankName())
                    .number(String.valueOf(wallet.getNumber()))
                    .lv(user.getLv())
                    .ip(clientIp)
                    .ownerName(wallet.getOwnerName())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .status(AmazonTransactionEnum.UNREAD)
                    .createdAt(LocalDateTime.now())
                    .build();
            amazonExchangeRepository.save(transaction);
        } else {
            throw new RuntimeException("환전 금액이 0보다 커야하고 지갑 잔액이 충분해야 합니다.");
        }
    }

    /**
     * 특정 환전 거래의 상태를 대기중(WAITING)으로 변경.
     *
     * @param transactionId 상태를 변경할 환전 거래의 ID
     * @param principalDetails 인증된 사용자의 세부 정보
     * @throws RuntimeException 지정된 ID의 거래 내역을 찾을 수 없거나, 거래가 처리 가능한 상태가 아닐 때 발생
     */
    public void updateTransactionStatusToWaiting(Long transactionId, PrincipalDetails principalDetails) {
        AmazonExchangeTransaction transaction = amazonExchangeRepository.findById(transactionId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "내역 없음"));

        if (transaction.getStatus() != AmazonTransactionEnum.UNREAD) {
            throw new RuntimeException("처리 가능한 상태의 내역만 변경 가능합니다.");
        }

        transaction.setStatus(AmazonTransactionEnum.WAITING);
        amazonExchangeRepository.save(transaction);
    }

    /**
     * 관리자가 환전 신청을 승인. 환전 금액을 사용자의 지갑에서 차감하고, 거래 상태를 승인(APPROVAL)으로 업데이트.
     *
     * @param transactionIds 승인할 환전 거래의 ID 목록
     * @param principalDetails 인증된 사용자의 세부 정보
     * @throws RuntimeException 환전 대기중인 내역만 승인 가능할 때, 해당 조건에 맞지 않는 경우 발생
     */
    public void updateAmazonMoney(List<Long> transactionIds, PrincipalDetails principalDetails) {
        for (Long transactionId : transactionIds) {

            AmazonExchangeTransaction originalTransaction = amazonExchangeRepository.findById(transactionId).orElseThrow(() -> new RuntimeException("내역 없음"));
            User user = originalTransaction.getUser();
            Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                    (() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "금액 정보 없음"));

            if (originalTransaction.getStatus() == AmazonTransactionEnum.WAITING) {

                AmazonExchangeTransaction updatedTransaction = AmazonExchangeTransaction.builder()
                        .id(originalTransaction.getId())
                        .exchangeAmount(originalTransaction.getExchangeAmount())
                        .ownerName(wallet.getOwnerName())
                        .user(originalTransaction.getUser())
                        .lv(user.getLv())
                        .ip(originalTransaction.getIp())
                        .phone(user.getPhone())
                        .bankName(wallet.getBankName())
                        .number(String.valueOf(wallet.getNumber()))
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .processedAt(LocalDateTime.now())
                        .status(AmazonTransactionEnum.APPROVAL)
                        .exchangedCount((int) (user.getWallet().getExchangedCount() + 1))
                        .build();
                AmazonExchangeTransaction savedTransaction = amazonExchangeRepository.save(updatedTransaction);

                wallet.setAmazonMoney(wallet.getAmazonMoney() - savedTransaction.getExchangeAmount());
                wallet.setExchangedCount(savedTransaction.getExchangedCount());
                wallet.setWithdrawTotal(wallet.getWithdrawTotal() + savedTransaction.getExchangeAmount());  //출금액 누적 합계
                wallet.setTotalSettlement(wallet.getWithdrawTotal() - wallet.getDepositTotal());
                wallet.setAmazonMoney(wallet.getAmazonMoney() - savedTransaction.getExchangeAmount());
                wallet.setExchangeProcessedAt(updatedTransaction.getProcessedAt());
                walletRepository.save(wallet);

                if ("ROLE_USER".equals(savedTransaction.getUser().getRole())) {
                    loginStatisticService.recordExchange();
                }
                moneyLogService.recordMoneyUsage(user.getId(), originalTransaction.getExchangeAmount(), wallet.getSportsBalance(), MoneyLogCategoryEnum.환전, "");
            } else {
                throw new RuntimeException("환전 대기중인 내역만 승인가능합니다.");
            }
        }
    }

    /**
     * 관리자가 환전 신청을 취소. 환전 거래의 상태를 취소(CANCELLATION)로 업데이트.
     *
     * @param userId 환전을 신청한 사용자의 ID
     * @param transactionIds 취소할 환전 거래의 ID 목록
     * @param principalDetails 인증된 사용자의 세부 정보
     * @throws RuntimeException 지정된 환전 거래를 찾을 수 없거나, 이미 처리된 거래를 취소하려 할 때 발생
     */
    public void cancelTransaction(Long userId, List<Long> transactionIds, PrincipalDetails principalDetails) {
        for (Long transactionId : transactionIds) {

            User user = userRepository.findById(userId).orElseThrow
                    (() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
            AmazonExchangeTransaction transaction = amazonExchangeRepository.findById(transactionId).orElseThrow
                    (() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "내역 없음"));

            if (transaction.getStatus() == AmazonTransactionEnum.WAITING) {

                transaction = AmazonExchangeTransaction.builder()
                        .id(transaction.getId())
                        .exchangeAmount(transaction.getExchangeAmount())
                        .user(user)
                        .lv(user.getLv())
                        .phone(user.getPhone())
                        .user(user)
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .ownerName(user.getWallet().getOwnerName())
                        .status(AmazonTransactionEnum.CANCELLATION)
                        .processedAt(LocalDateTime.now())
                        .build();

                AmazonExchangeTransaction savedTransaction = amazonExchangeRepository.save(transaction);
            }
        }
    }
}
