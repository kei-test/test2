package GInternational.server.amzn.repo;

import GInternational.server.api.entity.AmazonRollingTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmazonRollingTransactionRepository extends JpaRepository<AmazonRollingTransaction, Long> {
}
