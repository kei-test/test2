package GInternational.server.api.service;

import GInternational.server.api.dto.ActiveUserResponseDTO;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class ActiveUserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    /**
     * 최근 로그인한 상위 30명의 사용자 정보 조회
     *
     * @param principalDetails 사용자 인증 정보
     * @return 최근 로그인한 사용자의 상세 정보 목록
     */
    public List<ActiveUserResponseDTO> findTop30RecentlyLoggedInUsers(PrincipalDetails principalDetails) {
        List<User> users = userRepository.findTop30ByLastVisitNotNullOrderByLastVisit();

        return users.stream().map(user -> {
            Optional<Wallet> walletOpt = walletRepository.findByUserId(user.getId());

            Long sportsBalance = walletOpt.map(Wallet::getSportsBalance).orElse(0L);
            Long casinoBalance = walletOpt.map(Wallet::getCasinoBalance).orElse(0L);
            Long point = walletOpt.map(Wallet::getPoint).orElse(0L);
            Long depositTotal = walletOpt.map(Wallet::getDepositTotal).orElse(0L);
            Long withdrawTotal = walletOpt.map(Wallet::getWithdrawTotal).orElse(0L);
            String lastAccessedIp = Optional.ofNullable(user.getLastAccessedIp()).orElse("Unknown, 방문 기록 없음");
            String lastAccessedDevice = Optional.ofNullable(user.getLastAccessedDevice()).orElse("Unknown, 방문 기록 없음");
            String distributor = Optional.ofNullable(user.getDistributor()).orElse("Unknown");
            String store = Optional.ofNullable(user.getStore()).orElse("Unknown");
            String lastAccessedCountry = Optional.ofNullable(user.getLastAccessedCountry()).orElse("Unknown, 방문 기록 없음");

            String lastVisit = Optional.ofNullable(user.getLastVisit())
                    .map(LocalDateTime::toString)
                    .orElse("방문 기록 없음");
            String lastRechargedAt = walletOpt.flatMap(w -> Optional.ofNullable(w.getLastRechargedAt()))
                    .map(LocalDateTime::toString)
                    .orElse("충전 기록 없음");

            return new ActiveUserResponseDTO(
                    user.getId(),
                    user.getLv(),
                    user.getUsername(),
                    user.getNickname(),
                    "game",
                    sportsBalance,
                    casinoBalance,
                    point,
                    depositTotal,
                    withdrawTotal,
                    user.getVisitCount(),
                    lastAccessedIp,
                    lastAccessedDevice,
                    distributor,
                    store,
                    lastVisit,
                    lastRechargedAt,
                    lastAccessedCountry
            );
        }).collect(Collectors.toList());
    }
}
