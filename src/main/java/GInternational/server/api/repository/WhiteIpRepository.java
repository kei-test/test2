package GInternational.server.api.repository;

import GInternational.server.api.entity.WhiteIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface WhiteIpRepository extends JpaRepository<WhiteIp, Long>, JpaSpecificationExecutor<WhiteIp> {

    Optional<WhiteIp> findByWhiteIp(String whiteIp);
}
