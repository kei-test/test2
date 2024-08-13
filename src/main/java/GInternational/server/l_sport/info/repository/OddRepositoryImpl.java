package GInternational.server.l_sport.info.repository;


import GInternational.server.l_sport.info.dto.pre.OddResponseDTO;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

import static GInternational.server.l_sport.info.entity.QOdd.odd;

@RequiredArgsConstructor
public class OddRepositoryImpl implements OddRepositoryCustom{


    @Autowired
    @Qualifier("lsportEntityManager")  //각각의 엔티티매니저를 지정하여 주입한다.
    private final JPAQueryFactory queryFactory;


    @Override
    public List<OddResponseDTO> searchByIdx(List<String> list) {
        return queryFactory.select(Projections.constructor(OddResponseDTO.class,
                        odd.idx,
                        odd.matchId,
                        odd.marketName,
                        odd.betName,
                        odd.betStatus,
                        odd.price,
                        odd.baseLine,
                        odd.lastUpdate))
                .from(odd)
                .where(odd.idx.in(list))
                .fetch();
    }
}
