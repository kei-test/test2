package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoRechargeBankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutoRechargeBankAccountRepository extends JpaRepository<AutoRechargeBankAccount, Long> {
    Optional<AutoRechargeBankAccount> findByNumber(String number);
}