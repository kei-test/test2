package GInternational.server.api.repository;

import GInternational.server.api.entity.DailyLimit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DailyLimitRepository extends JpaRepository<DailyLimit, Long> {

    Optional<DailyLimit> findById(Long id);
}
