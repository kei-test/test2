package GInternational.server.api.service;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.RechargeTransaction;
import GInternational.server.api.repository.RechargeTransactionRepository;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class RechargeTransactionService {

    private final RechargeTransactionRepository rechargeTransactionRepository;

    /**
     * 특정 사용자의 충전 거래를 페이징하여 조회.
     *
     * @param userId           사용자 ID
     * @param page             페이지 번호
     * @param size             페이지 크기
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 충전 거래 내역과 페이징 정보
     */
    public Page<RechargeTransaction> getTransactionsByUserId(Long userId, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("userId").descending());
        Page<RechargeTransaction> transactions = rechargeTransactionRepository.findByUserIdAndTransaction(userId, pageable);
        long totalElements = rechargeTransactionRepository.countByUserId(userId);
        return new PageImpl<>(transactions.getContent(),pageable,totalElements);
    }

    /**
     * 특정 기간과 상태에 따른 충전 거래를 조회합니다.
     *
     * @param startDateTime     시작일
     * @param endDateTime       종료일
     * @param status            거래 상태
     * @param principalDetails  현재 사용자의 인증 정보
     * @return 조회된 충전 거래 목록
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public List<RechargeTransaction> findAllByProcessedAtBetweenAndStatus(LocalDateTime startDateTime, LocalDateTime endDateTime, TransactionEnum status, PrincipalDetails principalDetails) {
        return rechargeTransactionRepository.findAllByProcessedAtBetweenAndStatus(startDateTime, endDateTime, status);
    }

    /**
     * 특정 기간과 상태에 따른 충전 거래를 조회하고 DTO 리스트로 변환.
     *
     * @param startDateTime     시작일
     * @param endDateTime       종료일
     * @param status            거래 상태
     * @param principalDetails  현재 사용자의 인증 정보
     * @param lv                레벨
     * @param gubun             회원상태
     * @param distributor       총판명
     * @param store             매장명
     * @param nickname          닉네임
     * @param username          아이디
     * @param ownerName         예금주
     * @param phone             전화번호
     * @return 조회된 충전 거래 DTO 목록
     */
    public Map<String, Object> findAllTransactionsByCriteria(
            LocalDateTime startDateTime, LocalDateTime endDateTime, TransactionEnum status, PrincipalDetails principalDetails,
            Integer lv, String gubun, String distributor, String store, String nickname,
            String username, String ownerName, String phone, UserGubunEnum userGubunEnum) {

        Specification<RechargeTransaction> spec = Specification.where(null);

        if (startDateTime == null && endDateTime == null) {
            spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.isNotNull(root.get("createdAt")));
        } else {
            if (startDateTime == null) {
                startDateTime = LocalDateTime.of(2000, 1, 1, 0, 0);
            }
            if (endDateTime == null) {
                endDateTime = LocalDateTime.now().with(LocalTime.MAX);
            }
            LocalDateTime finalStartDateTime = startDateTime;
            LocalDateTime finalEndDateTime = endDateTime;
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("createdAt"), finalStartDateTime, finalEndDateTime));
        }

        if (status != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), status));
        }

        if (lv != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("lv"), lv));
        }

        if (gubun != null && !gubun.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("gubun"), gubun));
        }

        if (distributor != null && !distributor.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("distributor"), distributor));
        }

        if (store != null && !store.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("store"), store));
        }

        if (nickname != null && !nickname.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("nickname"), nickname));
        }

        if (username != null && !username.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("username"), username));
        }

        if (ownerName != null && !ownerName.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("wallet").get("ownerName"), ownerName));
        }

        if (phone != null && !phone.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("user").get("phone"), phone));
        }

        if (userGubunEnum != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("userGubunEnum"), userGubunEnum));
        }

        List<RechargeTransaction> rechargeTransactions = rechargeTransactionRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));


        // 조회 기간 동안의 전체 충전 금액 계산
        Long totalRechargeAmount = rechargeTransactions.stream()
                .filter(rt -> rt.getStatus() == TransactionEnum.APPROVAL || rt.getStatus() == TransactionEnum.AUTO_APPROVAL)
                .mapToLong(RechargeTransaction::getRechargeAmount)
                .sum();

        List<RechargeTransactionResDTO> transactionDTOs = rechargeTransactions.stream()
                .map(rt -> new RechargeTransactionResDTO(
                        rt.getId(),
                        rt.getUser().getId(),
                        rt.getUsername(),
                        rt.getNickname(),
                        rt.getPhone(),
                        rt.getGubun(),
                        rt.getRechargeAmount(),
                        rt.getRemainingSportsBalance(),
                        rt.getBonus(),
                        rt.getRemainingPoint(),
                        rt.getMessage(),
                        rt.getStatus(),
                        rt.getIp(),
                        rt.getSite(),
                        rt.getDepositor(),
                        rt.getUser().getUserGubunEnum(),
                        rt.getUser().getWallet() != null ? new WalletDetailDTO(rt.getUser().getWallet()) : null,
                        rt.getUser().getLv(),
                        rt.getUser().getDistributor(),
                        rt.getUser().getStore(),
                        rt.getCreatedAt(),
                        rt.getProcessedAt(),
                        rt.getUser().getWallet() != null ? rt.getUser().getWallet().getExchangeProcessedAt() : null))
                .collect(Collectors.toList());

        TotalRechargeAmountDTO totalAmountDTO = new TotalRechargeAmountDTO(totalRechargeAmount);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("transactions", transactionDTOs);
        resultMap.put("totalRechargeAmount", totalAmountDTO);

        return resultMap;
    }


    /**
     * 특정 기간에 충전 거래를 조회.
     *
     * @param startDate         시작일
     * @param endDate           종료일
     * @param principalDetails  현재 사용자의 인증 정보
     * @return 조회된 충전 거래 목록
     */
    public List<RechargeTransaction> rechargedSettlement(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();
        return rechargeTransactionRepository.findByStatusAndProcessedAtBetween(TransactionEnum.APPROVAL, startDateTime, endDateTime);
    }

    /**
     * 승인된 거래를 페이지네이션하여 조회.
     *
     * @param userId            사용자 ID
     * @param startDateTime     시작 일시
     * @param endDateTime       종료 일시
     * @param page              페이지 번호
     * @param size              페이지 크기
     * @param principalDetails  현재 사용자의 인증 정보
     * @return 페이지네이션된 승인된 거래 목록
     */
    public Page<RechargeTransactionApprovedDTO> getApprovedTransactionsWithPagination(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return rechargeTransactionRepository.findByUserIdAndStatusAndProcessedAtBetween(
                        userId, TransactionEnum.APPROVAL, startDateTime, endDateTime, pageable)
                .map(transaction -> new RechargeTransactionApprovedDTO(transaction.getId(), transaction.getRechargeAmount(), transaction.getProcessedAt()));
    }

    /**
     * 특정 기간의 충전 거래에 대한 총 충전금액과 평균 충전금액, 그리고 사용자의 모든 충전금액의 합을 조회.
     *
     * @param userId            사용자 ID
     * @param startDateTime     시작 일시
     * @param endDateTime       종료 일시
     * @param principalDetails  현재 사용자의 인증 정보
     * @return 충전 거래 요약 정보
     */
    public RechargeTransactionsSummaryDTO getTransactionsSummary(Long userId, LocalDateTime startDateTime, LocalDateTime endDateTime, PrincipalDetails principalDetails) {
        List<RechargeTransaction> approvedRechargeTransactions = rechargeTransactionRepository.findByUserIdAndStatusAndProcessedAtBetween(
                userId, TransactionEnum.APPROVAL, startDateTime, endDateTime);
        long totalRechargeAmount = approvedRechargeTransactions.stream()
                .mapToLong(RechargeTransaction::getRechargeAmount)
                .sum();
        BigDecimal averageRechargeAmount = approvedRechargeTransactions.isEmpty() ? BigDecimal.ZERO :
                BigDecimal.valueOf(approvedRechargeTransactions.stream()
                        .mapToLong(RechargeTransaction::getRechargeAmount)
                        .average()
                        .orElse(0));
        long totalAllTimeRechargeAmount = rechargeTransactionRepository.findByUserIdAndStatus(userId, TransactionEnum.APPROVAL).stream()
                .mapToLong(RechargeTransaction::getRechargeAmount)
                .sum();
        return new RechargeTransactionsSummaryDTO(totalRechargeAmount, averageRechargeAmount, totalAllTimeRechargeAmount);
    }
}


