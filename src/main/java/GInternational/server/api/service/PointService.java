package GInternational.server.api.service;

import GInternational.server.api.dto.AdjustPointRequestDTO;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.api.vo.PointTransactionDescriptionEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.PointRequestDTO;
import GInternational.server.api.dto.PointResponseDTO;
import GInternational.server.api.entity.PointTransaction;
import GInternational.server.api.repository.PointRepository;
import GInternational.server.api.vo.PointTransactionTypeEnum;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class PointService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PointRepository pointRepository;
    private final PointLogService pointLogService;
    private final MoneyLogService moneyLogService;

    /**
     * 포인트를 스포츠 머니로 교환하는 처리 로직 수행.
     *
     * @param userId 포인트를 교환할 사용자 ID
     * @param walletId 교환할 지갑 ID
     * @param pointRequestDTO 포인트 교환 요청 데이터
     * @param request 클라이언트의 요청 정보
     * @param principalDetails 현재 인증된 사용자의 인증 정보
     * @return 교환 처리 후 생성된 포인트 거래 정보
     * @throws RestControllerException 유저 정보 또는 지갑 정보가 없거나 교환할 포인트가 부족할 경우 예외 발생
     */
    public PointResponseDTO exchangedPoint(Long userId, Long walletId, PointRequestDTO pointRequestDTO, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow
                (()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
        Wallet wallet = walletRepository.findById(walletId).orElseThrow
                (()-> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "금액 정보 없음"));

        String clientIp = request.getRemoteAddr();

        if (pointRequestDTO.getExchangePoint() <= user.getWallet().getPoint()) {

            PointTransaction pointTransaction = PointTransaction.builder()
                    .processedAt(LocalDateTime.now())
                    .description(PointTransactionDescriptionEnum.포인트전환)
                    .remainingPoint(wallet.getPoint() - pointRequestDTO.getExchangePoint())
                    .usedPoint(pointRequestDTO.getExchangePoint())
                    .ip(clientIp)
                    .type(PointTransactionTypeEnum.적립)
                    .user(user)
                    .build();
            pointRepository.save(pointTransaction);

            //금액을 다루는 테이블 더티체킹 후 저장
            wallet.setSportsBalance(wallet.getSportsBalance() + pointRequestDTO.getExchangePoint());
            wallet.setPoint(wallet.getPoint() - pointRequestDTO.getExchangePoint());

            pointLogService.recordPointLog(user.getId(), (long) pointRequestDTO.getExchangePoint(), PointLogCategoryEnum.포인트전환, clientIp, "");
            moneyLogService.recordMoneyUsage(user.getId(), (long) pointRequestDTO.getExchangePoint(), wallet.getSportsBalance(), MoneyLogCategoryEnum.포인트전환, "");

            walletRepository.save(wallet);

            PointResponseDTO pointResponseDTO = new PointResponseDTO();
            pointResponseDTO.setSportsBalance(wallet.getSportsBalance());
            return pointResponseDTO;

        }
        else throw new RestControllerException(ExceptionCode.INSUFFICIENT_POINTS, "교환할 포인트가 부족합니다.");
    }
}

