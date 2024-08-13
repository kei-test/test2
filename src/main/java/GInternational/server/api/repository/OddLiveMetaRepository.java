package GInternational.server.api.repository;

import GInternational.server.api.entity.meta.OddLiveMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OddLiveMetaRepository extends JpaRepository<OddLiveMetaData,String> {
}
