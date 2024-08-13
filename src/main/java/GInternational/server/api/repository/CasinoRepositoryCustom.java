package GInternational.server.api.repository;



import GInternational.server.api.entity.CasinoTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface CasinoRepositoryCustom {

    Page<CasinoTransaction> findByUserIdAndCasinoTransaction(Long userId, String description, Pageable pageable);
    Long countByUserId(Long userId,String description);



    List<CasinoTransaction> findByCasinoTransaction(String description, LocalDate startDate, LocalDate endDate);
}
