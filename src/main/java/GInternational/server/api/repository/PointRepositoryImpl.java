package GInternational.server.api.repository;

import GInternational.server.api.entity.PointTransaction;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static GInternational.server.api.entity.QPointTransaction.pointTransaction;
import static GInternational.server.api.entity.QUser.user;

@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PointTransaction> findByUserIdAndPointTransaction(Long userId, Pageable pageable) {
        List<PointTransaction> results = queryFactory.selectFrom(pointTransaction)
                .innerJoin(pointTransaction.user, user)
                .where(user.id.eq(userId))
                .orderBy(pointTransaction.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        return new PageImpl<>(results,pageable,results.size());
    }

    @Override
    public Long countByUserId(Long userId) {
        return (long) queryFactory.selectFrom(pointTransaction)
                .innerJoin(pointTransaction.user,user)
                .where(user.id.eq(userId))
                .fetch().size();
    }
}
