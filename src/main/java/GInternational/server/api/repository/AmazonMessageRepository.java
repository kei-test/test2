package GInternational.server.api.repository;

import GInternational.server.api.entity.AmazonMessages;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AmazonMessageRepository extends JpaRepository<AmazonMessages, Long>, AmazonMessageRepositoryCustom {

    Optional<AmazonMessages> findById(Long id);
}
