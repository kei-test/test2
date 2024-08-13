package GInternational.server.api.service;

import GInternational.server.api.entity.AutoDepositTransaction;
import GInternational.server.api.repository.AutoDepositTransactionRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AutoDepositTransactionService {


    private final AutoDepositTransactionRepository autoDepositTransactionRepository;

    /**
     * 지정된 기간 동안의 자동 입금 거래를 조회.
     *
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 지정된 기간 내의 자동 입금 거래 목록
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public List<AutoDepositTransaction> findAllADT(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        LocalDateTime startDateTime;
        LocalDateTime endDateTime;

        if (startDate == null) {
            startDateTime = LocalDateTime.now().minusDays(1000);
        } else {
            startDateTime = startDate.atStartOfDay();
        }

        if (endDate == null) {
            endDateTime = LocalDateTime.now();
        } else {
            endDateTime = endDate.atStartOfDay();
        }
        return autoDepositTransactionRepository.findByDateBetween(startDateTime, endDateTime);
    }

    /**
     * 자동 입금 거래를 조회하는 기능을 수행. 기간을 지정하지 않으면 현재 날짜를 기준으로 조회.
     *
     * @param startDate 조회 시작 날짜, 기본값은 현재 날짜
     * @param endDate 조회 종료 날짜, 기본값은 현재 날짜
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 지정된 기간 내의 자동 입금 거래 목록
     */
    public List<AutoDepositTransaction> autoDepositTransaction(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        return autoDepositTransactionRepository.findByAutoDepositTransaction(startDate, endDate);
    }
}
