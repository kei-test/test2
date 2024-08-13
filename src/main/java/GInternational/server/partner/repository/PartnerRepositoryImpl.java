package GInternational.server.partner.repository;

import GInternational.server.partner.dto.PartnerCountDTO;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import static GInternational.server.api.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class PartnerRepositoryImpl implements PartnerRepositoryCustom{

    private final JPAQueryFactory queryFactory;



    @Override
    public PartnerCountDTO searchByAmazonUserList() {

        long daeId = queryFactory.select(user.count())
                .from(user)
                .where(user.daeId.isNotNull())
                .fetchOne();

        long bonId = queryFactory.select(user.count())
                .from(user)
                .where(user.bonId.isNotNull())
                .fetchOne();

        long buId = queryFactory.select(user.count())
                .from(user)
                .where(user.buId.isNotNull())
                .fetchOne();

        long chongId = queryFactory.select(user.count())
                .from(user)
                .where(user.chongId.eq(user.chongId))
                .fetchOne();


        return new PartnerCountDTO(daeId, bonId, buId, chongId);
    }

    @Override
    public PartnerCountDTO searchByPartnerTypeCount() {

        long daeId = queryFactory.select(user.count())
                .from(user)
                .where(user.partnerType.eq("대본사"))
                .fetchOne();


        long bonId = queryFactory.select(user.count())
                .from(user)
                .where(user.partnerType.eq("본사"))
                .fetchOne();

        long buId = queryFactory.select(user.count())
                .from(user)
                .where(user.partnerType.eq("부본사"))
                .fetchOne();

        long chongId = queryFactory.select(user.count())
                .from(user)
                .where(user.partnerType.eq("총판"))
                .fetchOne();

        long maeId = queryFactory.select(user.count())
                .from(user)
                .where(user.partnerType.eq("매장"))
                .fetchOne();

        return new PartnerCountDTO(daeId,bonId,buId,chongId,maeId);
    }
}
