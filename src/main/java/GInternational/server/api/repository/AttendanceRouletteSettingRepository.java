package GInternational.server.api.repository;

import GInternational.server.api.entity.AttendanceRouletteSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRouletteSettingRepository extends JpaRepository<AttendanceRouletteSettings, Long> {
}
