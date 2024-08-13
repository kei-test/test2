package GInternational.server.api.repository;

import GInternational.server.api.entity.AppleSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppleSettingRepository extends JpaRepository<AppleSettings, Long> {
}
