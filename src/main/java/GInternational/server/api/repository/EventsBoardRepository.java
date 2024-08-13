package GInternational.server.api.repository;

import GInternational.server.api.entity.EventsBoard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EventsBoardRepository extends JpaRepository<EventsBoard, Long>, JpaSpecificationExecutor<EventsBoard> {

    Optional<EventsBoard> findById(Long EventId);
}
