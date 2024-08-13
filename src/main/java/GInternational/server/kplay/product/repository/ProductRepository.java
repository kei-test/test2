package GInternational.server.kplay.product.repository;

import GInternational.server.kplay.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long>,ProductRepositoryCustom {

    @Query("SELECT p FROM product p WHERE p.prd_id = :prdId")
    Optional<Product> findByPrdId(@Param("prdId") int prdId);
}