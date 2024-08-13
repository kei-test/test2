package GInternational.server.kplay.credit.repository;

import GInternational.server.kplay.credit.entity.Credit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditRepository extends JpaRepository<Credit,Long> {
    //Optional<Credit> findByTxnId(String txnId);

    boolean existsByTxnId(String txnId);
}
