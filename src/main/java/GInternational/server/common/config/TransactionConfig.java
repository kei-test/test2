package GInternational.server.common.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    @Autowired
    @Qualifier("clientServerTransactionManager")
    private PlatformTransactionManager clientServerTransactionManager;

    @Autowired
    @Qualifier("lsportTransactionManager")
    private PlatformTransactionManager lsportTransactionManager;



    //ChainedTransactionManager deprecated
    @Bean(name = "multiTransactionManager")
    public PlatformTransactionManager transactionManager() {
        return new ChainedTransactionManager(clientServerTransactionManager,lsportTransactionManager);
    }
}
