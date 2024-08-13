package GInternational.server.kplay.game.repository;

import GInternational.server.kplay.game.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GameRepositoryCustom{


    Page<Game> searchByPrdGame(int prdId, Pageable pageable);
    Page<Game> searchByType(String type,String gameCategory, Pageable pageable);
    Page<Game> searchByNullCondition(Pageable pageable);
}
