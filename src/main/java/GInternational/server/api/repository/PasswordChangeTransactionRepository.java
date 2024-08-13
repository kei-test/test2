package GInternational.server.api.repository;

import GInternational.server.api.entity.PasswordChangeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PasswordChangeTransactionRepository extends JpaRepository<PasswordChangeTransaction, Long>, JpaSpecificationExecutor<PasswordChangeTransaction> {

    int countByStatus(String status);
}
