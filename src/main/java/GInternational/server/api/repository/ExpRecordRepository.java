package GInternational.server.api.repository;

import GInternational.server.api.entity.ExpRecord;
import GInternational.server.api.vo.ExpRecordEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;

public interface ExpRecordRepository extends JpaRepository<ExpRecord, Long>, JpaSpecificationExecutor<ExpRecord> {

    @Query("SELECT COUNT(e) > 0 FROM exp_record e WHERE e.userId = :userId AND e.content = :content AND e.createdAt BETWEEN :startOfDay AND :endOfDay")
    boolean existsByUserIdAndContentAndDateRange(Long userId, ExpRecordEnum content, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT COUNT(e) FROM exp_record e WHERE e.userId = :userId AND e.content = :content AND e.createdAt BETWEEN :startOfDay AND :endOfDay")
    long countByUserIdAndContentAndDateRange(Long userId, ExpRecordEnum content, LocalDateTime startOfDay, LocalDateTime endOfDay);

    Page<ExpRecord> findAll(Specification<ExpRecord> spec, Pageable pageable);

}
