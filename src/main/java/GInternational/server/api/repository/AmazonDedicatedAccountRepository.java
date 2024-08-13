package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonDedicatedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmazonDedicatedAccountRepository extends JpaRepository<AmazonDedicatedAccount, Long> {
}