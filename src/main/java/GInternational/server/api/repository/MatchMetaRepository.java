package GInternational.server.api.repository;

import GInternational.server.api.entity.meta.MatchMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MatchMetaRepository extends JpaRepository<MatchMetaData, Long> {

    Optional<MatchMetaData> findByMatchId(String matchId);
}
