package GInternational.server.api.repository;

import GInternational.server.api.entity.AdminLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface AdminLoginHistoryRepository extends JpaRepository<AdminLoginHistory, Long>, JpaSpecificationExecutor<AdminLoginHistory> {

    List<AdminLoginHistory> findAllByOrderByAttemptDateDesc();

    List<AdminLoginHistory> findByLoginResult(String loginResult);
}
