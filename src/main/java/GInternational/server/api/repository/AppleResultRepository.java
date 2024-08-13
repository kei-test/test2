package GInternational.server.api.repository;

import GInternational.server.api.entity.AppleResults;
import GInternational.server.api.vo.PaymentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppleResultRepository extends JpaRepository<AppleResults, Long> {

    List<AppleResults> findByStatus(PaymentStatusEnum status);
}
