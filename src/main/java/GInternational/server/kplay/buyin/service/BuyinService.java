package GInternational.server.kplay.buyin.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.kplay.buyin.dto.BuyinRequestDTO;
import GInternational.server.kplay.buyin.dto.BuyinResponseDTO;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.kplay.debit.repository.DebitRepository;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class BuyinService {

    private final DebitRepository debitRepository;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    @Value("${secret.secret-key}")
    private String secretKey;

    /**
     * 구매 요청 정보를 처리하여 구매 결과를 반환.
     *
     * @param buyinRequestDTO 구매 요청 정보를 담은 DTO
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return BuyinResponseDTO 구매 결과를 담은 DTO
     */
    public BuyinResponseDTO getInfo(BuyinRequestDTO buyinRequestDTO,String secretHeader) {
        User user = userRepository.findByAasId(buyinRequestDTO.getUser_id()).orElse(null);
        Debit existingDebit = debitRepository.findByTxnId(buyinRequestDTO.getTxn_id()).orElse(null);
        Wallet wallet;

        if (user == null) {
            return BuyinResponseDTO.createFailureResponse("INVALID_USER");
        } else if (user.getWallet().getCasinoBalance() < buyinRequestDTO.getAmount()) {
            return BuyinResponseDTO.createFailureResponse("INSUFFICIENT_FUNDS");
        } else if (existingDebit != null && existingDebit.getTxnId().equals(buyinRequestDTO.getTxn_id())) {
            return BuyinResponseDTO.createFailureResponse("DUPLICATE_DEBIT");
        } else {
            wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                    (() -> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "지갑 정보 없음"));
        }

        Debit savedDebit = Debit.builder()
                .user_id(buyinRequestDTO.getUser_id())
                .prd_id(buyinRequestDTO.getPrd_id())
                .game_id(buyinRequestDTO.getGame_id())
                .amount(buyinRequestDTO.getAmount())
                .txnId(buyinRequestDTO.getTxn_id())
                .credit_amount(buyinRequestDTO.getCredit_amount())
                .build();
        debitRepository.save(savedDebit);

        int status = (secretHeader.equals(secretKey) &&
                user.getAasId().equals(buyinRequestDTO.getUser_id()) &&
                user.getWallet().getCasinoBalance() >= buyinRequestDTO.getAmount() &&
                existingDebit == null) ? 1 : 0;

        long newCasinoBalance = user.getWallet().getCasinoBalance() - buyinRequestDTO.getAmount();
        user.getWallet().setCasinoBalance(newCasinoBalance);
        walletRepository.save(user.getWallet());

        String error = getErrorMessage(status, user, secretHeader);

        if (status == 1) {
            return new BuyinResponseDTO(status, wallet.getCasinoBalance());
        } else {
            return BuyinResponseDTO.createFailureResponse(error);
        }
    }

    /**
     * 오류 상황에 따른 메시지를 반환.
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




