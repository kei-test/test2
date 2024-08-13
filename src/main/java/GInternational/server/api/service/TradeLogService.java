package GInternational.server.api.service;

import GInternational.server.api.dto.TradeLogResponseDTO;
import GInternational.server.api.entity.TradeLog;
import GInternational.server.api.mapper.TradeLogResponseMapper;
import GInternational.server.api.repository.TradeLogRepository;
import GInternational.server.api.vo.TradeLogCategory;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class TradeLogService {

    private final TradeLogRepository tradeLogRepository;
    private final TradeLogResponseMapper tradeLogResponseMapper;
    private final UserService userService;
    private final UserRepository userRepository;

    /**
     * 사용자의 거래 로그를 필터링하여 조회. 카테고리, 역할, 시작 및 종료 날짜, 사용자 이름을 기준으로 필터링할 수 있음.
     *
     * @param principalDetails 현재 인증된 사용자의 정보
     * @param category 거래 로그의 카테고리
     * @param role 사용자의 역할
     * @param startDate 조회 기간의 시작 날짜
     * @param endDate 조회 기간의 종료 날짜
     * @param username 사용자 이름
     * @return 필터링된 거래 로그의 목록을 DTO로 변환하여 반환.
     */
    public List<TradeLogResponseDTO> getFilteredTradeLogs(PrincipalDetails principalDetails,
                                                          TradeLogCategory category,
                                                          String role,
                                                          LocalDateTime startDate,
                                                          LocalDateTime endDate,
                                                          String username) {
        List<TradeLog> transactions = tradeLogRepository.findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), category));
            }

            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }

            if (startDate != null && endDate != null) {
                predicates.add(criteriaBuilder.between(root.get("processedAt"), startDate, endDate));
            }

            if (username != null) {
                predicates.add(criteriaBuilder.equal(root.get("userId"), username));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        });

        return transactions.stream()
                .map(tradeLogResponseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 거래 로그를 기록. 사용된 금액, 최종 금액, 거래 카테고리, 비고를 포함하여 거래 로그를 생성하고 저장.
     *
     * @param userId 사용자 ID
     * @param usedMoney 사용된 금액 또는 포인트
     * @param finalMoney 최종 금액 또는 포인트
     * @param category 거래의 카테고리
     * @param bigo 비고
     * @return 생성된 거래 로그를 DTO로 변환하여 반환.
     */
    public TradeLogResponseDTO recordTrade(Long userId, Long usedMoney, Long finalMoney, TradeLogCategory category, String bigo) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        TradeLog tradeLog = new TradeLog();
        tradeLog.setUserId(user);
        tradeLog.setUsername(user.getUsername());
        tradeLog.setNickName(user.getNickname());
        tradeLog.setRole(user.getRole());
        tradeLog.setProcessedAt(LocalDateTime.now());

        // 머니 혹은 포인트에 따라 처리
        if (category == TradeLogCategory.MONEY) {
            tradeLog.setFirstAmazonMoney(user.getWallet().getAmazonMoney()); // 현재 머니
            tradeLog.setAmazonMoney(usedMoney); // 처리 금액
            tradeLog.setFinalAmazonMoney(finalMoney); // 최종 머니
        } else if (category == TradeLogCategory.POINT) {
            tradeLog.setFirstAmazonPoint(user.getWallet().getAmazonPoint()); // 현재 포인트
            tradeLog.setAmazonPoint(usedMoney); // 처리 포인트
            tradeLog.setFinalAmazonPoint(finalMoney); // 최종 포인트
        }

        tradeLog.setBigo(bigo);
        tradeLog.setCategory(category);

        TradeLog savedTradeLog = tradeLogRepository.save(tradeLog);
        return tradeLogResponseMapper.toDto(savedTradeLog);
    }
}