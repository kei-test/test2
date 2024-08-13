package GInternational.server.api.repository;

import GInternational.server.api.entity.Articles;
import GInternational.server.api.entity.QArticles;
import GInternational.server.api.entity.QCategory;
import GInternational.server.security.auth.PrincipalDetails;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static GInternational.server.api.entity.QArticles.*;
import static GInternational.server.api.entity.QCategory.*;

@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom{

    private final JPAQueryFactory queryFactory;


    // 1-1
    //카테고리별 게시물 가져오기
    @Override
    public Page<Articles> findByCategoryAndArticles(Long categoryId, Pageable pageable, PrincipalDetails principalDetails) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QArticles.articles.category.id.eq(categoryId));

        // 관리자 또는 매니저인 경우 viewStatus 상관없이 조회, 그 외는 "노출" 상태인 게시물만 조회
        if (!(principalDetails.getUser().getRole().equals("ROLE_ADMIN") || principalDetails.getUser().getRole().equals("ROLE_MANAGER"))) {
            builder.and(QArticles.articles.viewStatus.eq("노출"));
        }

        // 게시물 리스트와 총 게시물 수 계산
        List<Articles> articlesList = queryFactory.selectFrom(QArticles.articles)
                .where(builder)
                .orderBy(QArticles.articles.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory.selectFrom(QArticles.articles)
                .where(builder)
                .fetchCount();

        return new PageImpl<>(articlesList, pageable, total);
    }

    //1-2
    //특정 카테고리에 담긴 게시물 갯수 가져오기
    @Override
    public Long countByCategoryArticles(Long  categoryId) {
        return (long) queryFactory.selectFrom(articles)
                .innerJoin(articles.category,category)
                .where(category.id.eq(categoryId))
                .fetch()
                .size();
    }

    @Override
    public Page<Articles> searchByMyArticles(String categoryName, Long userId, Pageable pageable, PrincipalDetails principalDetails) {
        BooleanExpression condition = articles.category.name.eq(categoryName)
                .and(articles.writer.id.eq(userId));

        if (!principalDetails.getUser().getRole().equals("ROLE_ADMIN") && !principalDetails.getUser().getRole().equals("ROLE_MANAGER")) {
            condition = condition.and(articles.viewStatus.eq("노출"));
        }

        List<Articles> results = queryFactory
                .selectFrom(articles)
                .where(condition)
                .orderBy(articles.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long totalElements = queryFactory
                .selectFrom(articles)
                .where(condition)
                .fetchCount();

        return new PageImpl<>(results, pageable, totalElements);
    }


    @Override
    public Long findMaxId() {
        return queryFactory
                .select(articles.id.max())
                .from(articles)
                .fetchOne();
    }

    @Override
    public List<Articles> searchByAdvancedCriteria(String title, String content, String nickname, String viewStatus, String categoryName, LocalDateTime startDateTime, LocalDateTime endDateTime, String username) {
        QArticles articles = QArticles.articles;
        BooleanBuilder builder = new BooleanBuilder();

        if (title != null) {
            builder.and(articles.title.containsIgnoreCase(title));
        }
        if (content != null) {
            builder.and(articles.content.containsIgnoreCase(content));
        }
        if (nickname != null) {
            builder.and(articles.writer.nickname.eq(nickname));
        }
        if (viewStatus != null) {
            builder.and(articles.viewStatus.eq(viewStatus));
        }
        if (categoryName != null) {
            builder.and(articles.category.name.eq(categoryName));
        }
        if (startDateTime != null && endDateTime != null) {
            builder.and(articles.createdAt.between(startDateTime, endDateTime));
        }
        if (username != null) {
            builder.and(articles.writer.username.eq(username));
        }

        return queryFactory
                .selectFrom(articles)
                .where(builder)
                .orderBy(articles.id.desc())
                .fetch();
    }
}
