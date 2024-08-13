package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.MoneyLogResponseDTO;
import GInternational.server.api.entity.MoneyLog;
import GInternational.server.api.mapper.MoneyLogResponseMapper;
import GInternational.server.api.repository.MoneyLogRepository;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class MoneyLogService {

    private final MoneyLogRepository moneyLogRepository;
    private final MoneyLogResponseMapper moneyLogResponseMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * 모든 머니 적립 내역을 조회하고, 필요한 경우 카테고리별로 정렬.
     *
     * @param principalDetails 현재 사용자의 인증 정보
     * @param userId           사용자 ID (옵션)
     * @param category         카테고리 (옵션)
     * @param startDate        시작 날짜 (옵션)
     * @param endDate          종료 날짜 (옵션)
     * @param username         사용자 이름 (옵션)
     * @param nickname         닉네임 (옵션)
     * @param distributor      배급사 (옵션)
     * @param store            매장 (옵션)
     * @return 조회된 머니 적립 내역 목록
     */
    public List<MoneyLogResponseDTO> getAllMoneyTrackingTransactions(PrincipalDetails principalDetails, Long userId, MoneyLogCategoryEnum category,
                                                                     Long usedSportsBalance, LocalDate startDate, LocalDate endDate, String username,
                                                                     String nickname, String distributor, String store) {
        return moneyLogRepository.findAll((root, query, cb) -> {
                    List<Predicate> predicates = new ArrayList<>();

                    if (userId != null) {
                        predicates.add(cb.equal(root.get("user").get("id"), userId));
                    }

                    if (category != null) {
                        predicates.add(cb.equal(root.get("category"), category));
                    }

                    if (usedSportsBalance != null) {
                        predicates.add(cb.equal(root.get("usedSportsBalance"), usedSportsBalance));
                    }

                    if (startDate != null) {
                        LocalDateTime startDateTime = startDate.atStartOfDay();
                        if (endDate != null) {
                            LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                            predicates.add(cb.between(root.get("createdAt"), startDateTime, endDateTime));
                        } else {
                            predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startDateTime));
                        }
                    } else if (endDate != null) {
                        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                        predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endDateTime));
                    }

                    if (username != null && !username.isEmpty()) {
                        predicates.add(cb.equal(root.get("username"), username));
                    }

                    if (nickname != null && !nickname.isEmpty()) {
                        predicates.add(cb.equal(root.get("nickname"), nickname));
                    }

                    if (distributor != null && !distributor.isEmpty()) {
                        predicates.add(cb.equal(root.get("user").get("distributor"), distributor));
                    }

                    if (store != null && !store.isEmpty()) {
                        predicates.add(cb.equal(root.get("user").get("store"), store));
                    }

                    return cb.and(predicates.toArray(new Predicate[0]));
                }, Sort.by(Sort.Direction.DESC, "createdAt")).stream()
                .map(moneyLogResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 머니 사용 기록을 추가합니다.
     *
     * @param userId             사용자 ID.
     * @param usedSportsBalance  사용된 금액.
     * @param finalSportsBalance 최종 금액.
     * @param category           카테고리.
     * @param bigo               비고.
     */
    public void recordMoneyUsage(Long userId, Long usedSportsBalance, Long finalSportsBalance, MoneyLogCategoryEnum category, String bigo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setUser(user);
        moneyLog.setUsername(user.getUsername());
        moneyLog.setNickname(user.getNickname());
        moneyLog.setUsedSportsBalance(usedSportsBalance);
        moneyLog.setFinalSportsBalance(finalSportsBalance);
        moneyLog.setCategory(category);
        moneyLog.setBigo(bigo);
        moneyLog.setSite("test");
        moneyLogRepository.save(moneyLog);
    }
}