package GInternational.server.api.repository;

import GInternational.server.api.entity.AttendanceRouletteResults;
import GInternational.server.api.vo.PaymentStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttendanceRouletteResultRepository extends JpaRepository<AttendanceRouletteResults, Long> {

    List<AttendanceRouletteResults> findByStatus(PaymentStatusEnum status);
}
