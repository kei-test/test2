package GInternational.server.amzn.service;

import GInternational.server.amzn.dto.log.AmznLogDTO;

import GInternational.server.amzn.repo.AmznLogCustomImpl;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(value = "clientServerTransactionManager")
public class AmznLogService {

    private final AmznLogCustomImpl amznLogCustomImpl;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Page<AmznLogDTO> amazonSystemLog(String username, String nickname, LocalDate startDate, LocalDate endDate, int page, int size) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        Pageable pageable = PageRequest.of(page -1, size);
        Page<AmznLogDTO> content = amznLogCustomImpl.searchByAmazonLoginHistory(username,nickname,startDate,endDate,pageable);
        List<AmznLogDTO> list = content.getContent();
        return new PageImpl<>(list,pageable,content.getTotalElements());
    }



}
