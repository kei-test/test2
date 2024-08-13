package GInternational.server.l_sport.info.repository;

import GInternational.server.l_sport.info.entity.Odd;
import GInternational.server.l_sport.info.entity.OddLive;
import org.springframework.data.jpa.repository.JpaRepository;


public interface OddLiveRepository extends JpaRepository<OddLive, String>,OddLiveRepositoryCustom {

    OddLive findByIdxAndMatchId(String idx, String matchId);
}
