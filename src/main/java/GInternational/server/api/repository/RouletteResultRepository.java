package GInternational.server.api.repository;

import GInternational.server.api.entity.RouletteResults;
import GInternational.server.api.entity.User;
import GInternational.server.api.vo.PaymentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouletteResultRepository extends JpaRepository<RouletteResults, Long> {

    List<RouletteResults> findByStatus(PaymentStatusEnum status);

    List<RouletteResults> findByUserId(User user);
}
