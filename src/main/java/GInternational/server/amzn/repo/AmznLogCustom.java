package GInternational.server.amzn.repo;

import GInternational.server.amzn.dto.log.AmznLogDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface AmznLogCustom {

    Page<AmznLogDTO> searchByAmazonLoginHistory(String username, String nickname, LocalDate startDate, LocalDate endDate, Pageable pageable);

}
