package GInternational.server.common.config;

import org.mapstruct.Mapper;
import org.mapstruct.MapperConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(entityManagerFactoryRef = "entityManager",
        transactionManagerRef = "clientServerTransactionManager",
        basePackages = {"GInternational.server.api.repository",
                "GInternational.server.kplay.balance.repository",
                "GInternational.server.kplay.bonus.repository",
                "GInternational.server.kplay.credit.repository",
                "GInternational.server.kplay.debit.repository",
                "GInternational.server.kplay.game.repository",
                "GInternational.server.kplay.product.repository",
                "GInternational.server.l_sport.batch.model.repository"
        })
public class ClientDBConfig {


    @Value("${spring.primary.datasource.jdbc-url}")
    private String url;
    @Value("${spring.primary.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.primary.datasource.username}")
    private String username;
    @Value("${spring.primary.datasource.password}")
    private String password;



    @Bean(name = "clientApiServer")
    @Primary
    public DataSource clientServerDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }


    @Bean(name = "jdbcTemplate")
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(clientServerDataSource());
    }


    @Bean(name = "entityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManager() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(clientServerDataSource());

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        em.setJpaPropertyMap(properties);
        em.setPackagesToScan("GInternational.server.api.entity",
                "GInternational.server.kplay.debit.entity",
                "GInternational.server.kplay.credit.entity",
                "GInternational.server.kplay.game.entity",
                "GInternational.server.kplay.product.entity",
                "GInternational.server.l_sport.batch.model");  //멀티 스케줄링 테스트 도메인 경로
        return em;
    }


    @Bean(name = "clientServerTransactionManager")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManager().getObject());
        return transactionManager;
    }
}
