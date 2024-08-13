package GInternational.server.api.repository;

import GInternational.server.api.entity.SuddenRecharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuddenRechargeRepository extends JpaRepository<SuddenRecharge, Long> {
}
