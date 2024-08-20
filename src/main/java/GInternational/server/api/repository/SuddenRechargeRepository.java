package GInternational.server.api.repository;

import GInternational.server.api.entity.SuddenRecharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuddenRechargeRepository extends JpaRepository<SuddenRecharge, Long> {

    List<SuddenRecharge> findAllByEndDateTimeBeforeAndEnabledTrue(LocalDateTime now);

}
