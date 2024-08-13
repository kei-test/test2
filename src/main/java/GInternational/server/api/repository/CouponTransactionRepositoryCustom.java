package GInternational.server.api.repository;

import GInternational.server.api.entity.CouponTransaction;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponTransactionRepositoryCustom {

    List<CouponTransaction> findByUserIdAndDate(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Long countByUserId(Long userId);
}
