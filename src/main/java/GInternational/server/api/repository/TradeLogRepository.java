package GInternational.server.api.repository;

import GInternational.server.api.entity.TradeLog;
import GInternational.server.api.vo.TradeLogCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeLogRepository extends JpaRepository<TradeLog, Long>, JpaSpecificationExecutor<TradeLog> {
    List<TradeLog> findByCategory(TradeLogCategory category);
}
