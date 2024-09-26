package GInternational.server.kplay.debit.service;

import GInternational.server.amzn.repo.AmazonRollingTransactionRepository;
import GInternational.server.amzn.service.AmznRollingTransactionService;
import GInternational.server.api.service.ExpRecordService;
import GInternational.server.api.service.UserService;
import GInternational.server.api.vo.ExpRecordEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.kplay.debit.dto.DebitAmazonResponseDTO;
import GInternational.server.kplay.debit.dto.DebitRequestDTO;
import GInternational.server.kplay.debit.dto.DebitResponseDTO;
import GInternational.server.kplay.debit.dto.DebitUserResponseDTO;
import GInternational.server.kplay.debit.entity.Debit;
import GInternational.server.kplay.debit.mapper.DebitListMapper;
import GInternational.server.kplay.debit.repository.DebitRepository;
import GInternational.server.api.service.LoginStatisticService;
import GInternational.server.api.service.MoneyLogService;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.kplay.game.entity.Game;
import GInternational.server.kplay.game.repository.GameRepository;
import GInternational.server.kplay.product.entity.Product;
import GInternational.server.kplay.product.repository.ProductRepository;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.sun.jdi.LongValue;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static GInternational.server.kplay.credit.entity.QCredit.credit;
import static GInternational.server.kplay.debit.entity.QDebit.debit;
import static GInternational.server.kplay.game.entity.QGame.game;
import static GInternational.server.kplay.product.entity.QProduct.product;
import static GInternational.server.api.entity.QUser.user;


@Service
@RequiredArgsConstructor
@Transactional(value = "clientServerTransactionManager")
public class DebitService {
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final DebitRepository debitRepository;
    private final DebitListMapper debitListMapper;
    private final LoginStatisticService loginStatisticService;
    private final MoneyLogService moneyLogService;
    private final ExpRecordService expRecordService;
    private final AmazonRollingTransactionRepository amazonRollingTransactionRepository;
    private final AmznRollingTransactionService amznRollingTransactionService;
    private final UserService userService;
    private final GameRepository gameRepository;
    private final ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(DebitService.class);


    @Value("${secret.secret-key}")
    private String secretKey;

