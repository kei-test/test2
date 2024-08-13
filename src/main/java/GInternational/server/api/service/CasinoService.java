package GInternational.server.api.service;

import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.CasinoRequestDTO;
import GInternational.server.api.dto.CasinoResponseDTO;
import GInternational.server.api.entity.CasinoTransaction;
import GInternational.server.api.repository.CasinoRepository;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CasinoService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final CasinoRepository casinoRepository;
    private final MoneyLogService moneyLogService;

    /**
     * 사용자의 스포츠 머니를 카지노 머니로 전환.
     *
     * @param userId 사용자 ID
     * @param casinoRequestDTO 전환 요청에 필요한 정보를 담은 DTO
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 전환 후의 카지노 및 스포츠 머니 잔액 정보를 담은 DTO
     * @throws RestControllerException 유저 정보나 금액 정보가 없거나 스포츠머니가 부족할 경우 예외 발생
     */
    public CasinoResponseDTO exchangedSports(Long userId, CasinoRequestDTO casinoRequestDTO, PrincipalDetails principalDetails,
                                             HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow
                (()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 없음"));
        Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                (()-> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "금액 정보 없음"));

        String clientIp = request.getRemoteAddr();

        if (casinoRequestDTO.getExchangeSportsBalance() <= user.getWallet().getSportsBalance()) {
            CasinoTransaction casinoTransaction = CasinoTransaction.builder()
                    .processedAt(LocalDateTime.now())
                    .description("스포츠머니 -> 카지노머니로 전환")
                    .usedSportsBalance(casinoRequestDTO.getExchangeSportsBalance())
                    .remainingCasinoBalance(wallet.getCasinoBalance() + casinoRequestDTO.getExchangeSportsBalance())
                    .remainingSportsBalance(wallet.getSportsBalance() - casinoRequestDTO.getExchangeSportsBalance())
                    .exchangedCount(user.getWallet().getExchangedCount() + 1)
                    .ip(clientIp)
                    .user(user)
                    .status(TransactionEnum.APPROVAL)
                    .build();
            casinoRepository.save(casinoTransaction);

            wallet.setCasinoBalance(casinoTransaction.getRemainingCasinoBalance());
            wallet.setSportsBalance(casinoTransaction.getRemainingSportsBalance());
            wallet.setExchangedCount((int) casinoTransaction.getExchangedCount());
            walletRepository.save(wallet);

            moneyLogService.recordMoneyUsage(user.getId(), casinoRequestDTO.getExchangeSportsBalance(), casinoTransaction.getRemainingSportsBalance(), MoneyLogCategoryEnum.카지노머니로전환, "스->카");

            CasinoResponseDTO casinoResponseDTO = new CasinoResponseDTO();
            casinoResponseDTO.setCasinoBalance(wallet.getCasinoBalance());
            casinoResponseDTO.setSportsBalance(wallet.getSportsBalance());
            return casinoResponseDTO;

        } throw new RestControllerException(ExceptionCode.INSUFFICIENT_SPORTS_MONEY, "스포츠머니가 부족합니다.");
    }


    /**
     * 사용자의 카지노 머니를 스포츠 머니로 전환 신청.
     *
     * @param userId 사용자 ID
     * @param casinoRequestDTO 전환 요청에 필요한 정보를 담은 DTO
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 전환 후의 스포츠 및 카지노 머니 잔액 정보를 담은 DTO
     * @throws RestControllerException 유저 정보나 금액 정보가 없거나 카지노머니가 부족할 경우 예외 발생
     */
    public CasinoResponseDTO exchangedCasino(Long userId, CasinoRequestDTO casinoRequestDTO, PrincipalDetails principalDetails,
                                             HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow
                (()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 없음"));
        Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                (()-> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "금액 정보 없음"));

        String clientIp = request.getRemoteAddr();

        if (casinoRequestDTO.getExchangeCasinoBalance() <= user.getWallet().getCasinoBalance()) {

            CasinoTransaction casinoTransaction = CasinoTransaction.builder()
                    .processedAt(LocalDateTime.now())
                    .description("카지노머니 -> 스포츠머니로 전환")
                    .usedCasinoBalance(casinoRequestDTO.getExchangeCasinoBalance())
                    .remainingSportsBalance(wallet.getSportsBalance() + casinoRequestDTO.getExchangeCasinoBalance())
                    .remainingCasinoBalance(wallet.getCasinoBalance() - casinoRequestDTO.getExchangeCasinoBalance())
                    .exchangedCount(user.getWallet().getExchangedCount() + 1)
                    .ip(clientIp)
                    .user(user)
                    .status(TransactionEnum.APPROVAL)
                    .build();
            casinoRepository.save(casinoTransaction);

            //금액을 다루는 테이블 더티체킹 후 저장
            wallet.setSportsBalance(casinoTransaction.getRemainingSportsBalance());
            wallet.setCasinoBalance(casinoTransaction.getRemainingCasinoBalance());
            wallet.setExchangedCount((int) casinoTransaction.getExchangedCount());
            walletRepository.save(wallet);

            moneyLogService.recordMoneyUsage(user.getId(), casinoRequestDTO.getExchangeCasinoBalance(), casinoTransaction.getRemainingSportsBalance(), MoneyLogCategoryEnum.스포츠머니로전환, "카->스");

            CasinoResponseDTO pointResponseDTO = new CasinoResponseDTO();
            pointResponseDTO.setSportsBalance(wallet.getSportsBalance());
            pointResponseDTO.setCasinoBalance(wallet.getCasinoBalance());
            return pointResponseDTO;

        } else throw new RestControllerException(ExceptionCode.INSUFFICIENT_CASINO_MONEY, "카지노머니가 부족합니다.");
    }
}

