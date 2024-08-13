package GInternational.server.kplay.bonus.repository;

import GInternational.server.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BonusRepository extends JpaRepository<User, Long> {

    Optional<User> findByAasId(Integer aasId);

}