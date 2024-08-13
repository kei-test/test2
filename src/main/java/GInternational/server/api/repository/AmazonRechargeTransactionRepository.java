package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonRechargeTransaction;
import GInternational.server.api.vo.AmazonTransactionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AmazonRechargeTransactionRepository extends JpaRepository<AmazonRechargeTransaction, Long> {

    List<AmazonRechargeTransaction> findAllByProcessedAtBetweenAndStatus(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            AmazonTransactionEnum status);

    // 특정 날짜에 처리된 트랜잭션 조회
    Page<AmazonRechargeTransaction> findByUserIdAndStatusAndProcessedAtBetween(
            Long userId,
            AmazonTransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable);

    List<AmazonRechargeTransaction> findByUserIdAndStatusAndProcessedAtBetween(
            Long userId,
            AmazonTransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

    List<AmazonRechargeTransaction> findByStatusAndProcessedAtBetween(
            AmazonTransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

    List<AmazonRechargeTransaction> findAllByCreatedAtBetweenAndStatus(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            AmazonTransactionEnum status);


    Page<AmazonRechargeTransaction> findByUserId(Long userId, Pageable pageable);
    Long countByUserId(Long userId);

    List<AmazonRechargeTransaction> findByUserIdAndStatus(Long userId, AmazonTransactionEnum status);
}