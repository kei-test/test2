package GInternational.server.kplay.balance.repository;

import GInternational.server.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CasinoBalanceRepository extends JpaRepository<User, Long> {

    Optional<User> findByAasId(Integer aasId);
}