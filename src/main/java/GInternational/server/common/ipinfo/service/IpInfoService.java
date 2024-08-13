package GInternational.server.common.ipinfo.service;

import GInternational.server.common.ipinfo.config.IpInfoConfig;
import io.ipinfo.api.IPinfo;
import io.ipinfo.api.context.Context;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;
import net.sf.uadetector.UserAgent;
import net.sf.uadetector.UserAgentStringParser;
import net.sf.uadetector.service.UADetectorServiceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Service
public class IpInfoService {

    private final String IPINFO_API_URL = "https://ipinfo.io/{ip}/json";
    private final RestTemplate restTemplate;
    private final String apiKey;

    /**
     * RestTemplate과 API 키를 주입받아 IpInfoService 인스턴스를 초기화.
     *
     * @param restTemplate Spring의 RestTemplate 객체
     * @param ipInfoConfig API 키를 포함하고 있는 RestTemplateConfig 객체
     */
    @Autowired
    public IpInfoService(RestTemplate restTemplate, IpInfoConfig ipInfoConfig) {
        this.restTemplate = restTemplate;
        this.apiKey = ipInfoConfig.getApiKey();
    }

    /**
     * HTTP 요청으로부터 클라이언트의 IP 주소를 추출.
     * 여러 HTTP 헤더를 체크하여 IP 주소를 찾고, 찾지 못하는 경우 요청 객체로부터 IP 주소를 반환.
     *
     * @param request 클라이언트의 HTTP 요청
     * @return 클라이언트의 IP 주소
     */
    public String getClientIp(HttpServletRequest request) {
        String[] headersToCheck = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "X-Real-IP"
        };

        for (String header : headersToCheck) {
            String ip = request.getHeader(header);
            if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }

    /**
     * 주어진 IP 주소에 대한 정보를 조회하고 IPResponse를 반환.
     *
     * @param ip      조회할 IP 주소
     * @return IPResponse 객체
     */
    public IPResponse getIpInfo(String ip) {
        // IPInfo API 호출 URL 생성
        String apiUrl = IPINFO_API_URL.replace("{ip}", ip);

        // IPInfo 클라이언트 초기화
        IPinfo ipInfo = new IPinfo.Builder()
                .setToken(apiKey)
                .build();

        try {
            // IP 정보 조회
            IPResponse response = ipInfo.lookupIP(ip);

            response.setContext(new Context(new HashMap<>()));

            // IPResponse 객체에서 국가 정보 설정
            String countryCode = response.getCountryCode();

            if (countryCode == null) {
                countryCode = "Unknown";
            }

            // IPResponse 객체를 국가 정보만 설정하여 생성
            return new IPResponse(
                    ip,
                    null,  // 호스트명
                    false, // Anycast 여부
                    null,  // 도시
                    null,  // 지역
                    countryCode, // 국가 이름
                    null,  // 위치 정보
                    null,  // 조직 정보
                    null,  // 우편번호
                    null,  // 시간대
                    null,  // 자체 네트워크 시스템 (ASN)
                    null,  // 회사 정보
                    null,  // 통신사 정보
                    null,  // 개인정보 관련 정보
                    null,  // 남용에 관련된 정보
                    null   // 도메인 정보
            );
        } catch (RateLimitedException ex) {
            // API 호출 제한 예외 처리
            throw new RuntimeException("API 호출 제한 초과");
        }
    }

    /**
     * User-Agent에서 단말기 타입을 추출.
     *
     * @param userAgentString User-Agent 문자열
     * @return 단말기 타입
     */
    public String extractDeviceTypeFromUserAgent(String userAgentString) {
        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        UserAgent userAgent = (UserAgent) parser.parse(userAgentString);

        String simplifiedDeviceType = getSimplifiedDeviceType(userAgent.getDeviceCategory().getName());
        return simplifiedDeviceType;
    }

    /**
     * 관리자의 User-Agent 문자열로부터 디바이스 타입을 추출.
     * User-Agent 정보를 파싱하여 디바이스 카테고리 이름을 반환.
     * 정보가 없는 경우 "Unknown"을 반환.
     *
     * @param userAgentString User-Agent 문자열
     * @return 디바이스 타입 또는 "Unknown"
     */
    public String extractDeviceTypeFromUserAgentForAdmin(String userAgentString) {
        UserAgentStringParser parser = UADetectorServiceFactory.getResourceModuleParser();
        UserAgent userAgent = (UserAgent) parser.parse(userAgentString);

        String deviceType = userAgent.getDeviceCategory().getName();

        return deviceType != null ? deviceType : "Unknown";
    }

    /**
     * 원본 단말기 타입을 간소화된 형태로 변환.
     *
     * @param originalDeviceType 원본 단말기 타입
     * @return 간소화된 단말기 타입
     */
    private String getSimplifiedDeviceType(String originalDeviceType) {
        if ("Personal computer".equalsIgnoreCase(originalDeviceType)) {
            return "P";
        } else if ("Smartphone".equalsIgnoreCase(originalDeviceType)) {
            return "M";
        } else {
            return "Unknown";
        }
    }
}


