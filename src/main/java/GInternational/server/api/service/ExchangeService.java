package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.ExchangeRequestDTO;
import GInternational.server.api.mapper.ExchangeRequestMapper;
import GInternational.server.api.mapper.ExchangeResponseMapper;
import GInternational.server.api.entity.ExchangeTransaction;
import GInternational.server.api.repository.ExchangeRepository;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.api.vo.TransactionGubunEnum;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
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
public class ExchangeService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final ExchangeRepository exchangeRepository;
    private final MoneyLogService moneyLogService;
    private final ExchangeRequestMapper exchangeRequestMapper;
    private final ExchangeResponseMapper exchangeResponseMapper;
    private final LoginStatisticService loginStatisticService;

    /**
     * 사용자가 스포츠 머니를 환전 요청.
     *
     * @param userId 환전을 요청하는 사용자 ID
     * @param request 클라이언트의 요청 정보
     * @param exchangeRequestDTO 환전 요청에 필요한 데이터를 담은 DTO
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @throws RestControllerException 유저 정보 또는 금액 정보가 없거나, 환전 비밀번호가 일치하지 않는 경우 예외 발생
     */
    public void exchangeSportsBalance(Long userId, HttpServletRequest request, ExchangeRequestDTO exchangeRequestDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow
                (()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
        Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                (()-> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "금액 정보 없음"));

        String clientIp = request.getRemoteAddr();

        String walletBankPassword = wallet.getBankPassword();
        String requestBankPassword = exchangeRequestDTO.getBankPassword();
        if (walletBankPassword == null || !walletBankPassword.equals(requestBankPassword)) {
            throw new RestControllerException(ExceptionCode.PASSWORD_NOT_MATCH, "환전 비밀번호가 일치하지 않습니다.");
        }

        if (exchangeRequestDTO.getExchangeAmount() <= wallet.getSportsBalance()) {

            exchangeRequestMapper.toEntity(exchangeRequestDTO);

            //요청 기록 생성
            ExchangeTransaction transaction = ExchangeTransaction.builder()
                    .exchangeAmount(exchangeRequestDTO.getExchangeAmount())
                    .remainingSportsBalance(wallet.getSportsBalance() - exchangeRequestDTO.getExchangeAmount())
                    .bonus(0)
                    .remainingPoint((int) wallet.getPoint())
                    .user(user)
                    .ip(clientIp)
                    .phone(user.getPhone())
                    .site("test")
                    .wallet(wallet)
                    .bankName(wallet.getBankName())
                    .number(String.valueOf(wallet.getNumber()))
                    .lv(user.getLv())
                    .exchangedCount((int) wallet.getChargedCount())
                    .ownerName(wallet.getOwnerName())
                    .username(user.getUsername())
                    .nickname(user.getNickname())
                    .gubun(TransactionGubunEnum.EXCHANGE)
                    .status(TransactionEnum.UNREAD)
                    .createdAt(LocalDateTime.now())
                    .build();
            exchangeRepository.save(transaction);

            wallet.setSportsBalance(transaction.getRemainingSportsBalance());
            walletRepository.save(wallet);

            exchangeResponseMapper.toDto(wallet);
            moneyLogService.recordMoneyUsage(user.getId(), transaction.getExchangeAmount(), wallet.getSportsBalance(), MoneyLogCategoryEnum.환전, "");
        } else {
            throw new RestControllerException(ExceptionCode.INSUFFICIENT_FUNDS_OR_INVALID_AMOUNT, "환전 금액이 0보다 커야하고 지갑 잔액이 충분해야 합니다.");
        }
    }

    /**
     * 관리자가 환전 요청건의 상태를 대기중으로 변경.
     *
     * @param request 클라이언트의 요청 정보
     * @param transactionId 상태를 대기중으로 변경할 환전 요청의 ID
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @throws RuntimeException 처리 가능한 상태의 내역만 변경 가능한 경우 예외 발생
     */
    @AuditLogService.Audit("환전 상태값 변경")
    public void updateExchangeTransactionStatusToWaiting(HttpServletRequest request, Long transactionId, PrincipalDetails principalDetails) {
        ExchangeTransaction transaction = exchangeRepository.findById(transactionId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.APPLICATION_NOT_FOUND, "내역 없음"));

        if (transaction.getStatus() != TransactionEnum.UNREAD) {
            throw new RestControllerException(ExceptionCode.ONLY_WAITING_TRANSACTIONS_CAN_BE_APPROVED, "대기 중인 상태의 신청 건만 승인 가능합니다.");
        }

        transaction.setStatus(TransactionEnum.WAITING);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(transaction.getUser().getId()));
        context.setUsername(transaction.getUser().getUsername());
        context.setDetails(transaction.getUsername() + "의 " + transaction.getExchangeAmount() + "원 환전요청건 상태값 " + transaction.getStatus() + "으로 변경");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        exchangeRepository.save(transaction);
    }

    /**
     * 관리자가 환전 요청을 승인.
     *
     * @param request 클라이언트의 요청 정보
     * @param transactionIds 승인할 환전 요청의 ID 목록
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @throws RestControllerException 내역 없음, 금액 정보 없음, 또는 환전 대기중인 내역만 승인 가능한 경우 예외 발생
     */
    @AuditLogService.Audit("환전 승인")
    public void updateExchangeSportsBalance(HttpServletRequest request, List<Long> transactionIds, PrincipalDetails principalDetails) {
        for (Long transactionId : transactionIds) {

            ExchangeTransaction originalTransaction = exchangeRepository.findById(transactionId).orElseThrow
                    (() -> new RestControllerException(ExceptionCode.APPLICATION_NOT_FOUND, "내역 없음"));
            User user = originalTransaction.getUser();
            Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                    (()-> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "금액 정보 없음"));

            if (originalTransaction.getStatus() == TransactionEnum.WAITING) {

                AuditContext context = AuditContextHolder.getContext();
                String clientIp = request.getRemoteAddr();
                context.setIp(clientIp);
                context.setTargetId(String.valueOf(user.getId()));
                context.setUsername(user.getUsername());
                context.setDetails(user.getUsername() + "의 " + originalTransaction.getExchangeAmount() + "원 환전요청 승인");
                context.setAdminUsername(principalDetails.getUsername());
                context.setTimestamp(LocalDateTime.now());

                ExchangeTransaction updatedTransaction = ExchangeTransaction.builder()
                        .id(originalTransaction.getId())
                        .exchangeAmount(originalTransaction.getExchangeAmount())
                        .remainingSportsBalance(originalTransaction.getRemainingSportsBalance())
                        .bonus(0)
                        .remainingPoint((int) wallet.getPoint())
                        .ownerName(wallet.getOwnerName())
                        .user(originalTransaction.getUser())
                        .lv(user.getLv())
                        .ip(originalTransaction.getIp())
                        .phone(user.getPhone())
                        .wallet(wallet)
                        .bankName(wallet.getBankName())
                        .number(String.valueOf(wallet.getNumber()))
                        .site("test")
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .processedAt(LocalDateTime.now())
                        .gubun(TransactionGubunEnum.SPORTS)
                        .status(TransactionEnum.APPROVAL)
                        .exchangedCount((int) (wallet.getExchangedCount() + 1))
                        .build();
                ExchangeTransaction savedTransaction = exchangeRepository.save(updatedTransaction);

                wallet.setExchangedCount(savedTransaction.getExchangedCount());
                wallet.setExchangeProcessedAt(updatedTransaction.getProcessedAt());
                wallet.setWithdrawTotal(wallet.getWithdrawTotal() + savedTransaction.getExchangeAmount());  //출금액 누적 합계
                wallet.setTotalSettlement(wallet.getDepositTotal() - wallet.getWithdrawTotal());
                walletRepository.save(wallet);
                userRepository.save(user);

                if ("ROLE_USER".equals(savedTransaction.getUser().getRole())) {
                    loginStatisticService.recordExchange();
                }
            } else {
                throw new RestControllerException(ExceptionCode.ONLY_WAITING_TRANSACTIONS_CAN_BE_APPROVED, "환전 대기중인 내역만 승인가능합니다.");

            }
        }
    }

    /**
     * 관리자가 환전 요청을 취소 처리.
     *
     * @param request 클라이언트의 요청 정보
     * @param userId 환전 요청을 취소할 사용자 ID
     * @param transactionIds 취소할 환전 요청의 ID 목록
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @throws RestControllerException 유저 정보 없음, 금액 정보 없음, 또는 내역 없음의 경우 예외 발생
     */
    @AuditLogService.Audit("환전 취소")
    public void cancelExchangeTransaction(HttpServletRequest request, Long userId, List<Long> transactionIds, PrincipalDetails principalDetails) {
        for (Long transactionId : transactionIds) {
            User user = userRepository.findById(userId).orElseThrow
                    (()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
            Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                    (()-> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "금액 정보 없음"));
            ExchangeTransaction transaction = exchangeRepository.findById(transactionId).orElseThrow
                    (() -> new RestControllerException(ExceptionCode.APPLICATION_NOT_FOUND, "내역 없음"));

            if (transaction.getStatus() == TransactionEnum.WAITING) {

                AuditContext context = AuditContextHolder.getContext();
                String clientIp = request.getRemoteAddr();
                context.setIp(clientIp);
                context.setTargetId(String.valueOf(user.getId()));
                context.setUsername(user.getUsername());
                context.setDetails(user.getUsername() + "의 " + transaction.getExchangeAmount() + "원 충전요청 취소");
                context.setAdminUsername(principalDetails.getUsername());
                context.setTimestamp(LocalDateTime.now());

                transaction = ExchangeTransaction.builder()
                        .id(transaction.getId())
                        .exchangeAmount(transaction.getExchangeAmount())
                        .user(user)
                        .lv(user.getLv())
                        .ip(user.getIp())
                        .phone(user.getPhone())
                        .wallet(wallet)
                        .site("test")
                        .username(user.getUsername())
                        .nickname(user.getNickname())
                        .ownerName(wallet.getOwnerName())
                        .gubun(TransactionGubunEnum.EXCHANGE)
                        .status(TransactionEnum.CANCELLATION)
                        .processedAt(LocalDateTime.now())
                        .build();

                ExchangeTransaction savedTransaction = exchangeRepository.save(transaction);

                wallet.setSportsBalance(wallet.getSportsBalance() + savedTransaction.getExchangeAmount());
                walletRepository.save(wallet);

                moneyLogService.recordMoneyUsage(user.getId(), savedTransaction.getExchangeAmount(), wallet.getSportsBalance(), MoneyLogCategoryEnum.환전취소, "");
            }
        }
    }
}
