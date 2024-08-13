package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoTransactionRepository extends JpaRepository<AutoTransaction,Long>, AutoTransactionRepositoryCustom {
}
