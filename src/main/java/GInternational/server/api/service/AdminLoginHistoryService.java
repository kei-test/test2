package GInternational.server.api.service;

import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.dto.AdminLoginResultDTO;
import GInternational.server.api.dto.AdminLoginSuccessRecordDTO;
import GInternational.server.api.entity.AdminLoginHistory;
import GInternational.server.api.mapper.AdminLoginResultMapper;
import GInternational.server.api.repository.AdminLoginHistoryRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.vo.AmazonUserStatusEnum;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AdminLoginHistoryService {

    private final AdminLoginHistoryRepository adminLoginHistoryRepository;
    private final AdminLoginResultMapper adminLoginResultMapper;
    private final UserRepository userRepository;

    /**
     * 관리자의 로그인 시도를 기록.
     *
     * @param username 로그인 시도한 관리자의 사용자명
     * @param isSuccess 로그인 성공 여부
     * @param attemptIp 로그인 시도한 IP 주소
     * @param nickname 관리자의 닉네임
     * @param countryCode 국가 코드
     * @param deviceType 사용한 디바이스 타입
     * @return AdminLoginResultDTO 로그인 시도 결과를 담은 DTO
     */
    public AdminLoginResultDTO recordLoginAttempt(String username, boolean isSuccess, String attemptIp,
                                                  String nickname, String countryCode, String deviceType) {
        String loginResult = isSuccess ? "성공" : "실패";

        AdminLoginHistory loginAttempt = new AdminLoginHistory();
        loginAttempt.setUsername(username); // 로그인 시도한 관리자 아이디
        loginAttempt.setNickname(nickname); // 닉네임
        loginAttempt.setAttemptIp(attemptIp); // 시도한 IP 주소
        loginAttempt.setCountryCode(countryCode); // 국가 코드
        loginAttempt.setDeviceType(deviceType); // 디바이스 타입
        loginAttempt.setAttemptDate(LocalDateTime.now()); // 현재 시간을 로그인 시도 시간으로 설정
        loginAttempt.setLoginResult(loginResult); // 로그인 결과

        if (isSuccess) {
            User user = userRepository.findByUsername(username);
            if (user != null) {
                user.setVisitCount(user.getVisitCount() + 1);
                userRepository.save(user);
                loginAttempt.setVisitCount(user.getVisitCount());
                loginAttempt.setCreatedAt(user.getCreatedAt());
            }
        }

        // 로그인 시도 정보를 데이터베이스에 저장합니다.
        AdminLoginHistory savedLoginAttempt = adminLoginHistoryRepository.save(loginAttempt);

        // 저장된 정보를 DTO로 변환하여 반환합니다.
        return adminLoginResultMapper.toDto(savedLoginAttempt);
    }

    /**
     * 로그인 성공 이력만 조회하여 반환.
     *
     * @param principalDetails 인증된 관리자의 세부 정보
     * @return List<AdminLoginSuccessRecordDTO> 로그인 성공 이력 목록
     */
    public List<AdminLoginSuccessRecordDTO> getLoginSuccessRecords(String username, String attemptIp, String countryCode, PrincipalDetails principalDetails) {
        Specification<AdminLoginHistory> specification = createSpecification(username, attemptIp, countryCode);
        List<AdminLoginHistory> loginSuccesses = adminLoginHistoryRepository.findAll(specification);

        return loginSuccesses.stream()
                .sorted(Comparator.comparing(
                        AdminLoginHistory::getAttemptDate,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ))
                .map(history -> new AdminLoginSuccessRecordDTO(
                        history.getId(),
                        history.getUsername(),
                        history.getNickname(),
                        history.getAttemptIp(),
                        history.getCountryCode(),
                        history.getDeviceType(),
                        history.getAttemptDate(),
                        history.getSite()))
                .collect(Collectors.toList());
    }

    private Specification<AdminLoginHistory> createSpecification(String username, String attemptIp, String countryCode) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (username != null && !username.isEmpty()) {
                predicates.add(cb.equal(root.get("username"), username));
            }
            if (attemptIp != null && !attemptIp.isEmpty()) {
                predicates.add(cb.equal(root.get("attemptIp"), attemptIp));
            }
            if (countryCode != null && !countryCode.isEmpty()) {
                predicates.add(cb.equal(root.get("countryCode"), countryCode));
            }

            query.orderBy(cb.desc(root.get("attemptDate")));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * 모든 관리자의 로그인 이력을 조회하여 반환.
     *
     * @param principalDetails 인증된 관리자의 세부 정보
     * @return List<AdminLoginResultDTO> 관리자 로그인 이력 목록
     */
    public List<AdminLoginResultDTO> getAllLoginHistories(PrincipalDetails principalDetails) {
        List<AdminLoginHistory> histories = adminLoginHistoryRepository.findAllByOrderByAttemptDateDesc();
        return histories.stream()
                .map(adminLoginResultMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 관리자의 로그인 카운트를 0으로 초기화.
     *
     * @param userId 초기화할 관리자의 사용자 ID
     * @param principalDetails 인증된 관리자의 세부 정보
     */
    @AuditLogService.Audit("관리자 로그인 카운트 초기화")
    public void resetVisitCount(Long userId, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElse(null);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "의 로그인 카운트" + user.getVisitCount() +"에서 0으로 초기화");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        if (user != null) {
            user.setVisitCount(0);
            userRepository.save(user);
        }
    }

    /**
     * 특정 사용자의 AmazonUserStatusEnum 값을 변경.
     *
     * @param userId 상태를 변경할 사용자의 ID
     * @param newStatus 새로운 상태값
     * @param principalDetails 인증된 관리자의 세부 정보
     */
    @AuditLogService.Audit("총판 유저 상태값 변경")
    public void updateAmazonUserStatus(Long userId, AmazonUserStatusEnum newStatus, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElse(null);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "의 상태값을" + user.getAmazonUserStatus() + "에서 " + newStatus + "로 변경");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        if (user != null) {
            user.setAmazonUserStatus(newStatus);
            userRepository.save(user);
        }
    }

    /**
     * 특정 관리자의 비밀번호 변경.
     *
     * @param userId 비밀번호를 변경할 관리자의 사용자 ID
     * @param newPassword 새로운 비밀번호
     * @param principalDetails 인증된 관리자의 세부 정보
     */
    @AuditLogService.Audit("총판 유저 비밀번호 변경")
    public void updateAmazonUserPassword(Long userId, String newPassword, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow(null);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "의 비밀번호 변경");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        if (user != null) {
            user.setPassword(newPassword);
            userRepository.save(user);
        }
    }

    /**
     * 관리자의 접속 가능 IP 변경
     *
     * @param userId 관리자의 사용자 ID
     * @param newApproveIp 새로운 접속 가능 IP 주소
     * @param principalDetails 인증된 관리자의 세부 정보
     */
    @AuditLogService.Audit("관리자 접속가능 ip 변경")
    public void updateApproveIp(Long userId, String newApproveIp, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));

        String clientIp = request.getRemoteAddr();

        user.setApproveIp(newApproveIp);
        user.setUpdatedAtApproveIp(LocalDateTime.now());

        AuditContext context = AuditContextHolder.getContext();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "의 접속가능 ip 변경");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        userRepository.save(user);
    }

    /**
     * ROLE_ADMIN 또는 ROLE_MANAGER 역할을 가진 사용자들 중에서 접속 가능한 IP가 설정된 사용자들의 목록을 조회.
     * 이 메서드는 각 사용자의 접속 가능 IP, 사용자 이름(username), 그리고 IP가 업데이트된 날짜(updatedAtApproveIp)를 반환.
     *
     * @param principalDetails 현재 인증된 사용자의 세부 정보를 포함하는 PrincipalDetails 객체.
     * @return 사용자의 접속 가능 IP, 사용자 이름, IP 업데이트 날짜를 포함하는 맵의 리스트를 반환.
     */
    public List<Map<String, Object>> getAllApproveIp(PrincipalDetails principalDetails) {
        List<User> users = userRepository.findAllByRoleInAndApproveIpIsNotNull(Arrays.asList("ROLE_ADMIN", "ROLE_MANAGER"));

        List<Map<String, Object>> result = new ArrayList<>();

        for (User user : users) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("접속가능 IP", user.getApproveIp());
            userInfo.put("사용자", user.getUsername());
            userInfo.put("등록일", user.getCreatedAt());
            userInfo.put("수정일", user.getUpdatedAtApproveIp());
            userInfo.put("userId", user.getId());
            userInfo.put("site", user.getSite());

            result.add(userInfo);
        }

        return result;
    }

    /**
     * 지정된 관리자 사용자의 승인된 접속 가능 IP 주소를 삭제.
     *
     * @param userId 관리자의 사용자 ID. 이 ID에 해당하는 사용자의 승인된 접속 가능 IP를 삭제.
     * @param principalDetails 현재 인증된 사용자의 세부 정보를 포함하는 PrincipalDetails 객체.
     * @param request 현재 HTTP 요청 정보를 포함하는 HttpServletRequest 객체. 이를 통해 클라이언트의 IP 주소를 가져옴.
     * @throws RestControllerException 사용자를 찾을 수 없을 때 발생하는 예외. {@code USER_NOT_FOUND} 예외 코드와 함께 처리.
     */
    @AuditLogService.Audit("관리자 접속가능 ip 삭제")
    public void deleteApproveIp(Long userId, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));

        String clientIp = request.getRemoteAddr();

        user.setApproveIp(null);
        user.setUpdatedAtApproveIp(LocalDateTime.now());

        AuditContext context = AuditContextHolder.getContext();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "의 접속가능 ip 삭제");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        userRepository.save(user);
    }
}
