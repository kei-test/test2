package GInternational.server.kplay.game.repository;

import GInternational.server.kplay.game.entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long>,GameRepositoryCustom{
    Optional<Game> findById(Long id);

    List<Game> findByPrdIdAndGameIndex(int prdId, int gameIndex);
}
