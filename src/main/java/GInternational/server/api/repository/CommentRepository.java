package GInternational.server.api.repository;

import GInternational.server.api.entity.Articles;
import GInternational.server.api.entity.Comment;
import GInternational.server.api.entity.EventsBoard;
import GInternational.server.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    Optional<Comment> findByIdAndArticles(Long commentId, Articles articles);

    Optional<Comment> findByIdAndEventsBoard(Long commentId, EventsBoard eventsBoard);

    Page<Comment> findByWriter(User user, Pageable pageable);

    int countByWriterAndCreatedAtBetween(User writer, LocalDateTime startDateTime, LocalDateTime endDateTime);
}
