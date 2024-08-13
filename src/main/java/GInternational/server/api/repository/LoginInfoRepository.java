package GInternational.server.api.repository;

import GInternational.server.api.entity.LoginInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LoginInfoRepository extends JpaRepository<LoginInfo, Long> {

    @Query("SELECT l FROM login_info l WHERE l.accessedIp = :accessedIp ORDER BY l.lastVisit DESC")
    List<LoginInfo> findAllByAccessedIpOrderByLastVisitDesc(String accessedIp);
}
