package GInternational.server.kplay.debit.repository;

import GInternational.server.kplay.debit.entity.Debit;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static GInternational.server.kplay.credit.entity.QCredit.credit;
import static GInternational.server.kplay.debit.entity.QDebit.debit;
import static GInternational.server.kplay.game.entity.QGame.game;
import static GInternational.server.kplay.product.entity.QProduct.product;
import static GInternational.server.api.entity.QUser.user;

@Repository
@Primary
@RequiredArgsConstructor
public class DebitRepositoryImpl implements DebitCustomRepository {


    private final JPAQueryFactory queryFactory;

    @Override
    public List<Debit> findDataWithNOMatchingTxnId() {
        return queryFactory
                .selectFrom(debit)
                .leftJoin(debit.credit, credit)
                .on(debit.txnId.eq(credit.txnId))
                .where(credit.id.isNull())
                .fetch();
    }

    @Override
    public Page<Debit> findByUserId(int userId, Pageable pageable) {
        List<Debit> result = queryFactory.select(debit)
                .from(debit)
                .orderBy(debit.createdAt.desc())
                .where(debit.user_id.eq(userId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(debit)
                .where(debit.user_id.eq(debit.user_id))
                .fetch().size();

        return new PageImpl<>(result, pageable, totalElements);
    }

    @Override
    public Page<Tuple> findByUserIdWithCreditAmount(int userId, String type, Pageable pageable) {


        // type 매개변수를 기반으로 조건 정의
        BooleanExpression typeCondition = null;
        if ("casino".equals(type)) {
            typeCondition = debit.prd_id.between(1, 99);
        } else if ("sports".equals(type)) {
            typeCondition = debit.prd_id.between(100, 199);
        } else if ("slot".equals(type)) {
            typeCondition = debit.prd_id.between(200, 299);
        } else if ("minigame".equals(type)) {
            typeCondition = debit.prd_id.in(300,301,10002);
        }

        List<Tuple> results = queryFactory.select(debit, credit.amount, game.name)
                .from(debit)
                .leftJoin(credit).on(debit.user_id.eq(credit.user_id).and(debit.txnId.eq(credit.txnId)))
                .leftJoin(game).on(debit.game_id.eq(game.gameIndex).and(debit.prd_id.eq(game.prdId))) // Game 테이블 조인
                .where(debit.user_id.eq(userId).and(typeCondition))
                .orderBy(debit.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.select(debit, credit.amount, game.name)
                .from(debit)
                .leftJoin(credit).on(debit.user_id.eq(credit.user_id).and(debit.txnId.eq(credit.txnId)))
                .leftJoin(game).on(debit.game_id.eq(game.gameIndex).and(debit.prd_id.eq(game.prdId))) // Game 테이블 조인
                .where(debit.user_id.eq(userId).and(typeCondition)) // type 조건 적용
                .fetch().size();

        return new PageImpl<>(results, pageable, totalElements);
    }

    @Override
    public Page<Tuple> findByUserIdWithCreditAmount(String type, Pageable pageable) {


        // type 매개변수를 기반으로 조건 정의
        BooleanExpression typeCondition = null;
        if ("casino".equals(type)) {
            typeCondition = debit.prd_id.between(1, 99);
        } else if ("sports".equals(type)) {
            typeCondition = debit.prd_id.between(100, 199);
        } else if ("slot".equals(type)) {
            typeCondition = debit.prd_id.between(200, 299);
        }

        List<Tuple> results = queryFactory
                .select(debit.id, debit, product.prd_name, credit.amount, game.name, user.username)
                .from(debit)
                .leftJoin(credit).on(debit.user_id.eq(credit.user_id).and(debit.txnId.eq(credit.txnId)))
                .leftJoin(game).on(debit.game_id.eq(game.gameIndex).and(debit.prd_id.eq(game.prdId))) // Game 테이블 조인
                .join(product).on(product.prd_id.eq(game.prdId))
                .leftJoin(user).on(user.aasId.eq(debit.user_id))
                .where((typeCondition))
//                        .and(user.referredBy.eq(referrerName)))
                .orderBy(debit.createdAt.desc())
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory
                .select(debit.id, debit, product.prd_name, credit.amount, game.name)
                .from(debit)
                .leftJoin(credit).on(debit.user_id.eq(credit.user_id).and(debit.txnId.eq(credit.txnId)))
                .leftJoin(game).on(debit.game_id.eq(game.gameIndex).and(debit.prd_id.eq(game.prdId))) // Game 테이블 조인
                .join(product).on(product.prd_id.eq(game.prdId))
                .where(typeCondition) // type 조건 적용
                .distinct()
                .fetch().size();

        return new PageImpl<>(results, pageable, totalElements);
    }
}


//    @Override
//    public Debit findDataWithNOMatchingTxnId() {
//        return queryFactory
//                .selectFrom(debit)
//                .leftJoin(debit.credit, credit)
//                .on(debit.txnId.eq(credit.txnId))
//                .where(credit.id.isNull())
//                .fetchOne();
//    }
//}


/**
 * 1. credit이 되지않은 debit 을 찾는다.(단건)
 * 2. ResultController 에서 DTO 를 요청데이터로 매핑하고 컨트롤러에서 찾은 debit을 set한다.
 * 3. DTO에 바인딩한다.  (게임사측으로 요청이 들어감)
 * 4. responseDTO 를 생성해서 게임사에서 넘겨주는 데이터를 set한다.
 * 5. 받은 데이터 리턴
 */