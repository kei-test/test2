package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoRechargePhone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AutoRechargePhoneRepository extends JpaRepository<AutoRechargePhone, Long> {
}
