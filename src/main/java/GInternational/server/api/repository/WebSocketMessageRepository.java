package GInternational.server.api.repository;

import GInternational.server.api.entity.WebSocketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WebSocketMessageRepository extends JpaRepository<WebSocketMessage, Long> {
}
