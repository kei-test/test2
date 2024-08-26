package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonMessages;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AmazonMessageRepository extends JpaRepository<AmazonMessages, Long>, AmazonMessageRepositoryCustom {

    Optional<AmazonMessages> findById(Long id);
}
