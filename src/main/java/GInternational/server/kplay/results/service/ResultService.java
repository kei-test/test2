package GInternational.server.kplay.results.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.kplay.credit.entity.Credit;
import GInternational.server.kplay.credit.repository.CreditRepository;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.kplay.debit.repository.DebitRepository;
import GInternational.server.kplay.results.dto.SavedCreditDTO;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class ResultService {

    private final CreditRepository creditRepository;
    private final DebitRepository debitRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    /**
     * 베팅 결과가 처리되지 않은 베팅 항목들을 조회.
     *
     * @return List<Debit> 처리되지 않은 베팅 데이터 목록
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public List<Debit> getPrdAndTxnDTO() {
        return debitRepository.findDataWithNOMatchingTxnId();
    }

    /**
     * 저장된 Credit 정보를 기반으로 베팅 결과를 처리합니다. 성공적으로 처리된 경우,
     * 해당 사용자의 지갑 잔액을 업데이트하고, 새로운 Credit 데이터를 저장.
     *
     * @param savedCreditDTO 처리할 Credit 정보를 담은 DTO
     * @return SavedCreditDTO 처리 후의 Credit 정보 (현재 로직에서는 null을 반환하고 있습니다.)
     * @throws RestControllerException 거래 내역이 없거나 유효하지 않은 요청인 경우 예외를 발생시킵니다.
     */
    public SavedCreditDTO debitResults(SavedCreditDTO savedCreditDTO) {
        Debit debit = debitRepository.findByTxnId(savedCreditDTO.getTxnId()).orElseThrow(() -> new RestControllerException(ExceptionCode.DEBIT_NOT_FOUND, "거래 내역 없음"));
        User user = userRepository.findByAasId(debit.getUser_id()).orElseThrow(()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 없음"));
        Wallet wallet = user.getWallet();

        if (savedCreditDTO.getType() == 1 && savedCreditDTO.getIs_cancel() == 0) {
            Credit savedCredit = new Credit();
            savedCredit.setDebit(debit);
            savedCredit.setUser_id(debit.getUser_id());
            savedCredit.setPrd_id(debit.getPrd_id());
            savedCredit.setGame_id(savedCreditDTO.getGameId());
            savedCredit.setTable_id(debit.getTable_id());
            savedCredit.setAmount(savedCreditDTO.getPayout());
            savedCredit.setTxnId(debit.getTxnId());
            savedCredit.setIs_cancel(savedCreditDTO.getIs_cancel());
            wallet.setCasinoBalance(user.getWallet().getCasinoBalance() + savedCreditDTO.getPayout());  // payout 값 증가
            walletRepository.save(wallet);
            creditRepository.save(savedCredit);
        } else {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "유효하지않은 요청입니다.");
        } return null;
    }
}
