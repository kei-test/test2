package GInternational.server.kplay.game.repository;

import GInternational.server.api.entity.User;
import GInternational.server.kplay.game.entity.Game;
import GInternational.server.kplay.game.entity.GameFavorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameFavoriteRepository extends JpaRepository<GameFavorite, Long> {
    Optional<GameFavorite> findByUserAndGame(User user, Game game);
    List<GameFavorite> findAllByUser(User user);
}