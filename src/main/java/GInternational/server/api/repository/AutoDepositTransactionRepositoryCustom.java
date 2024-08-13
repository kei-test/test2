package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoDepositTransaction;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface AutoDepositTransactionRepositoryCustom {

    //30분이 지난 신청건 조회
    List<AutoDepositTransaction> searchByAutoDepositTransactionCondition();

    List<AutoDepositTransaction> findByAutoDepositTransaction(LocalDate startDate, LocalDate endDate);

    List<AutoDepositTransaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

}
