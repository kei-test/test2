package GInternational.server.api.service;

import GInternational.server.api.entity.LoginSuccessHistory;
import GInternational.server.api.repository.LoginSuccessHistoryRepository;
import GInternational.server.common.ipinfo.service.IpInfoService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LoginSuccessHistoryService {

    private final IpInfoService ipInfoService;
    private final LoginSuccessHistoryRepository loginSuccessHistoryRepository;

    public void saveLoginHistory(Long userId, String loginIp, HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String attemptDevice = ipInfoService.extractDeviceTypeFromUserAgent(userAgent);
        String attemptUrl = extractFullRequestURL(request);

        LoginSuccessHistory loginSuccessHistory = new LoginSuccessHistory();
        loginSuccessHistory.setUserId(userId);
        loginSuccessHistory.setLoginIp(loginIp);
        loginSuccessHistory.setLoginDevice(attemptDevice);
        loginSuccessHistory.setLoginDate(LocalDateTime.now());
        loginSuccessHistory.setLoginUrl(attemptUrl);

        loginSuccessHistoryRepository.save(loginSuccessHistory);
    }

    public List<LoginSuccessHistory> findLoginHistories(Long userId, String loginIp, LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        Specification<LoginSuccessHistory> spec = Specification.where(null);

        if (userId != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("userId"), userId));
        }

        if (loginIp != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("loginIp"), loginIp));
        }

        if (startDate != null && endDate != null) {
            LocalDateTime startOfDay = startDate.atStartOfDay();
            LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("loginDate"), startOfDay, endOfDay));
        } else if (startDate != null) {
            LocalDateTime startOfDay = startDate.atStartOfDay();
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get("loginDate"), startOfDay));
        } else if (endDate != null) {
            LocalDateTime endOfDay = endDate.atTime(23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get("loginDate"), endOfDay));
        }

        return loginSuccessHistoryRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "loginDate"));
    }

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
