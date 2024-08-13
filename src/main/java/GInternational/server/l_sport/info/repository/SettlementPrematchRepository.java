package GInternational.server.l_sport.info.repository;

import GInternational.server.l_sport.info.entity.Settlement;
import GInternational.server.l_sport.info.entity.SettlementPrematch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementPrematchRepository extends JpaRepository<SettlementPrematch,Long> {

    SettlementPrematch findByBetIdxAndMatchId(String betIdx, String matchId);


}
