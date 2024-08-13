package GInternational.server.kplay.debit.repository;

import GInternational.server.kplay.debit.entity.Debit;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DebitCustomRepository {
    List<Debit> findDataWithNOMatchingTxnId();

    Page<Debit> findByUserId(int userId, Pageable pageable);

    Page<Tuple> findByUserIdWithCreditAmount(int userId, String type, Pageable pageable);

    Page<Tuple> findByUserIdWithCreditAmount(String type, Pageable pageable);
}
