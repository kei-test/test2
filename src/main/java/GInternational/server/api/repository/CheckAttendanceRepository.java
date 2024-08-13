package GInternational.server.api.repository;

import GInternational.server.api.entity.CheckAttendance;
import GInternational.server.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CheckAttendanceRepository extends JpaRepository<CheckAttendance, Long>, JpaSpecificationExecutor<CheckAttendance> {

    Optional<CheckAttendance> findByUserAndAttendanceDate(User user, LocalDateTime date);

    long countByUserAndAttendanceDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);

    List<CheckAttendance> findByUserAndAttendanceDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate);
}
