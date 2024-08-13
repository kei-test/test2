package GInternational.server.api.repository;

import GInternational.server.api.entity.Messages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface MessageRepository extends JpaRepository<Messages, Long>, JpaSpecificationExecutor<Messages>, MessageRepositoryCustom {

    Optional<Messages> findById(Long id);
}
