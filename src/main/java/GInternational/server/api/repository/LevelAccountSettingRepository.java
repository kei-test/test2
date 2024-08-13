package GInternational.server.api.repository;

import GInternational.server.api.entity.LevelAccountSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LevelAccountSettingRepository extends JpaRepository<LevelAccountSetting, Long> {

    Optional<LevelAccountSetting> findByLv(int lv);
}
