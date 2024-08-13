package GInternational.server.api.repository;

import GInternational.server.api.entity.CompTransaction;
import GInternational.server.api.entity.User;
import GInternational.server.api.vo.RollingTransactionEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompTransactionRepository extends JpaRepository<CompTransaction, Long>, JpaSpecificationExecutor<CompTransaction> {

    boolean existsByUserAndCreatedAtGreaterThanEqual(User user, LocalDateTime createdAt);
}
