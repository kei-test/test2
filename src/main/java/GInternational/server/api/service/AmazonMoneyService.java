package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonMoneyRequestDTO;
import GInternational.server.api.entity.AmazonMoney;
import GInternational.server.api.repository.AmazonMoneyRepository;
import GInternational.server.api.vo.TradeLogCategory;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonMoneyService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final AmazonMoneyRepository amazonMoneyRepository;
    private final TradeLogService tradeLogService;

    /**
     * 사용자에게 아마존 머니를 지급.
     *
     * @param userId 사용자 ID
     * @param requestDTO 아마존 머니 지급에 관한 요청 데이터
     * @param principalDetails 인증된 사용자의 정보
     * @throws RestControllerException 사용자를 찾을 수 없거나 다른 내부 오류 발생 시
     */
    public void addAmazonMoneysToUser(Long userId, AmazonMoneyRequestDTO requestDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));
        Wallet wallet = walletRepository.findById(userId)
                        .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));

        wallet.setAmazonMoney(wallet.getAmazonMoney() + requestDTO.getAmazonMoney());
        walletRepository.save(wallet);

        AmazonMoney amazonMoney = new AmazonMoney();
        amazonMoney.setUserId(userId);
        amazonMoney.setAmazonMoney(requestDTO.getAmazonMoney());
        amazonMoney.setCreatedAt(LocalDateTime.now());
        amazonMoney.setDescription(requestDTO.getDescription());
        amazonMoneyRepository.save(amazonMoney);

        // TradeLog 기록
        tradeLogService.recordTrade(userId, requestDTO.getAmazonMoney(), wallet.getAmazonMoney() + requestDTO.getAmazonMoney(), TradeLogCategory.MONEY, requestDTO.getDescription());
    }

    /**
     * 사용자의 아마존 머니를 차감.
     *
     * @param userId 사용자 ID
     * @param requestDTO 아마존 머니 차감에 관한 요청 데이터
     * @param principalDetails 인증된 사용자의 정보
     * @throws RestControllerException 사용자의 머니가 차감 요청 금액보다 적을 때 또는 사용자를 찾을 수 없을 때
     */
    public void subtractAmazonMoneysFromUser(Long userId, AmazonMoneyRequestDTO requestDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));

        long currentAmazonMoneys = wallet.getAmazonMoney();
        long subtractAmount = requestDTO.getAmazonMoney();

        if (subtractAmount > currentAmazonMoneys) {
            throw new RestControllerException(ExceptionCode.INVALID_OPERATION, "차감할 머니가 사용자의 머니보다 많습니다.");
        }

        wallet.setAmazonMoney(currentAmazonMoneys - subtractAmount);
        walletRepository.save(wallet);

        AmazonMoney amazonMoney = new AmazonMoney();
        amazonMoney.setUserId(userId);
        amazonMoney.setAmazonMoney(-subtractAmount);
        amazonMoney.setCreatedAt(LocalDateTime.now());
        amazonMoney.setDescription(requestDTO.getDescription());
        amazonMoneyRepository.save(amazonMoney);

        // TradeLog 기록
        tradeLogService.recordTrade(userId, -subtractAmount, currentAmazonMoneys - subtractAmount, TradeLogCategory.MONEY, requestDTO.getDescription());
    }

    /**
     * 사용자의 모든 아마존 머니를 회수.
     *
     * @param userId 사용자 ID
     * @param principalDetails 인증된 사용자의 정보
     * @throws RestControllerException 사용자를 찾을 수 없을 때
     */
    public void reclaimAllAmazonMoneys(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "파트너를 찾을 수 없습니다."));
        Wallet wallet = walletRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));

        long currentAmazonMoneys = wallet.getAmazonMoney();
        if (currentAmazonMoneys > 0) {
            wallet.setAmazonMoney(0);
            walletRepository.save(wallet);

            AmazonMoney amazonMoney = new AmazonMoney();
            amazonMoney.setUserId(userId);
            amazonMoney.setAmazonMoney(-currentAmazonMoneys);
            amazonMoney.setCreatedAt(LocalDateTime.now());
            amazonMoney.setDescription("전액회수");
            amazonMoneyRepository.save(amazonMoney);
        }

        // TradeLog 기록
        tradeLogService.recordTrade(userId, -currentAmazonMoneys, 0L, TradeLogCategory.MONEY, "전액회수");
    }

    /**
     * 모든 사용자의 아마존 머니 거래 내역을 조회.
     *
     * @param principalDetails 인증된 사용자의 정보
     * @return 모든 아마존 머니 거래 내역
     */
    public List<AmazonMoney> findAllAmazonMoneys(PrincipalDetails principalDetails) {
        return amazonMoneyRepository.findAll();
    }

    /**
     * 특정 사용자의 아마존 머니 거래 내역을 조회.
     *
     * @param userId 사용자 ID
     * @param principalDetails 인증된 사용자의 정보
     * @return 해당 사용자의 아마존 머니 거래 내역
     */
    public List<AmazonMoney> findAmazonMoneysByUserId(Long userId, PrincipalDetails principalDetails) {
        return amazonMoneyRepository.findByUserId(userId);
    }

    /**
     * 특정 사용자의 주어진 날짜 범위 내에서의 아마존 머니 거래 내역을 조회.
     *
     * @param userId 사용자 ID
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param principalDetails 인증된 사용자의 정보
     * @return 해당 기간 내 사용자의 아마존 머니 거래 내역
     */
    public List<AmazonMoney> findAmazonMoneysByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        return amazonMoneyRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    /**
     * 주어진 날짜 범위 내에서의 모든 아마존 머니 거래 내역을 조회.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param principalDetails 인증된 사용자의 정보
     * @return 해당 기간 내 모든 아마존 머니 거래 내역
     */
    public List<AmazonMoney> findAllAmazonMoneysByDateRange(LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        return amazonMoneyRepository.findAllByCreatedAtBetween(startDate, endDate);
    }
}
