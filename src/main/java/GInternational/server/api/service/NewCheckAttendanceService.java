package GInternational.server.api.service;

import GInternational.server.api.dto.MonthlyAttendanceDTO;
import GInternational.server.api.dto.NewCheckAttendanceDTO;
import GInternational.server.api.entity.NewCheckAttendance;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.NewCheckAttendanceRepository;
import GInternational.server.api.repository.RechargeTransactionRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.common.ipinfo.service.IpInfoService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class NewCheckAttendanceService {

    private final NewCheckAttendanceRepository newCheckAttendanceRepository;
    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final UserRepository userRepository;
    private final PointLogService pointLogService;
    private final IpInfoService ipInfoService;
    private final WalletRepository walletRepository;

    private static final BigDecimal MIN_CHARGED_AMOUNT_FOR_ATTENDANCE = BigDecimal.valueOf(100000);
    private static final TransactionEnum APPROVAL_STATUS = TransactionEnum.APPROVAL;

    public void newAttendanceCheck(Long userId, PrincipalDetails principalDetails, HttpServletRequest request) {
        BigDecimal rechargeAmount = rechargeTransactionRepository.findSumByUserAndDateAndStatus(
                userId, LocalDate.now(), APPROVAL_STATUS.name());

        if (rechargeAmount == null || rechargeAmount.compareTo(MIN_CHARGED_AMOUNT_FOR_ATTENDANCE) < 0) {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "100,000원 이상 충전 후 출석체크 하세요");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String clientIp = ipInfoService.getClientIp(request);

        Optional<NewCheckAttendance> existingAttendance = newCheckAttendanceRepository.findByUserAndAttendanceDate(user, LocalDateTime.now());

        if (existingAttendance.isPresent()) {
            return;
        }

        NewCheckAttendance newAttendance = new NewCheckAttendance();
        newAttendance.setUser(user);
        newAttendance.setAttendanceDate(LocalDateTime.now());
        newCheckAttendanceRepository.save(newAttendance);

        int pointsToReward = calculateRewardPoints(user.getLv());
        addPointsToWallet(user, pointsToReward);

        pointLogService.recordPointLog(user.getId(), (long) pointsToReward, PointLogCategoryEnum.일일출석보상, clientIp, "일일 출석 보상 지급");
    }


    public MonthlyAttendanceDTO getCurrentMonthAttendance(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        LocalDate firstDayOfCurrentMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDayOfCurrentMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());

        List<NewCheckAttendance> attendanceList = newCheckAttendanceRepository.findByUserAndAttendanceDateBetween(user, firstDayOfCurrentMonth.atStartOfDay(), lastDayOfCurrentMonth.atTime(23, 59, 59));
        int attendanceCount = attendanceList.size();
        List<LocalDateTime> attendanceDates = attendanceList.stream().map(NewCheckAttendance::getAttendanceDate).collect(Collectors.toList());

        return new MonthlyAttendanceDTO(attendanceCount, attendanceDates);
    }

    public List<NewCheckAttendanceDTO> getAllAttendance(String username, String nickname, LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        List<NewCheckAttendance> attendanceList = newCheckAttendanceRepository.findByCriteria(username, nickname, startDate, endDate);
        return attendanceList.stream()
                .map(a -> new NewCheckAttendanceDTO(a.getUser().getId(), a.getUser().getUsername(), a.getUser().getNickname(), a.getUser().getLv(), a.getAttendanceDate()))
                .collect(Collectors.toList());
    }

    @Scheduled(cron = "0 0 0 1 * ?")
    public void handleMonthlyAttendanceResetAndReward() {
        checkAndRewardCompleteAttendanceForMonth();
    }

    private void checkAndRewardCompleteAttendanceForMonth() {
        List<User> allUsers = userRepository.findAll();
        LocalDate firstDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(1);
        LocalDate lastDayOfLastMonth = LocalDate.now().minusMonths(1).withDayOfMonth(LocalDate.now().minusMonths(1).lengthOfMonth());

        for (User user : allUsers) {
            long attendanceCount = newCheckAttendanceRepository.countByUserAndAttendanceDateBetween(user, firstDayOfLastMonth.atStartOfDay(), lastDayOfLastMonth.atTime(23, 59, 59));
            if (attendanceCount == firstDayOfLastMonth.lengthOfMonth()) {
                addPointsToWallet(user, 100000);
                pointLogService.recordPointLog(user.getId(), 100000L, PointLogCategoryEnum.만근출석보상, "매월 1일 자동지급건. ip정보 없음", "만근출석 보상 자동지급");
            }
        }
    }

    private void addPointsToWallet(User user, int points) {
        Wallet wallet = walletRepository.findById(user.getWallet().getId())
                .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다"));

        wallet.setPoint(wallet.getPoint() + points);
        walletRepository.save(wallet);
    }

    private int calculateRewardPoints(int userLevel) {
        if (userLevel == 1 || userLevel == 2) {
            return 1000;
        } else if (userLevel == 3 || userLevel == 4) {
            return 2000;
        } else if (userLevel == 5 || userLevel == 6) {
            return 3000;
        } else {
            return 0;
        }
    }
}