    /**
     * 사용자의 베팅 요청을 처리하고 결과를 반환.
     *
     * @param debitRequestDTO 베팅 요청 데이터를 담은 DTO
     * @param secretHeader 요청 헤더에 포함된 비밀 키
     * @return DebitResponseDTO 베팅 처리 결과
     */
    public DebitResponseDTO calledDebit(DebitRequestDTO debitRequestDTO, String secretHeader, HttpServletRequest request) {
        int status = (secretHeader.equals(secretKey)) ? 1 : 0;

        String clientIp = request.getRemoteAddr();

        if (status == 1) {
            User user = userRepository.findByAasId(debitRequestDTO.getUser_id()).orElse(null);
            Debit existingDebit = debitRepository.findByTxnId(debitRequestDTO.getTxn_id()).orElse(null);
            Wallet wallet;

            if (user == null) {
                return DebitResponseDTO.createFailureResponse("INVALID_USER");
            } else if (user.getWallet().getCasinoBalance() < debitRequestDTO.getAmount()) {
                return DebitResponseDTO.createFailureResponse("INSUFFICIENT_FUNDS");
            } else if (existingDebit != null && existingDebit.getTxnId().equals(debitRequestDTO.getTxn_id())) {
                return DebitResponseDTO.createFailureResponse("DUPLICATE_DEBIT");
            } else {
                wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                        (() -> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "지갑 정보 없음"));
            }

            long usedAmount = Long.valueOf(debitRequestDTO.getAmount());
            String description = null;

            String bettingCategory = getBettingCategory(debitRequestDTO.getPrd_id());
            if (bettingCategory.equals("카지노")) {
                Product product = productRepository.findByPrdId(debitRequestDTO.getPrd_id()).orElse(null);
                description = product.getPrd_name() + "(" + bettingCategory + ")";
            } else {
                Game game = gameRepository.searchByPrdIdAndGameIndex(debitRequestDTO.getPrd_id(),debitRequestDTO.getGame_id()).orElse(null);
                description = game.getName() + "(" + bettingCategory + ")";
            }





            long newWalletCasinoBalance = user.getWallet().getCasinoBalance() - debitRequestDTO.getAmount();
            moneyLogService.recordMoneyUsage(user.getId(), usedAmount ,wallet.getSportsBalance(), newWalletCasinoBalance, MoneyLogCategoryEnum.베팅차감, description);

            if (debitRequestDTO.getCredit_amount() != 0) {
                newWalletCasinoBalance += debitRequestDTO.getCredit_amount();
                moneyLogService.recordMoneyUsage(user.getId(), (long) debitRequestDTO.getCredit_amount(), wallet.getSportsBalance(), newWalletCasinoBalance, MoneyLogCategoryEnum.당첨, description);
            }

            Debit savedDebit = Debit.builder()
                    .user_id(debitRequestDTO.getUser_id())
                    .prd_id(debitRequestDTO.getPrd_id())
                    .game_id(debitRequestDTO.getGame_id())
                    .table_id(debitRequestDTO.getTable_id())
                    .amount(debitRequestDTO.getAmount())
                    .txnId(debitRequestDTO.getTxn_id())
                    .credit_amount(debitRequestDTO.getCredit_amount())
                    .remainAmount(newWalletCasinoBalance)
                    .build();
            debitRepository.save(savedDebit);


            //1.베팅을 한 유저가 daeId,bonId,buId,chongId,maeId 가 있는지,추천코드를 통해 가입한 유저가 맞는지 검증
            //2.추천받은 상위 유저의 설정된 롤링 퍼센트를 구해서 연산 후 해당 상위 유저에게 지급
            //3.지급받은 내역 저장
            Long daeId = null;
            Long bonId = null;
            Long buId = null;
            Long chongId = null;
            Long maeId = null;
            String partnerType = null;
            User partnerUser = null;
            double rollingAmount = 0;
            int betAmount = debitRequestDTO.getAmount();


            if (user.getPartnerType() != null) {
                //파트너일 경우 상위 찾기
                daeId = user.getDaeId();
                bonId = user.getBonId();
                buId = user.getBuId();
                chongId = user.getChongId();
                maeId = user.getMaeId();
                partnerType = user.getPartnerType();

                if (daeId != null || partnerType.equals("본사")) {
                    partnerUser = userRepository.findById(daeId).orElse(null);

                    if (bettingCategory.equals("카지노")) {
                        double cRolling = partnerUser.getCasinoRolling();
                        rollingAmount = betAmount * (cRolling / 100);
                    } else if (bettingCategory.equals("슬롯")) {
                        double sRolling = partnerUser.getSlotRolling();
                        rollingAmount = betAmount * (sRolling / 100);
                    }
                    Wallet pWallet = walletRepository.findByUser(partnerUser).orElse(null);
                    long cvtAmount = (long) Math.ceil(rollingAmount);  //Math.ceil 소수점 올림처리
                    pWallet.setAmazonMileage(pWallet.getAmazonMileage() + cvtAmount);
                    walletRepository.save(pWallet);

                    //롤링 지금 로그 저장
                    amznRollingTransactionService.createTransaction(user, bettingCategory,betAmount,cvtAmount,partnerUser.getId(),savedDebit, pWallet);
                } else if (bonId != null || partnerType.equals("부본사")) {
                    partnerUser = userRepository.findById(bonId).orElse(null);

                    if (bettingCategory.equals("카지노")) {
                        double cRolling = partnerUser.getCasinoRolling();
                        rollingAmount = betAmount * (cRolling / 100);
                    } else if (bettingCategory.equals("슬롯")) {
                        double sRolling = partnerUser.getSlotRolling();
                        rollingAmount = betAmount * (sRolling / 100);
                    }

                    Wallet pWallet = walletRepository.findByUser(partnerUser).orElse(null);
                    long cvtAmount = (long) Math.ceil(rollingAmount);
                    pWallet.setAmazonMileage(pWallet.getAmazonMileage() + cvtAmount);
                    walletRepository.save(pWallet);

                    amznRollingTransactionService.createTransaction(user, bettingCategory,betAmount,cvtAmount,partnerUser.getId(),savedDebit, pWallet);
                } else if (buId != null || partnerType.equals("총판")) {
                    partnerUser = userRepository.findById(buId).orElse(null);

                    if (bettingCategory.equals("카지노")) {
                        double cRolling = partnerUser.getCasinoRolling();
                        rollingAmount = betAmount * (cRolling / 100);
                    } else if (bettingCategory.equals("슬롯")) {
                        double sRolling = partnerUser.getSlotRolling();
                        rollingAmount = betAmount * (sRolling / 100);
                    }

                    Wallet pWallet = walletRepository.findByUser(partnerUser).orElse(null);
                    long cvtAmount = (long) Math.ceil(rollingAmount);
                    pWallet.setAmazonMileage(pWallet.getAmazonMileage() + cvtAmount);
                    walletRepository.save(pWallet);

                    amznRollingTransactionService.createTransaction(user, bettingCategory,betAmount,cvtAmount,partnerUser.getId(),savedDebit, pWallet);
                } else if (chongId != null || partnerType.equals("매장")) {
                    partnerUser = userRepository.findById(chongId).orElse(null);

                    if (bettingCategory.equals("카지노")) {
                        double cRolling = partnerUser.getCasinoRolling();
                        rollingAmount = betAmount * (cRolling / 100);
                    } else if (bettingCategory.equals("슬롯")) {
                        double sRolling = partnerUser.getSlotRolling();
                        rollingAmount = betAmount * (sRolling / 100);
                    }

                    Wallet pWallet = walletRepository.findByUser(partnerUser).orElse(null);
                    long cvtAmount = (long) Math.ceil(rollingAmount);
                    pWallet.setAmazonMileage(pWallet.getAmazonMileage() + cvtAmount);
                    walletRepository.save(pWallet);
                    amznRollingTransactionService.createTransaction(user, bettingCategory,betAmount,cvtAmount,partnerUser.getId(),savedDebit, pWallet);
                }
            } else if (user.isAmazonUser()) {
                String referredBy = user.getReferredBy();
                partnerUser = userRepository.findByUsername(referredBy);

                if (bettingCategory.equals("카지노")) {
                    double cRolling = partnerUser.getCasinoRolling();
                    rollingAmount = betAmount * (cRolling / 100);
                } else if (bettingCategory.equals("슬롯")) {
                    double sRolling = partnerUser.getSlotRolling();
                    rollingAmount = betAmount * (sRolling / 100);
                }

                Wallet pWallet = walletRepository.findByUser(partnerUser).orElse(null);
                long cvtAmount = (long) Math.ceil(rollingAmount);
                pWallet.setAmazonMileage(pWallet.getAmazonMileage() + cvtAmount);
                walletRepository.save(pWallet);
                amznRollingTransactionService.createTransaction(user, bettingCategory,betAmount,cvtAmount,partnerUser.getId(),savedDebit, pWallet);
            }


            user.getWallet().setCasinoBalance(newWalletCasinoBalance);
            long currentAccumulatedCasinoBet = user.getWallet().getAccumulatedCasinoBet();
            long currentAccumulatedSlotBet = user.getWallet().getAccumulatedSlotBet();
            if (bettingCategory.equals("카지노")) {
                user.getWallet().setAccumulatedCasinoBet(currentAccumulatedCasinoBet + debitRequestDTO.getAmount());
            } else if (bettingCategory.equals("슬롯")) {
                user.getWallet().setAccumulatedSlotBet(currentAccumulatedSlotBet + debitRequestDTO.getAmount());
            }
            user.setLastBetTime(savedDebit.getCreatedAt());
            walletRepository.save(user.getWallet());
            userRepository.save(user);

            if ("ROLE_USER".equals(user.getRole())) {
                loginStatisticService.recordDebitParticipant(debitRequestDTO.getUser_id());
            }

            if (debitRequestDTO.getPrd_id() >= 1 && debitRequestDTO.getPrd_id() <= 99) {
                expRecordService.recordDailyExp(user.getId(), user.getUsername(), user.getNickname(), 5, clientIp, ExpRecordEnum.카지노베팅경험치);
            } else if (debitRequestDTO.getPrd_id() >= 101 && debitRequestDTO.getPrd_id() <= 199) {
                expRecordService.recordDailyExp(user.getId(),user.getUsername(),user.getNickname(),10,clientIp, ExpRecordEnum.케이플레이스포츠베팅경험치);
            } else if (debitRequestDTO.getPrd_id() >= 200 && debitRequestDTO.getPrd_id() <= 299) {
                expRecordService.recordDailyExp(user.getId(), user.getUsername(), user.getNickname(), 1, clientIp, ExpRecordEnum.슬롯베팅경험치);
            } else if (debitRequestDTO.getPrd_id() == 10002 || debitRequestDTO.getPrd_id() == 10003 || debitRequestDTO.getPrd_id() == 300 || debitRequestDTO.getPrd_id() == 301) {
                expRecordService.recordDailyExp(user.getId(), user.getUsername(), user.getNickname(), 5, clientIp, ExpRecordEnum.아케이드베팅경험치);
            } else {
                return DebitResponseDTO.createFailureResponse("INVALID_PRD_ID");
            }

            String error = getErrorMessage(status, user, secretHeader);
            if (status == 1) {
                return new DebitResponseDTO(status, wallet.getCasinoBalance());
            } else {
                return DebitResponseDTO.createFailureResponse(error);
            }
        }
        return DebitResponseDTO.createFailureResponse("ACCESS_DENIED");
    }

    /**
     * 오류 상황에 따른 메시지를 반환.
     *
     * @param status 처리 상태 코드
     * @param user 요청한 사용자
     * @param secretHeader 요청 헤더에 포함된 비밀 키
     * @return String 오류 메시지
     */
    private String getErrorMessage(int status, User user, String secretHeader) {
        if (status == 0) {
            if (user == null) {
                return "INVALID_USER";
            } else if (!secretHeader.equals(secretKey)) {
                return "ACCESS_DENIED";
            } else {
                return "UNKNOWN_ERROR";
            }
        }
        return null;
    }

    /**
     * 사용자의 베팅 목록을 페이지네이션하여 반환.
     *
     * @param page 요청한 페이지 번호
     * @param size 페이지 당 표시할 아이템 수
     * @param principalDetails 요청한 사용자의 인증 정보
     * @return Page<DebitRequestDTO> 베팅 목록 페이지
     */
    public Page<DebitRequestDTO> debitList(int page, int size, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Debit> pages = debitRepository.findAll(pageable);

        List<DebitRequestDTO> response = pages.stream()
                .map(debitListMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(response);
    }

    /**
     * 특정 사용자의 베팅 내역을 검색하여 반환.
     *
     * @param userId 검색할 사용자 ID
     * @param size 페이지 당 표시할 아이템 수
     * @param type 검색 유형
     * @param page 요청한 페이지 번호
     * @return Page<DebitUserResponseDTO> 사용자의 베팅 내역 페이지
     */
    public Page<DebitUserResponseDTO> searchMyDebit(int userId, int size, String type, int page) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Tuple> results = debitRepository.findByUserIdWithCreditAmount(userId, type, pageable);

        List<DebitUserResponseDTO> list = results.getContent().stream()
                .map(tuple -> {
                    Debit debit = tuple.get(0, Debit.class); // 예시로 인덱스 0을 사용, 실제로 사용하는 인덱스에 따라 수정
                    String prdName = tuple.get(product.prd_name);
                    Integer creditAmount = tuple.get(credit.amount) != null ? tuple.get(credit.amount) : 0;
                    String gameName = tuple.get(game.name);

                    return new DebitUserResponseDTO(
                            debit.getId(),
                            debit.getUser_id(),
                            debit.getAmount(),
                            prdName,
                            debit.getPrd_id(),
                            debit.getTxnId(),
                            creditAmount,
                            debit.getGame_id(),
                            gameName,
                            debit.getTable_id(),
                            debit.getCredit_amount(),
                            debit.getCreated_at(),
                            debit.getRemainAmount() - debit.getAmount());})
                .collect(Collectors.toList());

        return new PageImpl<>(list, pageable, results.getTotalElements());
    }

    /**
     * 사용자의 베팅 내역을 검색하여 반환.
     *
     * @param size 페이지 당 표시할 아이템 수
     * @param type 검색 유형
     * @param page 요청한 페이지 번호
     * @return Page<DebitAmazonResponseDTO> 아마존 서비스를 통한 사용자 베팅 내역 페이지
     */
    public Page<DebitAmazonResponseDTO> searchMyDebits(int size, String type, int page, LocalDateTime startDate, LocalDateTime endDate, PrincipalDetails principalDetails) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("debit.id").descending());
        Page<DebitAmazonResponseDTO> results = debitRepository.findByUserIdWithCreditAmount(type, startDate, endDate, pageable, principalDetails);
        return results;
    }

    /**
     * prd_id에 따라 게임 카테고리를 결정합니다.
     *
     * @param prdId 제품 ID
     * @return "카지노" 또는 "슬롯"
     */
    private String getBettingCategory(int prdId) {
        if (prdId >= 1 && prdId <= 50) return "카지노";
        else if (prdId >= 200 && prdId <= 299) return "슬롯";
        else if (prdId >= 101 && prdId <= 199) return "케이플레이 스포츠";
        else if (prdId == 300 || prdId == 301 || prdId >= 10002 && prdId <= 10003) return "아케이드";
        else return "기타";
    }
}