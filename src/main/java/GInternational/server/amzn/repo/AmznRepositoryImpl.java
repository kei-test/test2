package GInternational.server.amzn.repo;


import GInternational.server.amzn.dto.index.AmznDetailsByTypeDTO;
import GInternational.server.amzn.dto.index.AmznPartnerObjectDTO;
import GInternational.server.amzn.dto.index.AmznUserDetailDTO;
import GInternational.server.amzn.dto.index.IsAmazonUserListDTO;
import GInternational.server.amzn.dto.total.AmznPartnerResDTO;
import GInternational.server.amzn.dto.total.AmznTotalPartnerReqDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Supplier;

import static GInternational.server.api.entity.QUser.*;
import static GInternational.server.api.entity.QWallet.*;

@Repository
@RequiredArgsConstructor
public class AmznRepositoryImpl {


    private final JPAQueryFactory queryFactory;

    public List<AmznTotalPartnerReqDTO> searchByTotalPartner() {
        List<AmznTotalPartnerReqDTO> results = queryFactory.select(Projections.constructor(AmznTotalPartnerReqDTO.class,
                user.id,
                user.username,
                user.nickname,
                wallet.amazonMoney,
                wallet.amazonMileage,
                user.recommendedCount,
                user.partnerType,
                user.daeId,
                user.bonId,
                user.buId,
                user.chongId,
                user.createdAt))
                .from(user)
                .join(wallet).on(wallet.user.id.eq(user.id))
                .where(user.daeId.isNotNull()
                        .or(user.bonId.isNotNull())
                        .or(user.buId.isNotNull())
                        .or(user.chongId.isNotNull())
                        .or(user.partnerType.eq("대본사")))
                .fetch();
        return results;
    }

    public AmznPartnerResDTO searchByPartner(String username, String nickname,Long id,String partnerType) {
        return queryFactory.select(Projections.constructor(AmznPartnerResDTO.class,
                user.id,
                user.username,
                user.nickname,
                wallet.amazonMoney,
                wallet.amazonMileage,
                user.recommendedCount,
                user.partnerType,
                user.createdAt,
                user.daeId,
                user.bonId,
                user.buId,
                user.chongId,
                user.maeId))
                .from(user)
                .join(wallet).on(wallet.user.id.eq(user.id))
                .where(usernameEq(username),nicknameEq(nickname))
                .fetchOne();
    }



    public List<IsAmazonUserListDTO> searchByIsAmazonUsers(String referredBy) {
        List<IsAmazonUserListDTO> results = queryFactory.select(Projections.constructor(IsAmazonUserListDTO.class,
                user.id,
                user.username,
                user.nickname,
                wallet.ownerName,
                wallet.sportsBalance,
                wallet.point,
                wallet.depositTotal,
                wallet.withdrawTotal,
                wallet.totalSettlement,
                user.createdAt,
                user.lastVisit))
                .from(user)
                .join(wallet).on(wallet.user.id.eq(user.id))
                .where(user.referredBy.eq(referredBy))
                .orderBy(user.createdAt.desc())
                .fetch();
        return results;

    }


    //회원 상세정보 조회 시 제공되는 상위파트너 트리구조
    public AmznPartnerObjectDTO searchByPO(Long id) {
        return queryFactory.select(Projections.constructor(AmznPartnerObjectDTO.class,
                user.id,
                user.username,
                user.partnerType,
                user.daeId,
                user.bonId,
                user.buId,
                user.chongId,
                user.maeId))
                .from(user)
                .where(user.id.eq(id))
                .fetchOne();
    }

    public AmznUserDetailDTO searchByAmznUserDetail(Long id) {
        return queryFactory.select(Projections.constructor(AmznUserDetailDTO.class,
                user.userGubunEnum,
                user.lv,
                user.password,
                user.phone,
                wallet.ownerName,
                wallet.bankName,
                wallet.number,
                user.referredBy))
                .from(user)
                .join(wallet).on(wallet.user.id.eq(id))
                .where(user.id.eq(id))
                .fetchOne();
    }

    public AmznDetailsByTypeDTO searchByUserTypeDetail(Long id) {
        return queryFactory.select(Projections.constructor(AmznDetailsByTypeDTO.class,
                user.partnerType,
                user.username,
                user.nickname,
                user.password,
                user.phone,
                user.casinoRolling,
                user.slotRolling))
                .from(user)
                .where(user.id.eq(id))
                .fetchOne();
    }


    private BooleanBuilder usernameEq(String username) {
        return nullSafeBooleanBuilder(() -> user.username.eq(username));
    }
    private BooleanBuilder nicknameEq(String nickname) {
        return nullSafeBooleanBuilder(() -> user.nickname.eq(nickname));
    }
    private BooleanBuilder nullSafeBooleanBuilder(Supplier<BooleanExpression> supplier) {
        try {
            return new BooleanBuilder(supplier.get());
        } catch (IllegalArgumentException e) {
            return new BooleanBuilder();
        }
    }
}
