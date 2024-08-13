package GInternational.server.api.service;

import GInternational.server.api.dto.CasinoTransactionResponseDTO;
import GInternational.server.api.entity.CasinoTransaction;
import GInternational.server.api.repository.CasinoRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class CasinoTransactionService {

    private final CasinoRepository casinoRepository;
    private final UserService userService;

    /**
     * 특정 사용자의 카지노 트랜잭션을 페이지네이션하여 조회.
     *
     * @param userId           사용자 ID
     * @param description      트랜잭션 설명
     * @param page             페이지 번호
     * @param size             페이지 크기
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 페이지네이션된 카지노 트랜잭션 정보를 담은 Page 객체
     */
    public Page<CasinoTransaction> getCasinoTransactionsByUserId(Long userId, String description, int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("userId").descending());
        Page<CasinoTransaction> transactions = casinoRepository.findByUserIdAndCasinoTransaction(userId, description, pageable);
        long totalElements = casinoRepository.countByUserId(userId, description);
        return new PageImpl<>(transactions.getContent(), pageable, totalElements);
    }

    /**
     * 지정된 설명과 기간 내의 카지노 트랜잭션을 조회.
     *
     * @param description      트랜잭션 설명
     * @param startDate        시작 날짜
     * @param endDate          종료 날짜
     * @param username         사용자 이름
     * @param nickname         닉네임
     * @param ip               IP 주소
     * @param principalDetails 현재 사용자의 인증 정보
     * @return 조회된 카지노 트랜잭션 리스트
     */
    public List<CasinoTransactionResponseDTO> findByCasinoTransaction(String description, LocalDate startDate, LocalDate endDate,
                                                                      String username, String nickname, String ip, PrincipalDetails principalDetails) {
        Specification<CasinoTransaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (description != null && !description.isEmpty()) {
                predicates.add(cb.like(root.get("description"), "%" + description + "%"));
            }

            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("processedAt"), startDateTime, endDateTime));
            }

            if (username != null && !username.isEmpty()) {
                predicates.add(cb.equal(root.get("user").get("username"), username));
            }

            if (nickname != null && !nickname.isEmpty()) {
                predicates.add(cb.equal(root.get("user").get("nickname"), nickname));
            }

            if (ip != null && !ip.isEmpty()) {
                predicates.add(cb.equal(root.get("ip"), ip));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<CasinoTransaction> casinoTransactions = casinoRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "processedAt"));

        return casinoTransactions.stream()
                .map(CasinoTransactionResponseDTO::new)
                .collect(Collectors.toList());
    }
}
