package GInternational.server.common.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class QueryDslConfig {

    @PersistenceContext(unitName = "entityManager")
    private EntityManager entityManager;

    @PersistenceContext(unitName = "lsportEntityManager")
    private EntityManager lsportEntityManager;

    @Bean
    @Primary
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }

    @Bean
    @Qualifier("lsportEntityManager")
    public JPAQueryFactory secondJpaQueryFactory() {
        return new JPAQueryFactory(lsportEntityManager);
    }

}