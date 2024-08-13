package GInternational.server.common.ipinfo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class IpInfoConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Value("3794a8a0a049fd")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}
