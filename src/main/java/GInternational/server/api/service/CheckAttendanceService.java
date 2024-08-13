package GInternational.server.api.service;

import GInternational.server.api.dto.NewCheckAttendanceDTO;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.api.dto.CheckAttendanceRequestDTO;
import GInternational.server.api.dto.CheckAttendanceResponseDTO;
import GInternational.server.api.dto.CheckAttendanceMonthlyDTO;
import GInternational.server.api.mapper.CheckAttendanceRequestMapper;
import GInternational.server.api.mapper.CheckAttendanceResponseMapper;
import GInternational.server.api.repository.CheckAttendanceRepository;
import GInternational.server.api.entity.CheckAttendance;

import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class CheckAttendanceService {

    private final CheckAttendanceRepository checkAttendanceRepository;
    private final CheckAttendanceRequestMapper checkAttendanceRequestMapper;
    private final CheckAttendanceResponseMapper checkAttendanceResponseMapper;
    private final UserRepository userRepository;

    // 자동 출석체크를 위한 최소 충전 금액
    private static final BigDecimal MIN_CHARGED_AMOUNT_FOR_ATTENDANCE = BigDecimal.valueOf(10000);
    // 룰렛 보상을 받기 위한 한 달 최소 출석 일수
    private static final int MIN_DAYS_FOR_ROULETTE_REWARD = 25;

    /**
     * 사용자 출석체크 처리. 자동 출석체크 조건을 충족하면 출석체크 진행.
     * @param userId 사용자 ID
     * @param rechargeAmount 충전 금액
     */
    public void chargeAndCheckAttendance(Long userId, BigDecimal rechargeAmount) {
        if (rechargeAmount.compareTo(MIN_CHARGED_AMOUNT_FOR_ATTENDANCE) >= 0) {
            checkAttendance(userId, rechargeAmount);
        }
    }

    /**
     * 사용자의 출석체크 상태를 확인하고 출석체크를 진행
     *
     * @param userId 사용자 ID
     * @param rechargeAmount 충전 금액
     */
    public void checkAttendance(Long userId, BigDecimal rechargeAmount) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ExceptionCode.USER_NOT_FOUND.getMessage()));

        LocalDateTime now = LocalDateTime.now();
        Optional<CheckAttendance> existingAttendance = checkAttendanceRepository.findByUserAndAttendanceDate(user, now);

        if (existingAttendance.isPresent()) {
            return;
        }

        CheckAttendanceRequestDTO requestDTO = new CheckAttendanceRequestDTO();
        requestDTO.setUserId(userId);
        requestDTO.setAttendanceDate(now);
        requestDTO.setIsChecked(true);
        CheckAttendance newAttendance = checkAttendanceRequestMapper.toEntity(requestDTO);

        newAttendance.setUser(user);

        checkAttendanceRepository.save(newAttendance);
    }

    /**
     * 사용자의 이번 달 출석 일수를 반환
     *
     * @param userId 사용자 ID
     * @return 이번 달 출석 일수
     */
    public int getCurrentMonthAttendanceCount(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(ExceptionCode.USER_NOT_FOUND.getMessage()));

        LocalDateTime firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastDayOfCurrentMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);

        return (int) checkAttendanceRepository.countByUserAndAttendanceDateBetween(user, firstDayOfCurrentMonth, lastDayOfCurrentMonth);
    }

    /**
     * 사용자의 이번 달 출석 날짜 목록을 반환
     *
     * @param userId 사용자 ID
     * @return 이번 달 출석한 날짜 목록
     */
    public List<CheckAttendanceResponseDTO> getCurrentMonthAttendanceDates(Long userId, PrincipalDetails principalDetails) {

        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException(ExceptionCode.USER_NOT_FOUND.getMessage()));
        LocalDateTime firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastDayOfCurrentMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59);

        List<CheckAttendance> attendances = checkAttendanceRepository.findByUserAndAttendanceDateBetween(user, firstDayOfCurrentMonth, lastDayOfCurrentMonth);

        return attendances.stream().map(checkAttendanceResponseMapper::toDto).collect(Collectors.toList());
    }

    /**
     * 사용자의 현재 월에 대한 출석 정보를 가져옵니다.
     *
     * @param userId 사용자의 아이디
     * @return MonthlyAttendanceDTO 현재 월의 출석 횟수 및 출석한 날짜들을 포함한 DTO
     */
    public CheckAttendanceMonthlyDTO getCurrentMonthAttendanceInfo(Long userId, PrincipalDetails principalDetails) {
        int count = getCurrentMonthAttendanceCount(userId, principalDetails);
        List<LocalDateTime> dates = getCurrentMonthAttendanceDates(userId, principalDetails)
                .stream()
                .map(CheckAttendanceResponseDTO::getAttendanceDate)
                .collect(Collectors.toList());

        return new CheckAttendanceMonthlyDTO(count, dates);
    }

    /**
     * 출석 기록 전체 조회.
     * @param username   유저 이름
     * @param nickname   유저 닉네임
     * @param startDate  시작일
     * @param endDate    종료일
     * @return 출석 기록 리스트
     */
    public List<NewCheckAttendanceDTO> getAllAttendance(String username, String nickname, LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        Specification<CheckAttendance> spec = Specification.where((root, query, criteriaBuilder) -> criteriaBuilder.conjunction());

        if (username != null && !username.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("username"), username));
        }

        if (nickname != null && !nickname.isEmpty()) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user").get("nickname"), nickname));
        }

        if (startDate != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("attendanceDate"), startDate));
        }

        if (endDate != null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("attendanceDate"), endDate));
        }

        List<CheckAttendance> attendanceList = checkAttendanceRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "attendanceDate"));
        return attendanceList.stream()
                .map(a -> new NewCheckAttendanceDTO(a.getUser().getId(), a.getUser().getUsername(), a.getUser().getNickname(), a.getUser().getLv(), a.getAttendanceDate()))
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void handleMonthlyAttendanceResetAndReward() {
        resetAttendanceRouletteCountIfNotUsed();
        monthlyAttendanceReward();
    }

    /**
     * 매달 1일 룰렛을 돌리지 않았다면 초기화
     */
    public void resetAttendanceRouletteCountIfNotUsed() {
        List<User> allUsers = userRepository.findAll();

        for (User user : allUsers) {
            if (user.getAttendanceRouletteCount() > 0) {
                user.setAttendanceRouletteCount(0);
                userRepository.save(user);
            }
        }
    }

    /**
     * 매달 1일 사용자의 출석 횟수를 확인하고 룰렛 보상을 지급
     */
    public void monthlyAttendanceReward() {
        List<User> allUsers = userRepository.findAll();
        LocalDateTime firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay();
        LocalDateTime lastDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth()).atTime(23, 59, 59);

        for (User user : allUsers) {
            long attendanceCount = checkAttendanceRepository.countByUserAndAttendanceDateBetween(user, firstDayOfLastMonth, lastDayOfLastMonth);
            if (attendanceCount >= MIN_DAYS_FOR_ROULETTE_REWARD) {
                user.increaseAttendanceRouletteCount();
                userRepository.save(user);
            }
        }
    }
}

