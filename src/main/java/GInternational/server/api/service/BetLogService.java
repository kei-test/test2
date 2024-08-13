package GInternational.server.api.service;

import GInternational.server.api.dto.BetLogResponseDTO;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.kplay.debit.repository.DebitRepository;
import GInternational.server.kplay.game.entity.Game;
import GInternational.server.kplay.game.repository.GameRepository;
import GInternational.server.kplay.product.entity.Product;
import GInternational.server.kplay.product.repository.ProductRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class BetLogService {

    private final DebitRepository debitRepository;
    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(BetLogService.class);

    public Page<BetLogResponseDTO> searchBetLogs(PrincipalDetails principalDetails, LocalDateTime startOfDay, LocalDateTime endDateTime, String gameResult, String username, String nickname, String gameType, String gameName, Pageable pageable) {
        Specification<Debit> spec = Specification.where(null);

        if (startOfDay != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                logger.debug("Adding startOfDay filter: {}", startOfDay);
                return criteriaBuilder.greaterThanOrEqualTo(root.get("created_at"), startOfDay);
            });
        }

        if (endDateTime != null) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                logger.debug("Adding endDateTime filter: {}", endDateTime);
                return criteriaBuilder.lessThanOrEqualTo(root.get("created_at"), endDateTime);
            });
        }

        if (username != null && !username.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<User> userRoot = subquery.from(User.class);
                subquery.select(userRoot.get("aasId")).where(criteriaBuilder.equal(userRoot.get("username"), username));
                logger.debug("Adding username filter: {}", username);
                return criteriaBuilder.in(root.get("user_id")).value(subquery);
            });
        }

        if (nickname != null && !nickname.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<User> userRoot = subquery.from(User.class);
                subquery.select(userRoot.get("aasId")).where(criteriaBuilder.equal(userRoot.get("nickname"), nickname));
                logger.debug("Adding nickname filter: {}", nickname);
                return criteriaBuilder.in(root.get("user_id")).value(subquery);
            });
        }

        if (gameType != null && !gameType.isBlank()) {
            int[] prdIdRange = mapGameTypeToPrdIdRange(gameType);
            if (prdIdRange != null) {
                spec = spec.and((root, query, criteriaBuilder) -> {
                    logger.debug("Adding gameType filter: {}", gameType);
                    return criteriaBuilder.between(root.get("prd_id"), prdIdRange[0], prdIdRange[1]);
                });
            }
        }

        if (gameName != null && !gameName.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Subquery<Integer> subquery = query.subquery(Integer.class);
                Root<Game> gameRoot = subquery.from(Game.class);
                subquery.select(gameRoot.get("prdId"))
                        .where(criteriaBuilder.and(
                                criteriaBuilder.equal(gameRoot.get("name"), gameName),
                                criteriaBuilder.equal(gameRoot.get("prdId"), root.get("prd_id")),
                                criteriaBuilder.equal(gameRoot.get("gameIndex"), root.get("game_id"))
                        ));
                logger.debug("Adding gameName filter: {}", gameName);
                return criteriaBuilder.in(root.get("prd_id")).value(subquery);
            });
        }

        if (gameResult != null && !gameResult.isBlank()) {
            spec = spec.and((root, query, criteriaBuilder) -> {
                Join<Object, Object> credit = root.join("credit", JoinType.LEFT);
                logger.debug("Adding gameResult filter: {}", gameResult);
                if (gameResult.equals("당첨")) {
                    return criteriaBuilder.or(
                            criteriaBuilder.greaterThanOrEqualTo(credit.get("amount"), 1),
                            criteriaBuilder.greaterThanOrEqualTo(root.get("credit_amount"), BigDecimal.ONE)
                    );
                } else {
                    return criteriaBuilder.and(
                            criteriaBuilder.or(
                                    criteriaBuilder.lessThan(credit.get("amount"), 1),
                                    criteriaBuilder.isNull(credit.get("amount"))
                            ),
                            criteriaBuilder.or(
                                    criteriaBuilder.lessThan(root.get("credit_amount"), BigDecimal.ONE),
                                    criteriaBuilder.isNull(root.get("credit_amount"))
                            )
                    );
                }
            });
        }

        List<Debit> debits = debitRepository.findAll(spec, pageable).getContent();
        logger.debug("Found {} debits after filtering", debits.size());

        List<BetLogResponseDTO> betLogs = debits.stream().map(debit -> {
            User user = userRepository.findByAasId(debit.getUser_id()).orElseThrow(
                    () -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다.")
            );
            Optional<Game> gameOpt = gameRepository.findByPrdIdAndGameIndex(debit.getPrd_id(), debit.getGame_id()).stream().findFirst();
            String determinedGameName = gameOpt.map(Game::getName).orElse("Unknown Game");
            Optional<Product> productOpt = productRepository.findByPrdId(debit.getPrd_id());
            String prdName = productOpt.map(Product::getPrd_name).orElse("Unknown Product");

            String determinedGameType = determineGameType(debit.getPrd_id());
            BigDecimal betAmount = calculateBetAmount(debit);
            BigDecimal winAmount = calculateWinAmount(debit);
            String determinedGameResult = determineResult(winAmount);

            return new BetLogResponseDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getNickname(),
                    determinedGameName,
                    determinedGameType,
                    debit.getCreated_at(),
                    betAmount,
                    winAmount,
                    determinedGameResult,
                    prdName
            );
        }).collect(Collectors.toList());

        logger.debug("Found {} bet logs after processing", betLogs.size());

        return new PageImpl<>(betLogs, pageable, betLogs.size());
    }

    private String determineGameType(int prdId) {
        if (prdId >= 1 && prdId <= 50) return "카지노";
        else if (prdId >= 200 && prdId <= 299) return "슬롯";
        else if (prdId >= 101 && prdId <= 199) return "케이플레이 스포츠";
        else if (prdId == 300 || prdId == 301 || prdId >= 10002 && prdId <= 10003) return "아케이드";
        else return "기타";
    }

    private int[] mapGameTypeToPrdIdRange(String gameType) {
        switch (gameType) {
            case "카지노":
                return new int[]{1, 50};
            case "슬롯":
                return new int[]{200, 299};
            case "케이플레이 스포츠":
                return new int[]{101, 199};
            case "아케이드":
                return new int[]{300, 301, 10002, 10003};
            default:
                return null;
        }
    }

    private BigDecimal calculateBetAmount(Debit debit) {
        return BigDecimal.valueOf(debit.getAmount());
    }

    private BigDecimal calculateWinAmount(Debit debit) {
        if (debit.getCredit() != null) {
            return BigDecimal.valueOf(debit.getCredit().getAmount());
        } else {
            return BigDecimal.valueOf(debit.getCredit_amount());
        }
    }

    private String determineResult(BigDecimal winAmount) {
        return winAmount.compareTo(BigDecimal.ONE) >= 0 ? "당첨" : "낙첨";
    }
}