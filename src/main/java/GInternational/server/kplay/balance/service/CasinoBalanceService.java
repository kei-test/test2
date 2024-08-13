package GInternational.server.kplay.balance.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.kplay.balance.dto.CasinoBalanceRequestDTO;
import GInternational.server.kplay.balance.dto.CasinoBalanceResponseDTO;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(value = "clientServerTransactionManager")
public class CasinoBalanceService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Value("${secret.secret-key}")
    private String secretKey;

    /**
     * 유저의 카지노 잔액을 조회. 유효한 유저와 비밀 키 검증을 수행.
     *
     * @param casinoBalanceRequestDTO 카지노 잔액 조회 요청 정보를 담은 DTO
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return CasinoBalanceResponseDTO 카지노 잔액 응답 DTO
     */
    public CasinoBalanceResponseDTO calledBalance(CasinoBalanceRequestDTO casinoBalanceRequestDTO, String secretHeader) {
        User user = userRepository.findByAasId(casinoBalanceRequestDTO.getUser_id()).orElse(null);

        if (user == null) {
            return CasinoBalanceResponseDTO.createFailureResponse("INVALID_USER");
        } else if (!secretHeader.equals(secretKey)) {
            return CasinoBalanceResponseDTO.createFailureResponse("ACCESS_DENIED");
        }

        Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow(
                () -> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "지갑 정보 없음"));

        int status = (secretHeader.equals(secretKey) &&
                user.getAasId().equals(casinoBalanceRequestDTO.getUser_id())) ? 1 : 0;

        String error = getErrorMessage(status, user, secretHeader);

        if (status == 1) {
            return new CasinoBalanceResponseDTO(status, wallet.getCasinoBalance());
        } else {
            return CasinoBalanceResponseDTO.createFailureResponse(error);
        }
    }

    /**
     * 잔액 조회 실패 시 오류 메시지를 결정.
     *
     * @param status 상태 코드 (성공: 1, 실패: 0)
     * @param user 조회 요청한 유저
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return String 오류 메시지
     */
    public String getErrorMessage(int status, User user, String secretHeader) {
        if (status == 0) {
            if (user.getAasId() == null) {
                return "INVALID_USER";
            } else if (!secretHeader.equals(secretKey)) {
                return "ACCESS_DENIED";
            } else {
                return "UNKNOWN_ERROR";
            }
        }
        return null;
    }
}
