package GInternational.server.api.repository;

import GInternational.server.api.entity.CasinoTransaction;
import GInternational.server.api.vo.TransactionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CasinoRepository extends JpaRepository<CasinoTransaction,Long>, JpaSpecificationExecutor<CasinoTransaction>, CasinoRepositoryCustom {

    List<CasinoTransaction> findByProcessedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    @Query("SELECT SUM(ct.usedSportsBalance) FROM casino_transaction ct WHERE ct.status = :status AND ct.description = :description AND ct.processedAt BETWEEN :startDateTime AND :endDateTime")
    Long sumUsedSportsBalanceForConversion(@Param("status") TransactionEnum status, @Param("description") String description, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT SUM(ct.usedCasinoBalance) FROM casino_transaction ct WHERE ct.status = :status AND ct.description = :description AND ct.processedAt BETWEEN :startDateTime AND :endDateTime")
    Long sumUsedCasinoBalanceForConversion(@Param("status") TransactionEnum status, @Param("description") String description, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT COUNT(ct) FROM casino_transaction ct WHERE ct.status = :status AND ct.description = :description AND ct.processedAt BETWEEN :startDateTime AND :endDateTime")
    int countByDescriptionAndStatus(@Param("status") TransactionEnum status, @Param("description") String description, @Param("startDateTime") LocalDateTime startDateTime, @Param("endDateTime") LocalDateTime endDateTime);
}
