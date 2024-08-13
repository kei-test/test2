package GInternational.server.api.service;

import GInternational.server.api.entity.meta.OddMetaData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OddMetaRepository extends JpaRepository<OddMetaData,String> {
}
