package GInternational.server.api.repository;

import GInternational.server.api.entity.CouponTransaction;
import GInternational.server.api.vo.CouponTransactionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface CouponTransactionRepository extends JpaRepository<CouponTransaction, Long>, JpaSpecificationExecutor<CouponTransaction>, CouponTransactionRepositoryCustom {

    List<CouponTransaction> findByStatusAndCreatedAtBetween(CouponTransactionEnum status, LocalDateTime startDateTime, LocalDateTime endDateTime);
}