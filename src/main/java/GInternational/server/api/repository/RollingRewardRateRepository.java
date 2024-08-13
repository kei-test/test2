package GInternational.server.api.repository;

import GInternational.server.api.entity.RollingRewardRate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RollingRewardRateRepository extends JpaRepository<RollingRewardRate, Integer> {
}
