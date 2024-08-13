package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.kplay.debit.repository.DebitRepository;
import GInternational.server.api.dto.UserSlotBetWinDTO;
import GInternational.server.api.domain.UserSlotStats;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static GInternational.server.api.service.RollingService.VALID_PRD_IDS;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class SlotStatisticsService {

    private final DebitRepository debitRepository;
    private final UserRepository userRepository;

    /**
     * 지정된 기간 동안의 사용자별 슬롯 게임 통계 계산.
     *
     * @param start 통계를 시작할 날짜와 시간
     * @param end 통계를 종료할 날짜와 시간
     * @param principalDetails 인증된 사용자의 상세 정보
     * @return List<UserSlotBetWinDTO> 계산된 사용자 슬롯 게임 통계 목록
     */
    public List<UserSlotBetWinDTO> calculateUserSlotStatistics(LocalDateTime start, LocalDateTime end, PrincipalDetails principalDetails) {
        List<UserSlotStats> stats = debitRepository.findSlotStatistics(start, end, VALID_PRD_IDS);
        return stats.stream()
                .map(stat -> {
                    User user = userRepository.findByAasIdAndRoles(stat.getUserId())
                            .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
                    BigDecimal totalWin = stat.getTotalWin() != null ? stat.getTotalWin() : BigDecimal.ZERO;
                    BigDecimal totalBet = stat.getTotalBet() != null ? stat.getTotalBet() : BigDecimal.ZERO;
                    BigDecimal totalProfit = totalBet.subtract(totalWin);
                    return new UserSlotBetWinDTO(
                            user.getUsername(),
                            totalBet,
                            totalWin,
                            totalProfit
                    );
                })
                .collect(Collectors.toList());
    }
}
