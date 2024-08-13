package GInternational.server.api.service;

import GInternational.server.api.dto.CompTransactionResDTO;
import GInternational.server.api.entity.CompTransaction;
import GInternational.server.api.repository.CompTransactionRepository;
import GInternational.server.api.vo.RollingTransactionEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class CompTransactionService {

    private final CompTransactionRepository compTransactionRepository;

    /**
     * 주어진 조건에 따라 롤링 거래를 검색하여 리스트로 반환.
     *
     * @param startDate         조회 시작 날짜 (옵션)
     * @param endDate           조회 종료 날짜 (옵션)
     * @param status            거래 상태 (옵션)
     * @param username          사용자 이름 (옵션)
     * @param nickname          닉네임 (옵션)
     * @param userIp            사용자 IP (옵션)
     * @param principalDetails  현재 사용자의 인증 정보, 사용자의 권한 및 식별 정보를 포함
     * @return                  조회된 롤링 거래 정보의 리스트
     */
    public List<CompTransaction> findTransactions(LocalDate startDate, LocalDate endDate, RollingTransactionEnum status,
                                                  String username, String nickname, String userIp, PrincipalDetails principalDetails) {
        Specification<CompTransaction> spec = Specification.where(null);

        if (startDate != null && endDate != null) {
            LocalDateTime startDateTime = startDate.atStartOfDay();
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("createdAt"), startDateTime, endDateTime));
        }

        if (status != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (username != null && !username.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("username"), username));
        }

        if (nickname != null && !nickname.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("nickname"), nickname));
        }

        if (userIp != null && !userIp.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("userIp"), userIp));
        }

        return compTransactionRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * 롤링 거래 엔티티 목록을 DTO 목록으로 변환.
     *
     * @param transactions   롤링 거래 엔티티 목록
     * @return               DTO로 변환된 롤링 거래 목록
     */
    public List<CompTransactionResDTO> toDTOList(List<CompTransaction> transactions) {
        List<CompTransactionResDTO> dtoList = new ArrayList<>();
        for (CompTransaction transaction : transactions) {
            CompTransactionResDTO dto = new CompTransactionResDTO();
            dto.setId(transaction.getId());
            dto.setUserId(transaction.getUserId());
            dto.setLv(transaction.getLv());
            dto.setUsername(transaction.getUsername());
            dto.setNickname(transaction.getNickname());
            dto.setCreatedAt(transaction.getCreatedAt());
            dto.setProcessedAt(transaction.getProcessedAt());
            dto.setLastDayChargeBalance(transaction.getLastDayChargeSportsBalance());
            dto.setCalculatedReward(transaction.getCalculatedReward());
            dto.setRate(transaction.getRate());
            dto.setLastDayAmount(transaction.getLastDayAmount());
            dto.setSportsBalance(transaction.getSportsBalance());
            dto.setCasinoBalance(transaction.getCasinoBalance());
            dto.setStatus(transaction.getStatus());
            dto.setUserIp(transaction.getUserIp());
            dtoList.add(dto);
        }
        return dtoList;
    }
}
