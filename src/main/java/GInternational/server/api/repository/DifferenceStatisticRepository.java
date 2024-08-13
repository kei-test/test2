package GInternational.server.api.repository;

import GInternational.server.api.entity.DedicatedAccount;
import GInternational.server.api.entity.DifferenceStatistic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DifferenceStatisticRepository extends JpaRepository<DifferenceStatistic, Long> {

    List<DifferenceStatistic> findByCreatedAtBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    DifferenceStatistic findTopByOrderByCreatedAtDesc();
}

