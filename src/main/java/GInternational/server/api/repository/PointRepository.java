package GInternational.server.api.repository;

import GInternational.server.api.entity.PointTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointRepository extends JpaRepository<PointTransaction,Long>,PointRepositoryCustom {
}
