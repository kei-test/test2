package GInternational.server.api.repository;


import GInternational.server.api.entity.AmazonLoginHistory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface AmazonLoginHistoryRepository extends JpaRepository<AmazonLoginHistory, Long>, JpaSpecificationExecutor<AmazonLoginHistory> {
}
