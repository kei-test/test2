package GInternational.server.api.repository;

import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.api.entity.MoneyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MoneyLogRepository extends JpaRepository<MoneyLog, Long>, JpaSpecificationExecutor<MoneyLog> {
}