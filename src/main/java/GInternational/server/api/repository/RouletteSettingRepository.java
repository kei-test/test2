package GInternational.server.api.repository;

import GInternational.server.api.entity.RouletteSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouletteSettingRepository extends JpaRepository<RouletteSettings, Long> {
}
