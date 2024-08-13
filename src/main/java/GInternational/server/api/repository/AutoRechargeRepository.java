package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoRecharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AutoRechargeRepository extends JpaRepository<AutoRecharge, Long>, JpaSpecificationExecutor<AutoRecharge> {
    Optional<AutoRecharge> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
