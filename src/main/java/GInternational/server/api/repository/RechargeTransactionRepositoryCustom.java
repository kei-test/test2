package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoTransaction;
import GInternational.server.api.entity.RechargeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface RechargeTransactionRepositoryCustom {


    Page<RechargeTransaction> findByUserIdAndTransaction(Long userId, Pageable pageable);
    Long countByUserId(Long userId);

    //1-2
    long sumRechargeAmountByProcessedAt(Long userId,LocalDate startDate, LocalDate endDate);

    List<RechargeTransaction> searchByRechargeTransactionCondition();
}
