package GInternational.server.kplay.game.repository;

import GInternational.server.kplay.game.entity.Game;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static GInternational.server.kplay.game.entity.QGame.game;

@RequiredArgsConstructor
public class GameRepositoryImpl implements GameRepositoryCustom{


    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Game> searchByPrdGame(int prdId, Pageable pageable) {
        List<Game> prdGame = queryFactory.select(game)
                .from(game)
                .where(game.prdId.eq(prdId)
                        .and(game.isEnabled.eq(1)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(game)
                .where(game.prdId.eq(prdId).and(game.isEnabled.eq(1)))
                .fetch().size();

        return new PageImpl<>(prdGame,pageable,totalElements);
    }

    @Override
    public Page<Game> searchByType(String gameType,String gameCategory, Pageable pageable) {
        List<Game> typeGame = queryFactory.selectFrom(game)
                .where(game.type.eq(gameType)
                        .or(game.gameCategory.eq(gameCategory))
                        .and(game.isEnabled.eq(1)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(game)
                .where(game.type.eq(gameType)
                        .or(game.gameCategory.eq(gameCategory))
                        .and(game.isEnabled.eq(1)))
                .fetch().size();

        return new PageImpl<>(typeGame,pageable,totalElements);
    }

    @Override
    public Page<Game> searchByNullCondition(Pageable pageable) {
        List<Game> findAll = queryFactory.selectFrom(game)
                .where(game.isEnabled.eq(1))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(game)
                .where(game.isEnabled.eq(1))
                .fetch().size();

        return new PageImpl<>(findAll, pageable,totalElements);
    }
}
