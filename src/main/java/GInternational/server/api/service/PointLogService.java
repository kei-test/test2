package GInternational.server.api.service;

import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.PointLogResponseDTO;
import GInternational.server.api.entity.PointLog;
import GInternational.server.api.mapper.PointLogResponseMapper;
import GInternational.server.api.repository.PointLogRepository;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class PointLogService {

    private final PointLogRepository pointLogRepository;
    private final PointLogResponseMapper pointLogResponseMapper;
    private final UserRepository userRepository;

    /**
     * 모든 포인트 적립 내역을 조회하고, 필요한 경우 필터링.
     *
     * @param principalDetails 현재 사용자의 인증 정보
     * @param userId           유저 ID
     * @param category         포인트 적립 카테고리
     * @param startDate        조회 시작일
     * @param endDate          조회 종료일
     * @param distributor      총판명
     * @param store            매장명
     * @param nickname         닉네임
     * @param username         아이디
     * @param site             사이트명
     * @param memo             비고
     * @return 필터링된 포인트 적립 내역 목록
     */
    public List<PointLogResponseDTO> getAllPointTrackingTransactions(PrincipalDetails principalDetails, Long userId, PointLogCategoryEnum category,
                                                                     LocalDate startDate, LocalDate endDate, String distributor, String store, String nickname,
                                                                     String username, String site, String memo) {
        Specification<PointLog> spec = Specification.where(null);

        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt"), startDateTime, endDateTime));
        }

        if (userId != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<PointLog, User> userJoin = root.join("userId", JoinType.LEFT);
                return criteriaBuilder.equal(userJoin.get("id"), userId);
            });
        }

        if (category != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("category"), category));
        }

        if (distributor != null && !distributor.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<PointLog, User> userJoin = root.join("userId", JoinType.LEFT);
                return criteriaBuilder.equal(userJoin.get("distributor"), distributor);
            });
        }

        if (store != null && !store.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<PointLog, User> userJoin = root.join("userId", JoinType.LEFT);
                return criteriaBuilder.equal(userJoin.get("store"), store);
            });
        }

        if (nickname != null && !nickname.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<PointLog, User> userJoin = root.join("userId", JoinType.LEFT);
                return criteriaBuilder.equal(userJoin.get("nickname"), nickname);
            });
        }

        if (username != null && !username.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<PointLog, User> userJoin = root.join("userId", JoinType.LEFT);
                return criteriaBuilder.equal(userJoin.get("username"), username);
            });
        }

        if (site != null && !site.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<PointLog, User> userJoin = root.join("userId", JoinType.LEFT);
                return criteriaBuilder.equal(userJoin.get("site"), site);
            });
        }

        if (memo != null && !memo.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("memo"), "%" + memo + "%"));
        }

        List<PointLog> transactions = pointLogRepository.findAll(spec);

        return transactions.stream()
                .map(pointLogResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 포인트 적립 또는 사용 내역을 PointLog에 기록.
     *
     * @param userId     사용자 ID
     * @param points     적립 또는 사용된 포인트 수
     * @param category   포인트 내역의 카테고리
     * @param userIp     사용자 IP
     */
    public void recordPointLog(Long userId, Long points, PointLogCategoryEnum category, String userIp, String memo) {
        User user = userRepository.findById(userId).orElseThrow
                (() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        PointLog pointLog = new PointLog();
        pointLog.setUserId(user);
        pointLog.setUsername(user.getUsername());
        pointLog.setNickname(user.getNickname());
        pointLog.setPoint((long) points.intValue());
        pointLog.setFinalPoint(user.getWallet().getPoint());
        pointLog.setCreatedAt(LocalDateTime.now());
        pointLog.setCategory(category.getValue());
        pointLog.setIp(userIp);
        pointLog.setMemo(memo);

        pointLogRepository.save(pointLog);
    }
}