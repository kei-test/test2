package GInternational.server.api.repository;

import GInternational.server.api.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointRepositoryCustom {

    Page<PointTransaction> findByUserIdAndPointTransaction(Long userId, Pageable pageable);
    Long countByUserId(Long userId);

}
