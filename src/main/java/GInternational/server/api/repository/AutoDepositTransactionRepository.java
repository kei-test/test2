package GInternational.server.api.repository;


import GInternational.server.api.entity.AutoDepositTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutoDepositTransactionRepository extends JpaRepository<AutoDepositTransaction,Long>,AutoDepositTransactionRepositoryCustom {
}
