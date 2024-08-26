package GInternational.server.api.repository;


import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Optional<Wallet> findById(Long walletId);

    Optional<Wallet> findByUser(User user);
    Optional<Wallet> findByUserId(Long userId);

    @Query("SELECT w FROM wallet w JOIN FETCH w.user WHERE w.id = :walletId")
    Optional<Wallet> findByIdWithUser(@Param("walletId") Long walletId);

    // 모든 Wallet의 sportsBalance 합계 조회
    @Query("SELECT SUM(w.sportsBalance) FROM wallet w")
    Long sumAllSportsBalance();

    // 모든 Wallet의 point 합계 조회
    @Query("SELECT SUM(w.point) FROM wallet w")
    Long sumAllPoint();

    @Query("SELECT COUNT(w) FROM wallet w WHERE w.todayChargedCount = 1")
    Long countByTodayChargedCount();

    @Modifying
    @Transactional
    @Query("UPDATE wallet w SET w.todayDeposit = 0, w.todayWithdraw = 0")
    void resetDailyTransactionsForAllUsers();
}
