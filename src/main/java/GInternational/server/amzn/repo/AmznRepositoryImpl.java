package GInternational.server.amzn.repo;


import GInternational.server.amzn.dto.index.AmznDetailsByTypeDTO;
import GInternational.server.amzn.dto.index.AmznPartnerObjectDTO;
import GInternational.server.amzn.dto.index.AmznUserDetailDTO;
import GInternational.server.amzn.dto.index.IsAmazonUserListDTO;
import GInternational.server.amzn.dto.total.AmznDaeOrDSTResDTO;
import GInternational.server.amzn.dto.total.AmznPartnerResDTO;
import GInternational.server.amzn.dto.total.AmznTotalPartnerReqDTO;
import GInternational.server.amzn.dto.total.AmznTotalPartnerReqDTO2;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static GInternational.server.api.entity.QUser.*;
import static GInternational.server.api.entity.QWallet.*;

@Repository
@RequiredArgsConstructor
public class AmznRepositoryImpl {


    private final JPAQueryFactory queryFactory;



    public List<AmznDaeOrDSTResDTO> getDaeOrDSTPartner() {
        List<AmznDaeOrDSTResDTO> partnerInfo = queryFactory.select(Projections.constructor(AmznDaeOrDSTResDTO.class,
                        user.id,
                        user.username,
                        user.nickname,
                        user.casinoRolling,
                        user.slotRolling,
                        user.recommendedCount,
                        user.userGubunEnum,
                        user.partnerType))
                .from(user)
                .where(user.partnerType.eq("대본사")
                        .or(user.partnerType.eq("DST")))
                .orderBy(user.createdAt.desc())
                .fetch();

        List<String> usernames = partnerInfo.stream()
                .map(AmznDaeOrDSTResDTO::getUsername)
                .collect(Collectors.toList());


        List<AmznDaeOrDSTResDTO> userCount = queryFactory.select(Projections.constructor(AmznDaeOrDSTResDTO.class,
                        user.referredBy,
                        user.userGubunEnum))
                .from(user)
                .where(user.referredBy.in(usernames)
                        .and(user.isAmazonUser.isTrue())
                        .or(user.isDstUser.isTrue())
                        .and(user.partnerType.isNull())
                        .and(user.role.eq("ROLE_USER")))
                .fetch();


        for (AmznDaeOrDSTResDTO partner : partnerInfo) {
            for (AmznDaeOrDSTResDTO user : userCount) {
                if (partner.getUsername().equals(user.getReferredBy())) {
                    if (user.getUserGubunEnum().get표시이름().equals("대기")) {
                        partner.setWait(partner.getWait() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("정상")) {
                        partner.setNormal(partner.getNormal() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("단폴")) {
                        partner.setSingleF(partner.getSingleF() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("배당")) {
                        partner.setOdd(partner.getOdd() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("호원")) {
                        partner.setHowon(partner.getHowon() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("불량")) {
                        partner.setBad(partner.getBad() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("악의")) {
                        partner.setMalice(partner.getMalice() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("정지")) {
                        partner.setStop(partner.getStop() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("거절")) {
                        partner.setReject(partner.getReject() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("하락탈퇴")) {
                        partner.setDownwardWithdrawal(partner.getDownwardWithdrawal() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("탈퇴1")) {
                        partner.setWithdraw1(partner.getWithdraw1() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("탈퇴2")) {
                        partner.setWithdraw2(partner.getWithdraw2() +1);
                    } else if (user.getUserGubunEnum().get표시이름().equals("탈퇴3")) {
                        partner.setWithdraw3(partner.getWithdraw3() +1);
                    }
                }
            }
            partner.setTotalRolling(partner.getCasinoRolling() + partner.getSlotRolling());
            if (partner.getPartnerType().equals("대본사")) {
                partner.setType("아마존");
            } else if (partner.getPartnerType().equals("DST")){
                partner.setType("DST");
            }

        }
        return partnerInfo;
    }



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

    public List<AmznTotalPartnerReqDTO2> searchByTotalPartner2() {
        List<AmznTotalPartnerReqDTO2> results = queryFactory.select(Projections.constructor(AmznTotalPartnerReqDTO2.class,
                        user.id,
                        user.username,
                        user.nickname,
                        wallet.amazonMoney,
                        wallet.amazonPoint,
                        wallet.todayDeposit,
                        wallet.todayWithdraw,
                        wallet.totalAmazonDeposit,
                        wallet.totalAmazonWithdraw,
                        wallet.totalAmazonSettlement,
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
                .where(user.referredBy.eq(referredBy)
                        .and(user.partnerType.isNull()))
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
