package GInternational.server.api.service;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.BetHistory;
import GInternational.server.api.entity.MoneyLog;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.*;
import GInternational.server.api.vo.*;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.l_sport.batch.job.dto.order.DetailResponseDTO;
import GInternational.server.l_sport.batch.job.dto.order.MatchScoreDTO;
import GInternational.server.l_sport.batch.job.dto.order.ResponseDTO;
import GInternational.server.api.entity.meta.MatchMetaData;
import GInternational.server.l_sport.info.repository.FixtureRepository;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BetHistoryService {

    private final BetHistoryRepository betHistoryRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final WalletService walletService;
    private final FixtureRepository matchRepository;
    private final WalletRepository walletRepository;
    private final ExpRecordService expRecordService;
    private final MoneyLogService moneyLogService;
    private final MatchMetaRepository matchMetaRepository;
    private final MoneyLogRepository moneyLogRepository;

    @Autowired
    @Qualifier("entityManager")
    private EntityManager entityManager;
    @Autowired
    @Qualifier("lsportEntityManager")
    private EntityManager lsportEntityManager;




    public List<BetHistory> insertUserBets(List<BetHistoryReqDTO> betRequestDTOs, Long betGroupId, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = Optional.ofNullable(principalDetails.getUser()).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));
        LocalDateTime currentTime = LocalDateTime.now();
        String ipAddress = extractIp(request);

        List<BetHistory> betHistories = new ArrayList<>();

        LinkedList<MatchMetaData> metaMatches = new LinkedList<>();

        long existingBetsCount = betHistoryRepository.countByBetGroupId(betGroupId);
        long totalBetsInGroup = existingBetsCount + betRequestDTOs.size();
        BetFoldCountEnum foldCount = BetFoldCountEnum.fromBetCount(totalBetsInGroup);

        long totalBetAmount = 0;

        for (BetHistoryReqDTO dto : betRequestDTOs) {
            String bet = betRequestDTOs.get(0).getBet();
            totalBetAmount = Long.parseLong(bet);

            checkAndUpdateUserSportsBalance(user, dto.getBet());

            BetHistory betHistory = new BetHistory();
            BeanUtils.copyProperties(dto, betHistory);

            betHistory.setUser(user);
            betHistory.setBetStartTime(currentTime);
            betHistory.setBetGroupId(betGroupId);
            betHistory.setOrderStatus(OrderStatusEnum.WAITING);
            betHistory.setBetIp(ipAddress);
            BetFoldTypeEnum betType = BetFoldTypeEnum.determineType(totalBetsInGroup);
            betHistory.setBetFoldType(betType);
            betHistory.setBetType(dto.getBetType());
            betHistory.setBetFoldCount(foldCount);
            betHistory.setStartDate(dto.getStartDate());
            betHistory.setMatchStatus(dto.getMatchStatus());
            betHistory.setBetStatus("정상");
            betHistory.setReadStatus("미확인");
            betHistory.setReadBy("");
            betHistory.setReadAt(null);
            betHistory.setApi("N");
            betHistory.setCronApi("N");
            betHistory.setFailBonusCol("N");
            betHistories.add(betHistory);

            MatchMetaData matchMetaData = matchMetaRepository.findByMatchId(dto.getMatchId()).orElse(null);
            if (matchMetaData != null) {
                if (!dto.getBetType().equals(BetTypeEnum.IN_PLAY)) {
                    matchMetaData.setPreCount(matchMetaData.getPreCount() + 1);
                    long betValue = Long.parseLong(dto.getBet());  // 베팅금
                    long tempValue = Long.parseLong(matchMetaData.getPreTotalAmount()); //기존 베팅 총액
                    long updatedTotalAmount = tempValue + betValue;
                    matchMetaData.setPreTotalAmount(String.valueOf(updatedTotalAmount));
                } else {
                    matchMetaData.setLiveCount(matchMetaData.getLiveCount() + 1);
                    long betValue = Long.parseLong(dto.getBet());  // 베팅금
                    long tempValue = Long.parseLong(matchMetaData.getPreTotalAmount()); //기존 베팅 총액
                    long updatedTotalAmount = tempValue + betValue;
                    matchMetaData.setLiveTotalAmount(String.valueOf(updatedTotalAmount));
                }
                metaMatches.add(matchMetaData);
            } else {
                MatchMetaData newMetaData = new MatchMetaData();
                newMetaData.setMatchId(dto.getMatchId()); // matchId 설정
                if (!dto.getBetType().equals(BetTypeEnum.IN_PLAY)) {
                    long betValue = Long.parseLong(dto.getBet());  // 베팅금
                    newMetaData.setPreCount(+1);
                    newMetaData.setPreTotalAmount(String.valueOf(betValue));
                    newMetaData.setLiveTotalAmount("0");
                } else {
                    long betValue = Long.parseLong(dto.getBet());  // 베팅금
                    newMetaData.setLiveCount(+1);
                    newMetaData.setLiveTotalAmount(String.valueOf(betValue));
                    newMetaData.setPreTotalAmount("0");
                }
                metaMatches.add(newMetaData);
            }
        }

        expRecordService.recordDailyExp(user.getId(), user.getUsername(), user.getNickname(), 10, ipAddress, ExpRecordEnum.스포츠베팅경험치);

        long currentAccumulatedSportsBet = user.getWallet().getAccumulatedSportsBet();
        user.getWallet().setAccumulatedSportsBet(currentAccumulatedSportsBet + totalBetAmount);
        user.getWallet().setSportsBalance(user.getWallet().getSportsBalance() - totalBetAmount);
        walletRepository.save(user.getWallet());

        moneyLogService.recordMoneyUsage(user.getId(), totalBetAmount, user.getWallet().getSportsBalance(), MoneyLogCategoryEnum.베팅차감, betGroupId + "(SPORTS)");

        matchMetaRepository.saveAll(metaMatches);  //경기 카운트,금액 누적
        return betHistoryRepository.saveAll(betHistories);
    }


    //어드민 조회 시 그룹 아이디를 기준으로 그룹화시켜 반환하는 로직
    public Page<ResponseDTO> orderResponse(int page,
                                           int size,
                                           Long userId,
                                           BetTypeEnum custom,
                                           String username,
                                           String nickname,
                                           BetFoldCountEnum foldCount,
                                           String ip,
                                           Long id,
                                           List<Long> betGroupId,
                                           OrderStatusEnum orderStatus,
                                           Boolean deleted,
                                           String orderBy,
                                           LocalDate startDate,
                                           LocalDate endDate,
                                           PrincipalDetails principalDetails) {
        updateReadStatusForGroup(betGroupId, principalDetails.getUsername());
        User user = userRepository.findById(principalDetails.getUser().getId()).orElse(null);
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<DetailResponseDTO> responseDTOList = betHistoryRepository.searchOrderList(userId, custom, username, nickname, foldCount, ip, id, betGroupId,orderStatus, deleted,orderBy, pageable, startDate, endDate);

        List<DetailResponseDTO> content = responseDTOList.getContent();
        Map<Long, List<DetailResponseDTO>> groupMap = content.stream()
                .collect(Collectors.groupingBy(DetailResponseDTO::getBetGroupId, LinkedHashMap::new, Collectors.toList()));


        List<ResponseDTO> responseDTOs = new ArrayList<>();
        for (Map.Entry<Long, List<DetailResponseDTO>> entry : groupMap.entrySet()) {
            Long groupId = entry.getKey();
            List<DetailResponseDTO> orderList = entry.getValue();
            ResponseDTO responseDTO = new ResponseDTO();
            responseDTO.setBetGroupId(groupId);
            double calculateProfit = betHistoryRepository.calculateProfit(groupId);

            int bet = Integer.parseInt(orderList.get(0).getBet());


            if (orderList.get(0).getBetFoldType().toString().equals("THREE_FOLDER")) {
                calculateProfit *= 1.03;
                responseDTO.setEventRate("1.03");
            } else if (orderList.get(0).getBetFoldType().toString().equals("FIVE_FOLDER")) {
                calculateProfit *= 1.05;
                responseDTO.setEventRate("1.05");
            } else if (orderList.get(0).getBetFoldType().toString().equals("SEVEN_FOLDER")) {
                calculateProfit *= 1.07;
                responseDTO.setEventRate("1.07");
            } else if (orderList.get(0).getBetFoldType().toString().equals("SINGLE_FOLDER")) {
                responseDTO.setEventRate("DEFAULT");
            }

            responseDTO.setBet(orderList.get(0).getBet());
            responseDTO.setTotalRate(String.valueOf(calculateProfit));
            responseDTO.setExpectedProfit(String.valueOf(calculateProfit * bet));
            responseDTO.setBetFoldTypeEnum(orderList.get(0).getBetFoldType());
            responseDTO.setBetStartTime(orderList.get(0).getBetStartTime());
            responseDTO.setReadBy(orderList.get(0).getReadBy());
            responseDTO.setReadAt(orderList.get(0).getReadAt());
            responseDTO.setReadStatus(orderList.get(0).getReadStatus());
            responseDTO.setMonitoringStatus(orderList.get(0).getMonitoringStatus());
            responseDTO.setBetStatus(orderList.get(0).getBetStatus());
            responseDTO.setDeleted(orderList.get(0).isDeleted());
            responseDTO.setDeletedAt(orderList.get(0).getDeletedAt());

            // 모든 베팅의 결과처리가 이루어지지않아도 다폴 베팅박스에 속한 베팅 중 1개라도 낙첨건이 있을 경우 즉시 최종결과를 낙첨 처리한다.
            // 모든 베팅의 결과처리가 되어야만 베팅박스의 속한 베팅건들을 검증하고 최종결과를 할당한다.

            boolean allHits = orderList.stream()
                    .allMatch(detailResponseDTO -> detailResponseDTO.getOrderStatus() == OrderStatusEnum.HIT);
            boolean anyFail = orderList.stream()
                    .anyMatch(detailResponseDTO -> detailResponseDTO.getOrderStatus() == OrderStatusEnum.FAIL);
            boolean allCancel = orderList.stream()
                    .allMatch(detailResponseDTO -> detailResponseDTO.getOrderStatus() == OrderStatusEnum.CANCEL);
            boolean allCancelHit = orderList.stream()
                    .allMatch(detailResponseDTO -> detailResponseDTO.getOrderStatus() == OrderStatusEnum.CANCEL_HIT);
            boolean anyCancelHitOrHits = orderList.stream()
                    .anyMatch(detailResponseDTO -> detailResponseDTO.getOrderStatus() == OrderStatusEnum.CANCEL_HIT && detailResponseDTO.getOrderStatus() == OrderStatusEnum.HIT);

            if (allHits) {
                responseDTO.setOrderResult("HIT");
                responseDTO.setRealProfit(String.valueOf(calculateProfit * bet));
            } else if (anyCancelHitOrHits) {
                responseDTO.setOrderResult("HIT");
                double calculateProfitFinal = calculateProfit;
                double totalProfit = orderList.stream()
                        .filter(detailResponseDTO -> detailResponseDTO.getOrderStatus() == OrderStatusEnum.HIT)
                        .mapToDouble(detailResponseDTO -> calculateProfitFinal * Integer.parseInt(detailResponseDTO.getBet()))
                        .sum();
                responseDTO.setRealProfit(String.valueOf(totalProfit));
            } else if (anyFail) {
                responseDTO.setOrderResult("FAIL");
            } else if (allCancel) {
                responseDTO.setOrderResult("CANCEL");
                responseDTO.setRealProfit(String.valueOf(bet));
            } else if (allCancelHit) {
                responseDTO.setOrderResult("CANCEL_HIT");
                responseDTO.setRealProfit(String.valueOf(bet));
            }
            responseDTO.setList(orderList);


            // 경기의 스코어값 가져오기
            List<String> matchList = orderList.stream()
                    .map(DetailResponseDTO::getMatchId)
                    .collect(Collectors.toList());
            List<MatchScoreDTO> matchScore = matchRepository.searchByScoreData(matchList);

            Map<String, MatchScoreDTO> matchScoreMap = matchScore.stream().collect(Collectors.toMap(MatchScoreDTO::getMatchId, Function.identity()));

            for (DetailResponseDTO detailResponseDTO : orderList) {
                MatchScoreDTO matchScoreDTO = matchScoreMap.get(detailResponseDTO.getMatchId());
                if (matchScoreDTO != null) {
                    detailResponseDTO.setHomeScore(matchScoreDTO.getHomeScore());
                    detailResponseDTO.setAwayScore(matchScoreDTO.getAwayScore());
                    detailResponseDTO.setMatchStatus(matchScoreDTO.getStatus());
                }
            }
            responseDTOs.add(responseDTO);
        }
        long totalElements = betHistoryRepository.countByOrder(userId, custom, username, nickname, foldCount, ip, id, betGroupId,orderStatus, deleted, startDate, endDate);
        return new PageImpl<>(responseDTOs, pageable, totalElements);
    }

    public void updateReadStatusForGroup(List<Long> betGroupId, String username) {
        if (betGroupId != null && !betGroupId.isEmpty()) {
            LocalDateTime now = LocalDateTime.now();
            betHistoryRepository.updateReadStatusByGroupId(now, username, "확인", betGroupId.get(0));
        }
    }



    public void softDeleteBetHistoryByGroupId(Long betGroupId, PrincipalDetails principalDetails) {
        List<BetHistory> betHistories = betHistoryRepository.findByBetGroupIdAndUserId(betGroupId, principalDetails.getUser().getId());

        if (betHistories.isEmpty()) {
            throw new RuntimeException("해당 그룹의 베팅내역을 찾을 수 없습니다.");
        }

        betHistories.forEach(betHistory -> {
            betHistory.setDeleted(true);
            betHistory.setDeletedAt(LocalDateTime.now());
            betHistory.setBetStatus("삭제됨");
        });

        betHistoryRepository.saveAll(betHistories);
    }


    public void softDeleteAllBetHistoryByUser(Long userId, PrincipalDetails principalDetails) {
        if (!principalDetails.getUser().getId().equals(userId)) {
            throw new RuntimeException("자신의 베팅내역만 삭제할 수 있습니다.");
        }

        List<BetHistory> betHistories = betHistoryRepository.findByUserId(userId);

        if (betHistories.isEmpty()) {
            throw new RuntimeException("해당 사용자의 베팅내역이 없습니다.");
        }

        betHistories.forEach(betHistory -> {
            betHistory.setDeleted(true);
            betHistory.setDeletedAt(LocalDateTime.now());
            betHistory.setBetStatus("삭제됨");
        });

        betHistoryRepository.saveAll(betHistories);
    }

    public List<BetHistory> findAllByUserId(Long userId, PrincipalDetails principalDetails) {
        return betHistoryRepository.findByUserIdAndDeletedFalse(userId);
    }

    // match_status가 "1" 또는 "2"인 베팅 내역 기간별 조회
    public List<BetHistoryResDTO> findAllByMatchStatusOneOrTwoAndDateBetween(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetail) {
        LocalDateTime startDateTime = startDate.atStartOfDay(); // 해당 날짜의 시작
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59); // 해당 날짜의 끝

        List<BetHistory> betHistories = betHistoryRepository.findByMatchStatusOneOrTwoAndDateBetweenWithUser(startDateTime, endDateTime);
        return betHistories.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // match_status가 "3"인 베팅 내역 기간별 조회
    public List<BetHistoryResDTO> findAllByMatchStatusThreeAndDateBetween(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetail) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        List<BetHistory> betHistories = betHistoryRepository.findByMatchStatusThreeAndDateBetween(startDateTime, endDateTime);
        return betHistories.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    private void checkAndUpdateUserSportsBalance(User user, String betString) {
        long bet;
        try {
            bet = Long.parseLong(betString);
        } catch (NumberFormatException e) {
            throw new RestControllerException(ExceptionCode.INVALID_BET_AMOUNT, "잘못된 베팅 금액입니다.");
        }

        Wallet wallet = walletService.getUserWallet(user.getId());
        if (wallet.getSportsBalance() < bet) {
            throw new RestControllerException(ExceptionCode.INSUFFICIENT_SPORTS_MONEY, "보유 스포츠머니가 부족합니다.");
        }
        // Wallet의 스포츠 밸런스 업데이트
        wallet.setSportsBalance(wallet.getSportsBalance() - bet);

        // Wallet 및 User 정보 저장
        walletService.updateWalletBalance(wallet);
        userRepository.save(user);
    }

    private String extractIp(HttpServletRequest request) {
        String[] ipHeaderCandidates = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "X-Real-IP"
        };

        return Arrays.stream(ipHeaderCandidates)
                .map(request::getHeader)
                .filter(ip -> ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip))
                .findFirst()
                .orElse(request.getRemoteAddr());
    }

    public void cancelBetsByGroupId(Long betGroupId, PrincipalDetails principalDetails) {
        Long currentUserId = principalDetails.getUser().getId();
        String currentUserRole = principalDetails.getUser().getRole();

        // 베팅 그룹 ID로 모든 베팅 내역을 가져옵니다.
        List<BetHistory> betHistories = betHistoryRepository.findByBetGroupId(betGroupId);

        BetHistory distinctBetHistory = betHistoryRepository.findFirstDistinctByBetGroupId(betGroupId);
        Wallet wallet = walletRepository.findByUserId(distinctBetHistory.getUser().getId()).orElse(null);
        long bet = Long.parseLong(distinctBetHistory.getBet());  // 베팅금
        long sportsBalance = wallet.getSportsBalance();
        long value = sportsBalance + bet;

        MoneyLog moneyLog = MoneyLog.builder()
                .username(wallet.getUser().getUsername())
                .nickname(wallet.getUser().getNickname())
                .user(wallet.getUser())
                .category(MoneyLogCategoryEnum.베팅취소)
                .usedSportsBalance(bet)
                .finalSportsBalance(value)
                .bigo(distinctBetHistory.getBetGroupId() + "(SPORT)")
                .site("test")
                .build();
        moneyLogRepository.save(moneyLog);

        MatchMetaData matchMetaData = matchMetaRepository.findByMatchId(distinctBetHistory.getMatchId()).orElse(null);
        if (!distinctBetHistory.getBetType().equals(BetTypeEnum.IN_PLAY)) {
            matchMetaData.setPreCount(matchMetaData.getPreCount() - 1);
            long betValue = Long.parseLong(distinctBetHistory.getBet());  // 베팅금
            long tempValue = Long.parseLong(matchMetaData.getPreTotalAmount()); //기존 베팅 총액
            long updatedTotalAmount = tempValue - betValue;
            matchMetaData.setPreTotalAmount(String.valueOf(updatedTotalAmount));
        } else {
            matchMetaData.setLiveCount(matchMetaData.getLiveCount() - 1);
            long betValue = Long.parseLong(distinctBetHistory.getBet());  // 베팅금
            long tempValue = Long.parseLong(matchMetaData.getPreTotalAmount()); //기존 베팅 총액
            long updatedTotalAmount = tempValue - betValue;
            matchMetaData.setLiveTotalAmount(String.valueOf(updatedTotalAmount));
        }
        matchMetaRepository.save(matchMetaData);


        // 관리자와 매니저는 본인의 베팅이 아니어도 취소할 수 있습니다.
        if (!currentUserRole.equals("ROLE_ADMIN") && !currentUserRole.equals("ROLE_MANAGER")) {
            // 베팅 내역이 현재 로그인한 사용자의 것인지 확인합니다.
            if (betHistories.isEmpty() || !betHistories.get(0).getUser().getId().equals(currentUserId)) {
                throw new RestControllerException(ExceptionCode.UNAUTHORIZED_ACCESS, "본인의 베팅이 아니므로 취소할 수 없습니다.");
            }
        }

        OrderStatusEnum cancelStatus = determineCancelStatus(principalDetails.getUser().getRole());

        betHistories.forEach(betHistory -> processCancelHit(betHistory, cancelStatus));
        saveAllBetHistories(betHistories);
    }

    // 취소된(소프트딜리트) 베팅을 복구하는 메서드
    public void restoreCancelledBetsByGroupId(Long betGroupId, PrincipalDetails principalDetails) {
        List<BetHistory> betHistories = betHistoryRepository.findByBetGroupIdAndDeleted(betGroupId, true);

        for (BetHistory betHistory : betHistories) {
            betHistory.setDeleted(false);
            betHistory.setDeletedAt(null);
            betHistory.setBetStatus("정상");
        }
        saveAllBetHistories(betHistories);
    }

    // 전체 취소된 베팅 조회
    public List<BetResultDTO> findAllCancelledBets(PrincipalDetails principalDetails, LocalDateTime startDate, LocalDateTime endDate) {

        List<BetResultDTO> cancelledBets = new ArrayList<>();
        cancelledBets.addAll(findCancelledBetsByStatus(OrderStatusEnum.CANCEL, startDate, endDate));
        cancelledBets.addAll(findCancelledBetsByStatus(OrderStatusEnum.CANCEL_HIT, startDate, endDate));
        return cancelledBets;
    }

    // 관리자에 의해 취소된 베팅만 조회
    public List<BetResultDTO> findCancelledBetsByAdmin(PrincipalDetails principalDetails, LocalDateTime startDate, LocalDateTime endDate) {
        return findCancelledBetsByStatus(OrderStatusEnum.CANCEL_HIT, startDate, endDate);
    }

    // 유저에 의해 취소된 베팅만 조회
    public List<BetResultDTO> findCancelledBetsByUser(PrincipalDetails principalDetails, LocalDateTime startDate, LocalDateTime endDate) {
        return findCancelledBetsByStatus(OrderStatusEnum.CANCEL, startDate, endDate);
    }

    // 특정 취소 상태에 따른 베팅 조회
    private List<BetResultDTO> findCancelledBetsByStatus(OrderStatusEnum status, LocalDateTime startDate, LocalDateTime endDate) {
        List<BetResultDTO> cancelledBets = new ArrayList<>();
        int startNumber = 0;
        cancelledBets.addAll(convertToDTOs(betHistoryRepository.findByOrderStatusAndBetStartTimeBetween(OrderStatusEnum.CANCEL, startDate, endDate), startNumber));
        return cancelledBets;
    }

    private List<BetResultDTO> convertToDTOs(List<? extends BetHistory> betHistories, int startNumber) {
        List<BetResultDTO> dtos = new ArrayList<>();
        AtomicInteger globalSequence = new AtomicInteger(startNumber + 1);

        for (BetHistory history : betHistories) {
            int sequenceNumber = globalSequence.getAndIncrement();
            BetResultDTO dto = new BetResultDTO(
                    history.getBetGroupId(),
                    history.getUser() != null ? history.getUser().getId() : null, // User 엔티티의 ID
                    history.getBetFoldType(),
                    history.getBetType(),
                    history.getBetStartTime(),
                    history.getBet(),
                    history.getPrice(),
                    history.getBetReward(),
                    history.getProcessedAt(),
                    history.getBetFoldCount(),
                    history.getOrderStatus(),
                    history.getMatchStatus(),
                    history.getBetIp(),
                    sequenceNumber
            );
            dtos.add(dto);
        }
        return dtos;
    }


    private void processCancelHit(BetHistory betHistory, OrderStatusEnum cancelStatus) {
        Wallet wallet = walletService.getUserWallet(betHistory.getUser().getId());
        long betAmount = Long.parseLong(betHistory.getBet());
        wallet.setSportsBalance(wallet.getSportsBalance() + betAmount);
        walletService.updateWalletBalance(wallet);
        betHistory.setOrderStatus(cancelStatus);
        betHistory.setProcessedAt(LocalDateTime.now());
    }

    private void saveAllBetHistories(List<BetHistory> betHistories) {
        betHistoryRepository.saveAll(betHistories);
    }

    private OrderStatusEnum determineCancelStatus(String userRole) {
        if (userRole.equals("ROLE_USER")) {
            return OrderStatusEnum.CANCEL;
        } else if (userRole.equals("ROLE_ADMIN") || userRole.equals("ROLE_MANAGER")) {
            return OrderStatusEnum.CANCEL_HIT;
        }
        throw new IllegalArgumentException("유효하지 않은 사용자 역할:: " + userRole);
    }

    // matchId를 기준으로 베팅 내역 조회 및 그룹화

    @Transactional(value = "clientServerTransactionManager", readOnly = true)
    public BetHistoryCalculationResult findBetHistoriesGroupedByBetGroupId(String matchId, PrincipalDetails principalDetails) {
        // 매치 ID로 초기 베팅 내역 조회
        List<BetHistory> initialBetHistories = betHistoryRepository.findByMatchId(matchId);

        // 베팅 그룹 ID 추출
        Set<Long> betGroupIds = initialBetHistories.stream()
                .map(BetHistory::getBetGroupId)
                .collect(Collectors.toSet());

        // 베팅 그룹 ID별로 모든 베팅 내역 조회
        List<BetHistory> allBetHistories = betHistoryRepository.findByBetGroupIdIn(betGroupIds);

        // 모든 매치 ID 추출
        List<String> allMatchIds = allBetHistories.stream()
                .map(BetHistory::getMatchId)
                .distinct()
                .collect(Collectors.toList());

        // 매치 ID를 기준으로 스코어값 조회
        List<MatchScoreDTO> matchScores = matchRepository.searchByScoreData(allMatchIds);

        // 스코어 정보를 매치 ID를 키로 하는 맵으로 변환
        Map<String, MatchScoreDTO> matchScoreMap = matchScores.stream()
                .collect(Collectors.toMap(MatchScoreDTO::getMatchId, Function.identity()));

        // 전체 베팅금액 계산: 각 그룹에서 첫 번째 베팅금액만 합산
        BigDecimal totalBetAmount = allBetHistories.stream()
                .collect(Collectors.groupingBy(BetHistory::getBetGroupId))
                .values().stream()
                .map(group -> group.stream()
                        .findFirst()
                        .map(betHistory -> new BigDecimal(betHistory.getBet()))
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<BetHistoryGroupedDTO> groupedResult = new ArrayList<>();
        BigDecimal validBetAmountSum = BigDecimal.ZERO;
        BigDecimal validWinningAmountSum = BigDecimal.ZERO;
        BigDecimal totalBetReward = BigDecimal.ZERO;

        for (Long groupId : betGroupIds) {
            // 특정 그룹 ID에 해당하는 베팅들 필터링
            List<BetHistory> groupBets = allBetHistories.stream()
                    .filter(betHistory -> betHistory.getBetGroupId().equals(groupId))
                    .collect(Collectors.toList());

            // 대기 상태가 아닌 베팅이 존재하는지 확인
            boolean anyBetNotWaiting = groupBets.stream()
                    .anyMatch(bet -> bet.getOrderStatus() != OrderStatusEnum.WAITING);

            // 유효 베팅 금액 계산
            BigDecimal groupValidBetAmount = BigDecimal.ZERO;
            if (!anyBetNotWaiting && !groupBets.isEmpty()) {
                try {
                    groupValidBetAmount = new BigDecimal(groupBets.get(0).getBet());
                } catch (NumberFormatException e) {
                    // 숫자 형식 예외 처리
                }
            }
            validBetAmountSum = validBetAmountSum.add(groupValidBetAmount);

            // DTO 변환
            List<BetHistoryResDTO> resDTOList = groupBets.stream()
                    .map(betHistory -> {
                        MatchScoreDTO matchScore = matchScoreMap.get(betHistory.getMatchId());
                        // 스코어 정보가 있다면 해당 정보를 이용해 BetHistoryResDTO 생성
                        return new BetHistoryResDTO(betHistory,
                                matchScore != null ? matchScore.getHomeScore() : "0",
                                matchScore != null ? matchScore.getAwayScore() : "0");
                    })
                    .collect(Collectors.toList());

            // 모든 베팅이 HIT 상태일 경우에만 당첨금액을 계산합니다.
            BigDecimal groupTotalBetReward = calculateGroupBetReward(resDTOList);
            totalBetReward = totalBetReward.add(groupTotalBetReward);

            // 그룹별 유효 당첨 금액 계산
            BigDecimal groupValidWinningAmount = calculateValidWinningAmount(resDTOList, groupValidBetAmount);
            validWinningAmountSum = validWinningAmountSum.add(groupValidWinningAmount);

            groupedResult.add(new BetHistoryGroupedDTO(groupId, resDTOList));
        }

        BigDecimal totalProfitAmount = validBetAmountSum.subtract(totalBetReward);

        // 결과 객체 설정 및 반환
        BetHistoryCalculationResult result = new BetHistoryCalculationResult();
        result.setTotalBetAmount(totalBetAmount);
        result.setValidBetAmount(validBetAmountSum);
        result.setValidWinningAmount(validWinningAmountSum);
        result.setTotalBetReward(totalBetReward);
        result.setTotalProfitAmount(totalProfitAmount);
        result.setGroupedBetHistories(groupedResult);
        return result;
    }

    // 주어진 베팅 리스트에서 유효 당첨 금액을 계산
    private BigDecimal calculateValidWinningAmount(List<BetHistoryResDTO> bets, BigDecimal validBetAmount) {
        if (bets.isEmpty() || validBetAmount.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        // 그룹 내 모든 가격의 곱을 계산.
        BigDecimal priceProduct = bets.stream()
                .map(bet -> new BigDecimal(bet.getPrice()))
                .reduce(BigDecimal.ONE, BigDecimal::multiply);

        // 모든 베팅의 foldType은 동일.
        BetFoldTypeEnum betFoldType = bets.get(0).getBetFoldType();
        BigDecimal bonusMultiplier = getBonusMultiplierBasedOnBetFoldType(betFoldType);

        // 그룹의 유효 당첨 금액 계산
        return validBetAmount.multiply(priceProduct).multiply(bonusMultiplier);
    }

    private BigDecimal calculateGroupBetReward(List<BetHistoryResDTO> bets) {
        // 그룹 내 모든 베팅의 상태가 HIT인지 확인.
        if (bets.isEmpty() || !bets.stream().allMatch(bet -> bet.getOrderStatus() == OrderStatusEnum.HIT)) {
            return BigDecimal.ZERO; // 모든 베팅이 HIT 상태가 아니라면, 당첨금액은 0입니다.
        }

        // 그룹 내 모든 가격의 곱을 계산.
        BigDecimal priceProduct = bets.stream()
                .map(bet -> new BigDecimal(bet.getPrice()))
                .reduce(BigDecimal.ONE, BigDecimal::multiply);

        // 첫 번째 베팅 금액을 확인. (이미 모든 베팅이 HIT 상태임을 확인했으므로 첫 번째 베팅으로 계산)
        BigDecimal betAmount = new BigDecimal(bets.get(0).getBet());

        // 모든 베팅의 foldType은 동일.
        BetFoldTypeEnum betFoldType = bets.get(0).getBetFoldType();
        BigDecimal bonusMultiplier = getBonusMultiplierBasedOnBetFoldType(betFoldType);

        // 최종적으로 계산된 price와 베팅 금액을 곱.
        return betAmount.multiply(priceProduct).multiply(bonusMultiplier);
    }



    private BigDecimal getBonusMultiplierBasedOnBetFoldType(BetFoldTypeEnum betFoldType) {
        switch (betFoldType) {
            case THREE_FOLDER: return BigDecimal.valueOf(1.03);
            case FIVE_FOLDER: return BigDecimal.valueOf(1.05);
            case SEVEN_FOLDER: return BigDecimal.valueOf(1.07);
            default: return BigDecimal.ONE;
        }
    }



    public Boolean validateBetGroupId(Long betGroupId) {
        List<BetHistory> betHistory = betHistoryRepository.findByBetGroupId(betGroupId);
        if (betHistory.size() >= 1) {
            return true;
        } return false;
    }



    private BetHistoryResDTO convertToDto(BetHistory betHistory) {
        BetHistoryResDTO dto = new BetHistoryResDTO();
        BeanUtils.copyProperties(betHistory, dto);
        if (betHistory.getUser() != null) {
            dto.setUserId(betHistory.getUser().getId());
            dto.setUsername(betHistory.getUser().getUsername());
            dto.setNickname(betHistory.getUser().getNickname());
        }
        return dto;
    }
}
