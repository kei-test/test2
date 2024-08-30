package GInternational.server.kplay.debit.repository;

import GInternational.server.api.controller.UserController;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.kplay.debit.dto.DebitAmazonResponseDTO;
import GInternational.server.kplay.debit.entity.Debit;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static GInternational.server.api.entity.QUser.user;
import static GInternational.server.kplay.credit.entity.QCredit.credit;
import static GInternational.server.kplay.debit.entity.QDebit.debit;
import static GInternational.server.kplay.game.entity.QGame.game;
import static GInternational.server.kplay.product.entity.QProduct.product;

@Repository
@Primary
@RequiredArgsConstructor
public class DebitRepositoryImpl implements DebitCustomRepository {


    private final JPAQueryFactory queryFactory;
    private final UserRepository userRepository;

    private static final Logger logger = LoggerFactory.getLogger(DebitRepositoryImpl.class);

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

        BooleanExpression userCondition = user.isAmazonUser.isTrue();
        BooleanExpression finalCondition = userCondition;
        if (typeCondition != null) {
            finalCondition = finalCondition.and(typeCondition);
        }

        List<Tuple> results = queryFactory
                .select(debit.id, debit.amount, debit.prd_id, debit.txnId, debit.game_id, debit.table_id,
                        debit.credit_amount, debit.created_at, debit.remainAmount,
                        product.prd_name, credit.amount, game.name, user.aasId) // 사용자 ID를 쿼리에서 가져옴
                .from(debit)
                .leftJoin(credit).on(debit.user_id.eq(credit.user_id).and(debit.txnId.eq(credit.txnId)))
                .leftJoin(game).on(debit.game_id.eq(game.gameIndex).and(debit.prd_id.eq(game.prdId))) // Game 테이블 조인
                .join(product).on(product.prd_id.eq(game.prdId))
                .leftJoin(user).on(user.aasId.eq(debit.user_id).and(userCondition)) // 수정된 조인 조건
                .where(finalCondition) // 최종 조건 적용
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
                    Integer aasId = tuple.get(user.aasId); // ID 가져오기

                    // ID를 사용해 파트너 정보 가져오기
                    User partnerUser = findPartnerUser(aasId);

                    String partnerUsername = (partnerUser != null && Arrays.asList("대본사", "본사", "부본사", "총판", "매장").contains(partnerUser.getPartnerType()))
                            ? partnerUser.getUsername() : null;
                    String partnerNickname = (partnerUser != null && Arrays.asList("대본사", "본사", "부본사", "총판", "매장").contains(partnerUser.getPartnerType()))
                            ? partnerUser.getNickname() : null;
                    String partnerTypeValue = (partnerUser != null && Arrays.asList("대본사", "본사", "부본사", "총판", "매장").contains(partnerUser.getPartnerType()))
                            ? partnerUser.getPartnerType() : null;

                    int creditAmountIntValue = (creditAmountValue != null) ? creditAmountValue : 0; // null 체크 후 기본값 설정

                    return new DebitAmazonResponseDTO(
                            id,
                            partnerUsername, // 이름
                            partnerNickname, // 닉네임
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
                            partnerTypeValue
                    );
                })
                .collect(Collectors.toList());

        long totalElements = queryFactory
                .select(debit.id, debit.amount, debit.prd_id, debit.txnId, debit.game_id, debit.table_id,
                        debit.credit_amount, debit.created_at, debit.remainAmount,
                        product.prd_name, credit.amount, game.name, user.aasId) // 사용자 ID를 쿼리에서 가져옴
                .from(debit)
                .leftJoin(credit).on(debit.user_id.eq(credit.user_id).and(debit.txnId.eq(credit.txnId)))
                .leftJoin(game).on(debit.game_id.eq(game.gameIndex).and(debit.prd_id.eq(game.prdId))) // Game 테이블 조인
                .join(product).on(product.prd_id.eq(game.prdId))
                .leftJoin(user).on(user.aasId.eq(debit.user_id).and(userCondition)) // 수정된 조인 조건
                .where(finalCondition) // 최종 조건 적용
                .distinct()
                .fetch().size();

        return new PageImpl<>(list, pageable, totalElements);
    }

    private User findPartnerUser(Integer aasId) {
        if (aasId == null) {
            throw new IllegalArgumentException("User ID must not be null");
        }
        User user = userRepository.findByAasId(aasId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        if (user != null && user.getReferredBy() != null) {
            User partnerUser = userRepository.findByUsername(user.getReferredBy());
            logger.debug("Amazon user: {}", user);
            logger.debug("Found partner user: {}", partnerUser);
            logger.debug("Partner type: {}", partnerUser != null ? partnerUser.getPartnerType() : "null");
            return partnerUser;
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