package GInternational.server.kplay.debit.repository;

import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.kplay.debit.dto.DebitAmazonResponseDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository;

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
    public Page<DebitAmazonResponseDTO> findByUserIdWithCreditAmount(String type, Pageable pageable) {
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
                .select(debit.id, debit.amount, debit.prd_id, debit.txnId, debit.game_id, debit.table_id,
                        debit.credit_amount, debit.created_at, debit.remainAmount,
                        product.prd_name, credit.amount, game.name, user.username, user.nickname, user.partnerType)
                .from(debit)
                .leftJoin(credit).on(debit.user_id.eq(credit.user_id).and(debit.txnId.eq(credit.txnId)))
                .leftJoin(game).on(debit.game_id.eq(game.gameIndex).and(debit.prd_id.eq(game.prdId))) // Game 테이블 조인
                .join(product).on(product.prd_id.eq(game.prdId))
                .leftJoin(user).on(user.aasId.eq(debit.user_id))
                .where(typeCondition.and(user.isAmazonUser.isTrue())) // isAmazonUser가 true인 사용자만 필터링
                .orderBy(debit.createdAt.desc())
                .distinct()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        List<DebitAmazonResponseDTO> list = results.stream()
                .map(tuple -> {
                    Long id = tuple.get(debit.id);
                    int amount = tuple.get(debit.amount);
                    int prdId = tuple.get(debit.prd_id);
                    String txnId = tuple.get(debit.txnId);
                    int gameId = tuple.get(debit.game_id);
                    String tableId = tuple.get(debit.table_id);
                    int creditAmount = tuple.get(debit.credit_amount);
                    LocalDateTime createdAt = tuple.get(debit.created_at);
                    Long remainAmount = tuple.get(debit.remainAmount);
                    String prdName = tuple.get(product.prd_name);
                    Integer creditAmountValue = tuple.get(credit.amount);
                    String gameName = tuple.get(game.name);
                    String userName = tuple.get(user.username);
                    String nickName = tuple.get(user.nickname);
                    String partnerType = tuple.get(user.partnerType);

                    // 파트너 정보 가져오기
                    User partnerUser = findPartnerUser(tuple.get(user));
                    String partnerUsername = (partnerUser != null) ? partnerUser.getUsername() : null;
                    String partnerNickname = (partnerUser != null) ? partnerUser.getNickname() : null;

                    int creditAmountIntValue = (creditAmountValue != null) ? creditAmountValue.intValue() : 0;

                    return new DebitAmazonResponseDTO(
                            id,
                            userName,
                            nickName,
                            amount,
                            prdName,
                            prdId,
                            txnId,
                            creditAmountIntValue,
                            gameId,
                            gameName,
                            tableId,
                            creditAmount,
                            createdAt,
                            remainAmount - amount,
                            partnerUsername,
                            partnerNickname,
                            partnerType
                    );
                })
                .collect(Collectors.toList());

        long totalElements = queryFactory
                .select(debit.id, debit.amount, debit.prd_id, debit.txnId, debit.game_id, debit.table_id,
                        debit.credit_amount, debit.created_at, debit.remainAmount,
                        product.prd_name, credit.amount, game.name)
                .from(debit)
                .leftJoin(credit).on(debit.user_id.eq(credit.user_id).and(debit.txnId.eq(credit.txnId)))
                .leftJoin(game).on(debit.game_id.eq(game.gameIndex).and(debit.prd_id.eq(game.prdId))) // Game 테이블 조인
                .join(product).on(product.prd_id.eq(game.prdId))
                .where(typeCondition.and(user.isAmazonUser.isTrue())) // isAmazonUser가 true인 사용자만 필터링
                .distinct()
                .fetch().size();

        return new PageImpl<>(list, pageable, totalElements);
    }

    private User findPartnerUser(User amazonUser) {
        // 파트너 유저를 찾기 위한 로직
        if (amazonUser.getDaeId() != null) {
            return userRepository.findById(amazonUser.getDaeId()).orElse(null);
        } else if (amazonUser.getBonId() != null) {
            return userRepository.findById(amazonUser.getBonId()).orElse(null);
        } else if (amazonUser.getBuId() != null) {
            return userRepository.findById(amazonUser.getBuId()).orElse(null);
        } else if (amazonUser.getChongId() != null) {
            return userRepository.findById(amazonUser.getChongId()).orElse(null);
        } else if (amazonUser.getMaeId() != null) {
            return userRepository.findById(amazonUser.getMaeId()).orElse(null);
        }
        return null;
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