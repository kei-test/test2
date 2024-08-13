package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonBonus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmazonBonusRepository extends JpaRepository<AmazonBonus, Long> {

    Optional<AmazonBonus> findFirstByOrderByIdDesc();
}
