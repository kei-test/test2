package GInternational.server.api.service;

import GInternational.server.api.dto.LoginRequestDto;
import GInternational.server.security.auth.PrincipalDetails;

import GInternational.server.api.dto.LoginHistoryDTO;
import GInternational.server.api.entity.LoginHistory;
import GInternational.server.common.ipinfo.service.IpInfoService;
import GInternational.server.api.mapper.LoginHistoryMapper;
import GInternational.server.api.repository.LoginHistoryRepository;
import io.ipinfo.api.model.IPResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;
    private final LoginHistoryMapper loginHistoryMapper;
    private final IpInfoService ipInfoService;
    private final UserService userService;

    /**
     * 로그인 시도 기록.
     *
     * @param loginRequestDto 로그인 요청 데이터
     * @param attemptIP 접속 시도 IP
     * @param ipResponse IP 응답 데이터
     * @param attemptNickname 닉네임
     * @param request HTTP 요청
     * @param countryName 국가명
     */
    public void saveLoginHistory(LoginRequestDto loginRequestDto, String attemptIP, IPResponse ipResponse,
                                 String attemptNickname, HttpServletRequest request, String countryName) {
        LoginHistory loginHistory = new LoginHistory();
        loginHistory.setAttemptUsername(loginRequestDto.getUsername());

        // 유저의 닉네임 정보가 있을 때만 저장
        if (attemptNickname != null) {
            loginHistory.setAttemptNickname(attemptNickname);
        }

        loginHistory.setAttemptPassword(loginRequestDto.getPassword());
        loginHistory.setAttemptIP(attemptIP);

        String countryCode = getCountryCode(ipResponse);
        loginHistory.setAttemptNation(countryCode);

        // User-Agent에서 단말기 타입 추출
        String userAgent = request.getHeader("User-Agent");
        String attemptDevice = ipInfoService.extractDeviceTypeFromUserAgent(userAgent);
        loginHistory.setAttemptDevice(attemptDevice);

        String attemptUrl = extractFullRequestURL(request);
        loginHistory.setAttemptUrl(attemptUrl);

        loginHistoryRepository.save(loginHistory);
    }

    /**
     * IP 응답 객체에서 국가 코드를 추출.
     * IP 응답 객체가 null이 아니며 국가 코드 필드에 값이 설정되어 있다면 해당 국가 코드를 반환.
     * 설정되어 있지 않은 경우 "Unknown"을 반환.
     *
     * @param ipResponse IP 응답 객체
     * @return String 국가 코드 또는 "Unknown"
     */
    public static String getCountryCode(IPResponse ipResponse) {
        // IPResponse 객체가 null이 아니고, country 코드 필드에 값이 설정되어 있는지 확인
        if (ipResponse != null && ipResponse.getCountryCode() != null) {
            return ipResponse.getCountryCode(); // country 코드를 반환
        }
        return "Unknown"; // 국가 코드가 없는 경우 "Unknown"을 반환
    }

    /**
     * 특정 날짜 범위의 로그인 이력 조회.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param attemptUsername 시도한 사용자 이름
     * @param attemptNickname 시도한 사용자 닉네임
     * @param attemptIP 시도한 IP
     * @param principalDetails 인증된 사용자 정보
     * @return 해당 날짜 범위의 로그인 이력 목록
     */
    public List<LoginHistoryDTO> getLoginHistoryByDateRange(LocalDate startDate, LocalDate endDate,
                                                            String attemptUsername, String attemptNickname, String attemptIP,
                                                            PrincipalDetails principalDetails) {
        LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

        List<LoginHistory> loginHistories = loginHistoryRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (startDateTime != null && endDateTime != null) {
                predicates.add(criteriaBuilder.between(root.get("attemptDate"), startDateTime, endDateTime));
            }

            if (attemptUsername != null) {
                predicates.add(criteriaBuilder.equal(root.get("attemptUsername"), attemptUsername));
            }

            if (attemptNickname != null) {
                predicates.add(criteriaBuilder.equal(root.get("attemptNickname"), attemptNickname));
            }

            if (attemptIP != null) {
                predicates.add(criteriaBuilder.equal(root.get("attemptIP"), attemptIP));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        return loginHistories.stream()
                .map(loginHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 접속 시도 URL 추출.
     *
     * @param request HTTP 요청
     * @return 접속 시도 URL
     */
    private String extractFullRequestURL(HttpServletRequest request) {
        String requestURL = request.getRequestURL().toString();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL;
        } else {
            return requestURL + "?" + queryString;
        }
    }
}