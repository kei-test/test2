package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonExchangeTransaction;
import GInternational.server.api.vo.AmazonTransactionEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AmazonExchangeRepository extends JpaRepository<AmazonExchangeTransaction,Long> {

    // 특정 날짜에 처리된 트랜잭션 조회
    Page<AmazonExchangeTransaction> findByUserIdAndStatusAndProcessedAtBetween(
            Long userId,
            AmazonTransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            Pageable pageable);

    List<AmazonExchangeTransaction> findByUserIdAndStatusAndProcessedAtBetween(
            Long userId,
            AmazonTransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

    List<AmazonExchangeTransaction> findByStatusAndProcessedAtBetween(
            AmazonTransactionEnum status,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime);

    List<AmazonExchangeTransaction> findAllByCreatedAtBetweenAndStatus(
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            AmazonTransactionEnum status);

    Page<AmazonExchangeTransaction> findByUserId(Long userId, Pageable pageable);
    Long countByUserId(Long userId);

    List<AmazonExchangeTransaction> findByUserIdAndStatus(Long userId, AmazonTransactionEnum status);
}