package GInternational.server.l_sport.info.repository;

import GInternational.server.l_sport.info.entity.OddLive;
import GInternational.server.l_sport.info.entity.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<Settlement,Long> {


    Settlement findByBetIdxAndMatchId(String betIdx, String matchId);

}
