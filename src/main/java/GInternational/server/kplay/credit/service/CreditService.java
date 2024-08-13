package GInternational.server.kplay.credit.service;

import GInternational.server.api.service.MoneyLogService;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.kplay.credit.dto.CreditRequestDTO;
import GInternational.server.kplay.credit.dto.CreditResponseDTO;
import GInternational.server.kplay.credit.entity.Credit;
import GInternational.server.kplay.credit.repository.CreditRepository;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.kplay.debit.repository.DebitRepository;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(value = "clientServerTransactionManager")
public class CreditService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final CreditRepository creditRepository;
    private final DebitRepository debitRepository;
    private final MoneyLogService moneyLogService;

    @Value("${secret.secret-key}")
    private String secretKey;

    /**
     * 크레딧(베팅) 추가 요청을 처리하고 결과를 반환.
     *
     * @param creditRequestDTO 크레딧 추가 요청 정보를 담은 DTO
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return CreditResponseDTO 크레딧 추가 처리 결과
     */
    public CreditResponseDTO calledCredit(CreditRequestDTO creditRequestDTO, String secretHeader) {
        User user = userRepository.findByAasId(creditRequestDTO.getUser_id()).orElse(null);
        Debit existingDebit = debitRepository.findByTxnId(creditRequestDTO.getTxn_id()).orElse(null);
        Wallet wallet;

        if (user == null) {
            return CreditResponseDTO.createFailureResponse("INVALID_USER");
        } else if (!secretHeader.equals(secretKey)) {
            return CreditResponseDTO.createFailureResponse("ACCESS_DENIED");
        } else if (existingDebit == null) {
            return CreditResponseDTO.createFailureResponse("INVALID_DEBIT");
        } else {
            boolean isDuplicateCredit = creditRepository.existsByTxnId(creditRequestDTO.getTxn_id());
            wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                    (() -> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "지갑 정보 없음"));

            if (isDuplicateCredit) {
                return CreditResponseDTO.createFailureResponse("DUPLICATE_CREDIT");
            } else {
                Credit savedCredit = new Credit();
                savedCredit.setDebit(existingDebit);
                savedCredit.setUser_id(creditRequestDTO.getUser_id());
                savedCredit.setPrd_id(creditRequestDTO.getPrd_id());
                savedCredit.setGame_id(creditRequestDTO.getGame_id());
                savedCredit.setTable_id(creditRequestDTO.getTable_id());
                savedCredit.setAmount(creditRequestDTO.getAmount());
                savedCredit.setTxnId(creditRequestDTO.getTxn_id());
                savedCredit.setIs_cancel(creditRequestDTO.getIs_cancel());
                savedCredit.setRemainAmount((int) (wallet.getCasinoBalance() + creditRequestDTO.getAmount()));
                creditRepository.save(savedCredit);
            }
        }

        int status = (secretHeader.equals(secretKey) &&
                user.getAasId().equals(creditRequestDTO.getUser_id()) &&
                existingDebit.getId() != null &&
                existingDebit.getTxnId().equals(creditRequestDTO.getTxn_id())) ? 1 : 0;

        long newCasinoBalance = user.getWallet().getCasinoBalance() + creditRequestDTO.getAmount();
        user.getWallet().setCasinoBalance(newCasinoBalance);
        walletRepository.save(user.getWallet());

        String bettingCategory = getBettingCategory(creditRequestDTO.getPrd_id());
        String description = creditRequestDTO.getGame_id() + "(" + bettingCategory + ")";
        if (creditRequestDTO.getAmount() > 0) {
            moneyLogService.recordMoneyUsage(user.getId(), Long.valueOf(creditRequestDTO.getAmount()), user.getWallet().getCasinoBalance(), MoneyLogCategoryEnum.당첨, description);
        }

        String error = getErrorMessage(status, user, secretHeader);

        if (status == 1) {
            return new CreditResponseDTO(status, user.getWallet().getCasinoBalance());
        } else {
            return CreditResponseDTO.createFailureResponse(error);
        }
    }

    /**
     * 오류 상황에 따른 메시지를 반환.
     *
     * @param status 처리 상태 코드 (성공: 1, 실패: 0)
     * @param user 요청한 사용자 정보
     * @param secretHeader 요청 헤더에서 전달된 비밀 키
     * @return String 오류 메시지
     */
    public String getErrorMessage(int status, User user, String secretHeader) {
        if (status == 0) {
            if (user.getAasId() == null) {
                return "INVALID_USER";
            } else {
                return "UNKNOWN_ERROR";
            }
        }
        return null;
    }

    /**
     * prd_id에 따라 게임 카테고리를 결정합니다.
     *
     * @param prdId 제품 ID
     * @return "카지노" 또는 "슬롯"
     */
    private String getBettingCategory(int prdId) {
        if (prdId >= 1 && prdId <= 99) {
            return "카지노";
        } else if (prdId >= 200 && prdId <= 299) {
            return "슬롯";
        } else {
            return "기타";
        }
    }
}
