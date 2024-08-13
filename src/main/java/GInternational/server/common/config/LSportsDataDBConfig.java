package GInternational.server.common.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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
@EnableJpaRepositories(entityManagerFactoryRef = "lsportEntityManager",
        transactionManagerRef = "lsportTransactionManager",
        basePackages = {"GInternational.server.l_sport.info.repository"})
// 하위 spring data jpa를 상속하는 repository scan -> entityManager 와 transactionManager 를 분리시킬 수 있음
// 도메인 패키지 구조 보다는 레이어 구조를 써야 일괄 스캔할 수 있음
public class LSportsDataDBConfig {

    @Value("${spring.secondary.datasource.jdbc-url}")
    private String url;
    @Value("${spring.secondary.datasource.driver-class-name}")
    private String driverClassName;
    @Value("${spring.secondary.datasource.username}")
    private String username;
    @Value("${spring.secondary.datasource.password}")
    private String password;


    @Bean(name = "lsportDatasource")
    @Primary
    public DataSource lsportDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }


    @Bean(name = "lsportJdbcTemplate")
    @Primary
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(lsportDatasource());
    }


    @Bean(name = "lsportEntityManager")
    @Primary
    public LocalContainerEntityManagerFactoryBean lsportEntityManagerFactory() {

        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(lsportDatasource());

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "create");
        properties.put("hibernate.show.sql","true");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
        em.setJpaPropertyMap(properties);
        em.setPackagesToScan("GInternational.server.l_sport.info.entity");
        return em;
    }


    @Bean(name = "lsportTransactionManager")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(lsportEntityManagerFactory().getObject());
        return transactionManager;
    }
}
