package GInternational.server.api.repository;

import GInternational.server.api.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long>, AccountRepositoryCustom, JpaSpecificationExecutor<Account> {

    Optional <Account> findById(Long id);
}
