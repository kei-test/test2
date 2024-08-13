package GInternational.server.api.repository;

import GInternational.server.api.entity.Account;
import GInternational.server.api.vo.AppStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

import static GInternational.server.api.entity.QAccount.account;

@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepositoryCustom{


    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Account> searchByAppStatus(AppStatus status, Pageable pageable) {
        List<Account> results = queryFactory.select(account)
                .from(account)
                .where(account.status.eq(status))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory.selectFrom(account)
                .where(account.status.eq(status))
                .fetch().size();

        return new PageImpl<>(results,pageable,totalElements);
    }
}
