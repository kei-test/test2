package GInternational.server.api.repository;

import GInternational.server.api.entity.LoginSuccessHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LoginSuccessHistoryRepository extends JpaRepository<LoginSuccessHistory, Long>, JpaSpecificationExecutor<LoginSuccessHistory> {
}
