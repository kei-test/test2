package GInternational.server.security.config;

import GInternational.server.api.repository.IpRepository;
import GInternational.server.api.repository.WhiteIpRepository;
import GInternational.server.api.service.*;
import GInternational.server.security.jwt.AuthorizationFilter;
import GInternational.server.security.jwt.JwtAuthenticationFilter;
import GInternational.server.common.ipinfo.service.IpInfoService;
import GInternational.server.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IpRepository ipRepository;
    @Autowired
    private CorsConfig corsConfig;
    @Autowired
    private IpInfoService ipInfoService;
    @Autowired
    private LoginHistoryService loginHistoryService;
    @Autowired
    private LoginStatisticService loginStatisticService;
    @Autowired
    private AdminLoginHistoryService adminLoginHistoryService;
    @Autowired
    private AmazonLoginHistoryService amazonLoginHistoryService;

    @Autowired
    private WhiteIpRepository whiteIpRepository;

    @Autowired
    private ExpRecordService expRecordService;

    @Autowired
    private LoginInfoService loginInfoService;

    @Autowired
    private LoginSuccessHistoryService loginSuccessHistoryService;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable()
                .httpBasic().disable()
                .apply(new MyCustomDsl())
                .and()
                .authorizeRequests()
                .antMatchers("/api/v2/admins/**").access("hasRole('ROLE_ADMIN')")
                .antMatchers("/api/v2/managers/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
                .antMatchers("/api/v2/users/**").access("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_TEST')")
                .anyRequest()
                .permitAll()
                .and()
                .build();
    }

    public class MyCustomDsl extends AbstractHttpConfigurer<MyCustomDsl, HttpSecurity> {
        @Override
        public void configure(HttpSecurity http) throws Exception {
            AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
            http
                    .addFilter(corsConfig.corsFilter())
                    .addFilter(new JwtAuthenticationFilter(authenticationManager, userRepository, ipRepository, ipInfoService, loginHistoryService, loginStatisticService,
                                                           adminLoginHistoryService, amazonLoginHistoryService, whiteIpRepository, expRecordService, loginInfoService,
                                                           loginSuccessHistoryService))
                    .addFilter(new AuthorizationFilter(authenticationManager, userRepository));
        }
    }
}