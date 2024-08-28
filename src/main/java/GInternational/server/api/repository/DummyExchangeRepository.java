package GInternational.server.api.repository;

import GInternational.server.api.entity.DummyExchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DummyExchangeRepository extends JpaRepository<DummyExchange, Long> {
}
