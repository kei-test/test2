package GInternational.server.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * BCrypt 암호화를 위한 설정 클래스.
 */
@Configuration
public class BCryptConfig {

    /**
     * BCryptPasswordEncoder 빈 등록.
     * @return BCryptPasswordEncoder 인스턴스
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}