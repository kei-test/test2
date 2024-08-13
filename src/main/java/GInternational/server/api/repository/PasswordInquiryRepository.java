package GInternational.server.api.repository;

import GInternational.server.api.entity.PasswordInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordInquiryRepository extends JpaRepository<PasswordInquiry, Long> {
}
