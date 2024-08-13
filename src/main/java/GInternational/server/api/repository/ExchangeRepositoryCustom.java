package GInternational.server.api.repository;

import GInternational.server.api.entity.ExchangeTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface ExchangeRepositoryCustom {
    Page<ExchangeTransaction> findByUserIdAndExchangeTransaction(Long userId, Pageable pageable);
    Long countByUserId(Long userId);

    //1-2 회원종합
    long sumExchangeAmountByProcessedAt(Long userId, LocalDate startDate, LocalDate endDate);

    List<ExchangeTransaction> findByExchangeTransaction(LocalDate startDate, LocalDate endDate);
}
