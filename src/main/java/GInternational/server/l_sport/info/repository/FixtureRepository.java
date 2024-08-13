package GInternational.server.l_sport.info.repository;

import GInternational.server.l_sport.info.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FixtureRepository extends JpaRepository<Match, String>, FixtureRepositoryCustom {
    Optional<Match> findByMatchId(String matchId);


}
