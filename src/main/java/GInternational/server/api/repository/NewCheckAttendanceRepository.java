package GInternational.server.api.repository;

import GInternational.server.api.entity.NewCheckAttendance;
import GInternational.server.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NewCheckAttendanceRepository extends JpaRepository<NewCheckAttendance, Long> {
    Optional<NewCheckAttendance> findByUserAndAttendanceDate(User user, LocalDateTime attendanceDate);

    long countByUserAndAttendanceDateBetween(User user, LocalDateTime start, LocalDateTime end);

    List<NewCheckAttendance> findByUserAndAttendanceDateBetween(User user, LocalDateTime start, LocalDateTime end);

    @Query("SELECT n FROM new_check_attendance n WHERE " +
            "(:username IS NULL OR n.user.username = :username) AND " +
            "(:nickname IS NULL OR n.user.nickname = :nickname) AND " +
            "(:startDate IS NULL OR n.attendanceDate >= :startDate) AND " +
            "(:endDate IS NULL OR n.attendanceDate <= :endDate)")
    List<NewCheckAttendance> findByCriteria(
            @Param("username") String username,
            @Param("nickname") String nickname,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);
}