package GInternational.server.api.service;

import GInternational.server.api.entity.PointTransaction;
import GInternational.server.api.repository.PointRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class PointTransactionService {

    private final PointRepository pointRepository;

    /**
     * 특정 사용자의 포인트 거래를 페이징하여 조회.
     *
     * @param userId           사용자 ID
     * @param page             페이지 번호
     * @param size             페이지 크기
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 포인트 거래 내역과 페이징 정보
     */
    public Page<PointTransaction> getPointTransactionsByUserId(Long userId, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("userId").descending());
        Page<PointTransaction> transactions = pointRepository.findByUserIdAndPointTransaction(userId, pageable);
        long totalElements = pointRepository.countByUserId(userId);
        return new PageImpl<>(transactions.getContent(),pageable,totalElements);
    }
}
