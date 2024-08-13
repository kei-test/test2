package GInternational.server.api.repository;

import GInternational.server.api.entity.LevelBetSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelBetSettingRepository extends JpaRepository<LevelBetSetting, Long> {
    Optional<LevelBetSetting> findByLv(Integer lv);
}
