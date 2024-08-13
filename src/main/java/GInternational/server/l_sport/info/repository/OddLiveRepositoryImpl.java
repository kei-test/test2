package GInternational.server.l_sport.info.repository;

import GInternational.server.l_sport.info.dto.pre.OddResponseDTO;
import GInternational.server.l_sport.info.entity.QOddLive;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static GInternational.server.l_sport.info.entity.QOdd.odd;
import static GInternational.server.l_sport.info.entity.QOddLive.*;

@RequiredArgsConstructor
public class OddLiveRepositoryImpl implements OddLiveRepositoryCustom{


    @Autowired
    @Qualifier("lsportEntityManager")
    private final JPAQueryFactory queryFactory;


    @Override
    public List<OddResponseDTO> searchByIdx(List<String> list) {
        return queryFactory.select(Projections.constructor(OddResponseDTO.class,
                        oddLive.idx,
                        oddLive.matchId,
                        oddLive.marketName,
                        oddLive.betName,
                        oddLive.betStatus,
                        oddLive.price,
                        oddLive.baseLine,
                        oddLive.lastUpdate))
                .from(oddLive)
                .where(oddLive.idx.in(list))
                .fetch();
    }
}
