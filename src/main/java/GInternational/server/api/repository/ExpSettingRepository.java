package GInternational.server.api.repository;

import GInternational.server.api.entity.ExpSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExpSettingRepository extends JpaRepository<ExpSetting, Long> {

    Optional<ExpSetting> findByLv(int lv);
}
