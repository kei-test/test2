package GInternational.server.api.repository;

import GInternational.server.api.entity.DedicatedAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DedicatedAccountRepository extends JpaRepository<DedicatedAccount, Long> {

    // 주어진 레벨에 해당하는 활성화된 전용계좌 조회
    @Query("SELECT da FROM dedicated_account da JOIN da.levels l WHERE l = :lv AND da.isActive = :isActive")
    List<DedicatedAccount> findByLevelsContainsAndIsActive(int lv, boolean isActive);
}