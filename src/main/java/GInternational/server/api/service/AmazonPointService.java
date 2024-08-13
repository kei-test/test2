package GInternational.server.api.service;

import GInternational.server.api.dto.AmazonPointRequestDTO;
import GInternational.server.api.entity.AmazonPoint;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.AmazonPointRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WalletRepository;
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
public class AmazonPointService {

    private final UserRepository userRepository;
    private final AmazonPointRepository amazonPointRepository;
    private final TradeLogService tradeLogService;
    private final WalletRepository walletRepository;

    /**
     * 포인트 지급.
     *
     * @param userId 사용자 ID
     * @param walletId 지갑 ID
     * @param requestDTO 포인트 지급 정보
     * @param principalDetails 인증된 사용자 정보
     */
    public void addAmazonPointsToUser(Long userId, Long walletId, AmazonPointRequestDTO requestDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "파트너를 찾을 수 없습니다."));
        Wallet wallet = walletRepository.findById(walletId).
                orElseThrow(()-> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));

        wallet.setAmazonPoint(wallet.getAmazonPoint() + requestDTO.getAmazonPoint());
        walletRepository.save(wallet);

        AmazonPoint amazonPoint = new AmazonPoint();
        amazonPoint.setUserId(userId);
        amazonPoint.setAmazonPoint(requestDTO.getAmazonPoint());
        amazonPoint.setCreatedAt(LocalDateTime.now());
        amazonPoint.setDescription(requestDTO.getDescription());
        amazonPointRepository.save(amazonPoint);

        // TradeLog 기록
        tradeLogService.recordTrade(userId, requestDTO.getAmazonPoint(), wallet.getAmazonPoint() + requestDTO.getAmazonPoint(), TradeLogCategory.POINT, requestDTO.getDescription());
    }

    /**
     * 포인트 차감.
     *
     * @param userId 사용자 ID
     * @param walletId 지갑 ID
     * @param requestDTO 포인트 차감 정보
     * @param principalDetails 인증된 사용자 정보
     */
    public void subtractAmazonPointsFromUser(Long userId, Long walletId, AmazonPointRequestDTO requestDTO, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        Wallet wallet = walletRepository.findById(walletId).
                orElseThrow(()-> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));

        long currentAmazonPoints = wallet.getAmazonPoint();
        long subtractAmount = requestDTO.getAmazonPoint();
        String clientIp = request.getRemoteAddr();

        if (subtractAmount > currentAmazonPoints) {
            throw new RestControllerException(ExceptionCode.INVALID_OPERATION, "차감할 포인트가 사용자의 포인트보다 많습니다.");
        }

        wallet.setAmazonPoint(currentAmazonPoints - subtractAmount);
        walletRepository.save(wallet);

        AmazonPoint amazonPoint = new AmazonPoint();
        amazonPoint.setUserId(userId);
        amazonPoint.setAmazonPoint(-subtractAmount);
        amazonPoint.setCreatedAt(LocalDateTime.now());
        amazonPoint.setDescription(requestDTO.getDescription());
        amazonPointRepository.save(amazonPoint);

        // TradeLog 기록
        tradeLogService.recordTrade(userId, -subtractAmount, currentAmazonPoints - subtractAmount, TradeLogCategory.POINT, requestDTO.getDescription());
    }

    /**
     * 전액 회수.
     *
     * @param userId 사용자 ID
     * @param walletId 지갑 ID
     * @param principalDetails 인증된 사용자 정보
     */
    public void reclaimAllAmazonPoints(Long userId, Long walletId, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "파트너를 찾을 수 없습니다."));
        Wallet wallet = walletRepository.findById(walletId).
                orElseThrow(()-> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));

        String clientIp = request.getRemoteAddr();

        long currentAmazonPoints = wallet.getAmazonPoint();
        if (currentAmazonPoints > 0) {
            wallet.setAmazonPoint(0);
            walletRepository.save(wallet);

            AmazonPoint amazonPoint = new AmazonPoint();
            amazonPoint.setUserId(userId);
            amazonPoint.setAmazonPoint(-currentAmazonPoints);
            amazonPoint.setCreatedAt(LocalDateTime.now());
            amazonPoint.setDescription("전액회수");
            amazonPointRepository.save(amazonPoint);
        }

        // TradeLog 기록
        tradeLogService.recordTrade(userId, -currentAmazonPoints, 0L, TradeLogCategory.POINT, "전액회수");
    }

    /**
     * 전체 조회.
     *
     * @param principalDetails 인증된 사용자 정보
     * @return 모든 아마존 포인트 거래 내역
     */
    public List<AmazonPoint> findAllAmazonPoints(PrincipalDetails principalDetails) {
        return amazonPointRepository.findAll();
    }

    /**
     * 사용자별 조회.
     *
     * @param userId 사용자 ID
     * @param principalDetails 인증된 사용자 정보
     * @return 해당 사용자의 아마존 포인트 거래 내역
     */
    public List<AmazonPoint> findAmazonPointsByUserId(Long userId, PrincipalDetails principalDetails) {
        return amazonPointRepository.findByUserId(userId);
    }

    /**
     * 사용자별 날짜 범위 조회.
     *
     * @param userId 사용자 ID
     * @param startDate 시작 날짜와 시간
     * @param endDate 종료 날짜와 시간
     * @param principalDetails 인증된 사용자 정보
     * @return 지정된 날짜 범위 내 해당 사용자의 아마존 포인트 거래 내역
     */
    public List<AmazonPoint> findAmazonPointsByUserIdAndDateRange(Long userId, LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        return amazonPointRepository.findByUserIdAndCreatedAtBetween(userId, startDate, endDate);
    }

    /**
     * 날짜 범위 조회.
     *
     * @param startDate 시작 날짜와 시간
     * @param endDate 종료 날짜와 시간
     * @param principalDetails 인증된 사용자 정보
     * @return 지정된 날짜 범위 내 모든 아마존 포인트 거래 내역
     */
    public List<AmazonPoint> findAllAmazonPointsByDateRange(LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        return amazonPointRepository.findAllByCreatedAtBetween(startDate, endDate);
    }
}
