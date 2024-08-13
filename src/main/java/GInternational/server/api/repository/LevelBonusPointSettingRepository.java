package GInternational.server.api.repository;

import GInternational.server.api.entity.LevelBonusPointSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelBonusPointSettingRepository extends JpaRepository<LevelBonusPointSetting, Long> {

    Optional<LevelBonusPointSetting> findByLv(int lv);
}
