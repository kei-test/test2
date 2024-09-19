package GInternational.server.kplay.debit.repository;

import GInternational.server.kplay.debit.dto.DebitAmazonResponseDTO;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.security.auth.PrincipalDetails;
import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface DebitCustomRepository {
    List<Debit> findDataWithNOMatchingTxnId();

    Page<Debit> findByUserId(int userId, Pageable pageable);

    Page<Tuple> findByUserIdWithCreditAmount(int userId, String type, Pageable pageable);

    Page<DebitAmazonResponseDTO> findByUserIdWithCreditAmount(String type, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, PrincipalDetails principalDetails);
}
