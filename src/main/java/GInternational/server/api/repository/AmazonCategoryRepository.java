package GInternational.server.api.repository;


import GInternational.server.api.entity.AmazonCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AmazonCategoryRepository extends JpaRepository<AmazonCategory,Long> {
}
