package GInternational.server.api.service;


import GInternational.server.api.entity.AutoTransaction;
import GInternational.server.api.repository.AutoTransactionRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AutoTransactionService {


    private final AutoTransactionRepository autoTransactionRepository;

    /**
     * 사용자 ID에 따른 자동승인 문자 수신 내역을 페이징하여 조회.
     *
     * @param userId 조회할 사용자의 ID
     * @param page 페이지 번호
     * @param size 페이지 당 항목 수
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 사용자 ID에 따른 자동승인 문자 수신 내역 페이지
     */
    public Page<AutoTransaction> getAutoTransactionByUserId(Long userId, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page -1,size,Sort.by("id").descending());
        Page<AutoTransaction> autoTransactionPage = autoTransactionRepository.findByUserIdAndAutoTransaction(userId,pageable);
        return new PageImpl<>(autoTransactionPage.getContent(),pageable,autoTransactionPage.getTotalElements());
    }

    /**
     * 모든 자동승인 문자 수신 내역을 페이징하여 조회.
     *
     * @param page 페이지 번호
     * @param size 페이지 당 항목 수
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 모든 자동승인 문자 수신 내역 페이지
     */
    public Page<AutoTransaction> findAll(int page, int size, PrincipalDetails principalDetails) {
        return autoTransactionRepository.findAll(PageRequest.of(page -1,size,Sort.by("id").descending()));
    }
}
