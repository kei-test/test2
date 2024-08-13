package GInternational.server.api.repository;


import GInternational.server.api.entity.AmazonLoginHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AmazonLoginHistoryRepository extends JpaRepository<AmazonLoginHistory, Long> {

    // 특정 날짜 범위의 로그인 이력 조회
    List<AmazonLoginHistory> findByAttemptDateBetween(LocalDateTime start, LocalDateTime end);

    List<AmazonLoginHistory> findByAttemptUsername(String username);

    List<AmazonLoginHistory> findByAttemptNickname(String nickname);
}
