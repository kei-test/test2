package GInternational.server.api.repository;

import GInternational.server.api.entity.AutoTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AutoTransactionRepositoryCustom {

    List<AutoTransaction> searchByAutoTransactionCondition();


    Page<AutoTransaction> findByUserIdAndAutoTransaction(Long userId, Pageable pageable);

}
