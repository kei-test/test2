package GInternational.server.amzn.repo;


import GInternational.server.amzn.dto.indi.business.AmznPartnerWalletDTO;
import GInternational.server.amzn.dto.indi.calculate.UserBetweenCalculateDTO;
import GInternational.server.amzn.dto.indi.calculate.UserCasinoMoneyDTO;
import GInternational.server.amzn.dto.indi.indi_prj.*;
import GInternational.server.amzn.dto.indi.indi_response.AmznIndiPartnerResDTO;
import GInternational.server.amzn.dto.indi.indi_response.AmznKplayResDTO;
import GInternational.server.amzn.dto.indi.indi_response.AmznSportResDTO;
import GInternational.server.amzn.dto.indi.indi_response.AmznUserResDTO;
import GInternational.server.amzn.service.AmznIndiService;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.kplay.debit.entity.QDebit;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static GInternational.server.api.entity.QAmazonExchangeTransaction.amazonExchangeTransaction;
import static GInternational.server.api.entity.QAmazonRechargeTransaction.amazonRechargeTransaction;
import static GInternational.server.api.entity.QAmazonRollingTransaction.*;
import static GInternational.server.api.entity.QBetHistory.betHistory;
import static GInternational.server.api.entity.QExchangeTransaction.exchangeTransaction;
import static GInternational.server.api.entity.QRechargeTransaction.rechargeTransaction;
import static GInternational.server.api.entity.QUser.user;
import static GInternational.server.api.entity.QWallet.wallet;
import static GInternational.server.kplay.credit.entity.QCredit.credit;
import static GInternational.server.kplay.debit.entity.QDebit.*;
import static GInternational.server.kplay.debit.entity.QDebit.debit;


@Repository
@RequiredArgsConstructor
public class AmznIndiRepositoryImpl {


    private final JPAQueryFactory queryFactory;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());




    public UserBetweenCalculateDTO getUserCalculate(Long userId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);

        UserCasinoMoneyDTO casinoMoneyAndUserInfo = queryFactory.select(Projections.constructor(UserCasinoMoneyDTO.class,
                        user.id,
                        user.aasId,
                        wallet.casinoBalance))
                .from(user)
                .leftJoin(wallet).on(wallet.user.id.eq(user.id))
                .where(user.id.eq(userId))
                .fetchOne();

        int aas = casinoMoneyAndUserInfo.getAas();

        AmznRechargeDTO recharges = queryFactory.select(Projections.constructor(AmznRechargeDTO.class,
                        user.id,
                        rechargeTransaction.rechargeAmount.sum()))
                .from(user)
                .leftJoin(rechargeTransaction).on(rechargeTransaction.user.id.eq(user.id))
                .where(rechargeTransaction.user.id.eq(userId)
                        .and(rechargeTransaction.processedAt.between(startOfDay,endOfDay))
                        .and(rechargeTransaction.status.eq(TransactionEnum.valueOf("APPROVAL"))))
                .fetchOne();

        AmznExchangeDTO exchange = queryFactory.select(Projections.constructor(AmznExchangeDTO.class,
                        user.id,
                        exchangeTransaction.exchangeAmount.sum()))
                .from(user)
                .leftJoin(exchangeTransaction).on(exchangeTransaction.user.id.eq(user.id))
                .where(exchangeTransaction.user.id.eq(userId)
                        .and(exchangeTransaction.processedAt.between(startOfDay,endOfDay))
                        .and(exchangeTransaction.status.eq(TransactionEnum.valueOf("APPROVAL"))))
                .fetchOne();


        AmznDebit debitTransaction = queryFactory.select(Projections.constructor(AmznDebit.class,
                        debit.amount.sum()))
                .from(user)
                .leftJoin(debit).on(debit.user_id.eq(aas))
                .where(debit.createdAt.between(startOfDay,endOfDay)
                        .and(debit.prd_id.between(200,299)
                                .or(debit.prd_id.between(1,99))))
                .fetchOne();

        AmznCredit creditTransaction = queryFactory.select(Projections.constructor(AmznCredit.class,
                        credit.amount.sum()))
                .from(user)
                .leftJoin(credit).on(credit.user_id.eq(aas))
                .where(credit.is_cancel.notIn(1)
                        .and(credit.amount.notIn(0))
                        .and(credit.createdAt.between(startOfDay,endOfDay))
                        .and(credit.prd_id.between(200,299)
                                .or(credit.prd_id.between(1,99))))
                .fetchOne();

        UserBetweenCalculateDTO responseDTO = new UserBetweenCalculateDTO();
        responseDTO.setUserId(userId);
        responseDTO.setTotalBetAmount((long) debitTransaction.getAmount());
        responseDTO.setTotalWinningAmount((long) creditTransaction.getAmount());
        responseDTO.setTotalBetSettlement((long) debitTransaction.getAmount() - (long) creditTransaction.getAmount());
        responseDTO.setCasinoMoney(casinoMoneyAndUserInfo.getCasinoMoney());
        responseDTO.setTotalRechargeAmount(recharges.getRechargeAmount());
        responseDTO.setTotalExchangeAmount(exchange.getExchangeAmount());
        responseDTO.setTotalWalletSettlement(recharges.getRechargeAmount() - exchange.getExchangeAmount());

        return responseDTO;
    }




    public AmznPartnerWalletDTO getPartnerWallet(Long id) {
        return queryFactory.select(Projections.constructor(AmznPartnerWalletDTO.class,
                        user.id,
                        user.username,
                        wallet.amazonMoney,
                        wallet.amazonMileage))
                .from(user)
                .leftJoin(wallet).on(wallet.user.id.eq(user.id))
                .where(wallet.user.id.eq(id))
                .fetchOne();
    }



    //쿼리 오버헤드로 인해 각 테이블별로 셀렉트하되 최소갯수의 쿼리를 사용하고 스트림을 통해 그룹화하여 연산처리해야함
    //레이턴시 최대 4s -> 1300ms
    public List<AmznIndiPartnerResDTO> getResults(LocalDate startDate, LocalDate endDate) {

        //between 날짜 매개변수
        LocalDateTime startOfDay = startDate.atStartOfDay();
        LocalDateTime endOfDay = endDate.atTime(LocalTime.MAX);

        //파트너 1-1 현재 테이블에 저장된 파트너 정보와 지갑 정보를 가져온다
        List<AmznIndiPartnerResDTO> partnerInfo = queryFactory.select(Projections.constructor(AmznIndiPartnerResDTO.class,
                        user.id,
                        user.partnerType,
                        user.username,
                        user.nickname,
                        user.referredBy,
                        user.aasId,
                        wallet.amazonMoney,
                        wallet.amazonMileage))
                .from(user)
                .leftJoin(wallet).on(wallet.user.id.eq(user.id))
                .where(user.daeId.isNotNull()
                        .or(user.bonId.isNotNull())
                        .or(user.buId.isNotNull())
                        .or(user.chongId.isNotNull())
                        .or(user.partnerType.eq("대본사")))
                .fetch();

        //partner username 추출
        List<String> usernames = partnerInfo.stream()
                .map(AmznIndiPartnerResDTO::getUsername)
                .collect(Collectors.toList());

        List<Long> partnerIds = partnerInfo.stream()
                .map(AmznIndiPartnerResDTO::getId)
                .collect(Collectors.toList());



        List<AmznIndiUserInfoDTO> usersInfo = queryFactory.select(Projections.constructor(AmznIndiUserInfoDTO.class,
                        user.id,
                        user.username,
                        user.nickname,
                        user.aasId,
                        user.referredBy,
                        user.partnerType,
                        user.isAmazonUser))
                .from(user)
                .where(user.referredBy.in(usernames))
                .fetch();

        //회원 리스트에서 id 추출
        List<Long> referredUser = usersInfo.stream()
                .map(AmznIndiUserInfoDTO::getId)
                .collect(Collectors.toList());

        //회원 리스트에서 aasId 추출
        List<Integer> kplayUserAasId = usersInfo.stream()
                .map(AmznIndiUserInfoDTO::getAasId)
                .collect(Collectors.toList());


        //특정 파트너의 롤링이 지급된 금액 추출 쿼리 // 총 롤링
        List<AmznRollingDTO> rollingAmounts = queryFactory.select(Projections.constructor(AmznRollingDTO.class,
                        user.id, //파트너 uid
                        amazonRollingTransaction.username,  //베팅한 유저네임
                        amazonRollingTransaction.category,
                        amazonRollingTransaction.rollingAmount))
                .from(user)
                .leftJoin(amazonRollingTransaction).on(amazonRollingTransaction.user.id.eq(user.id))
                .where(amazonRollingTransaction.user.id.in(partnerIds)
                        .and(amazonRollingTransaction.processedAt.between(startOfDay,endOfDay)))
                .fetch();



        //파트너의 충전액 추출 쿼리
        List<AmznRechargeDTO> rechargeAmounts = queryFactory.select(Projections.constructor(AmznRechargeDTO.class,
                        user.id,
                        amazonRechargeTransaction.rechargeAmount))
                .from(user)
                .leftJoin(amazonRechargeTransaction).on(amazonRechargeTransaction.user.id.eq(user.id))
                .where(amazonRechargeTransaction.user.id.in(partnerIds)
                        .and(amazonRechargeTransaction.processedAt.between(startOfDay,endOfDay)))
                .fetch();

        //파트너의 환전액 추출 쿼리
        List<AmznExchangeDTO> exchangeAmounts = queryFactory.select(Projections.constructor(AmznExchangeDTO.class,
                        user.id,
                        amazonExchangeTransaction.exchangeAmount))
                .from(user)
                .leftJoin(amazonExchangeTransaction).on(amazonExchangeTransaction.user.id.eq(user.id))
                .where(amazonExchangeTransaction.user.id.in(partnerIds)
                        .and(amazonExchangeTransaction.processedAt.between(startOfDay,endOfDay)))
                .fetch();


        //롤링 지급액
        Map<Long, Long> rollingAmountMap = rollingAmounts.stream()
                .collect(Collectors.groupingBy(
                        AmznRollingDTO::getId,
                        Collectors.summingLong(AmznRollingDTO::getRollingAmount)
                ));

        //파트너 탭
        Map<Long, Long> rechargeAmountMap = rechargeAmounts.stream()
                .collect(Collectors.groupingBy(
                        AmznRechargeDTO::getId,
                        Collectors.summingLong(AmznRechargeDTO::getRechargeAmount)
                ));

        Map<Long, Long> exchangeAmountMap = exchangeAmounts.stream()
                .collect(Collectors.groupingBy(
                        AmznExchangeDTO::getId,
                        Collectors.summingLong(AmznExchangeDTO::getExchangeAmount)
                ));

        partnerInfo.forEach(partner -> {
            Long userId = partner.getId();
            Long totalRollingAmount = rollingAmountMap.getOrDefault(userId, 0L);
            partner.setAmazonRollingAmount(totalRollingAmount);
        });

        partnerInfo.forEach(partner -> {
            Long userId = partner.getId();
            Long totalRechargeAmount = rechargeAmountMap.getOrDefault(userId, 0L);
            partner.setRechargeAmount(totalRechargeAmount);
        });

        partnerInfo.forEach(partner -> {
            Long userId = partner.getId();
            Long totalExchangeAmount = exchangeAmountMap.getOrDefault(userId, 0L);
            partner.setExchangeAmount(totalExchangeAmount);
        });

        for (AmznIndiPartnerResDTO p : partnerInfo) {
            long recharge = p.getRechargeAmount();
            long exchange = p.getExchangeAmount();
            long totalSettlement = recharge - exchange;
            p.setTotalSettlement(totalSettlement); //손인 = 충 - 환
        }


        //현재 파트너에 의해 가입된 유저의 지갑 금액정보
        List<AmznUserResDTO> userWallets = queryFactory.select(Projections.constructor(AmznUserResDTO.class,
                        user.referredBy,
                        wallet.sportsBalance,
                        wallet.point))
                .from(user)
                .leftJoin(wallet).on(wallet.user.id.eq(user.id))
                .where(wallet.user.id.in(referredUser))
                .fetch();


        //상위 파트너를 추천인으로 가입한 유저들의 입금내역
        //입금테이블 셀렉트
        List<AmznUserRechargeDTO> userRecharges = queryFactory.select(Projections.constructor(AmznUserRechargeDTO.class,
                        user.id,
                        user.referredBy,
                        rechargeTransaction.rechargeAmount))
                .from(user)
                .leftJoin(rechargeTransaction).on(rechargeTransaction.user.id.eq(user.id))
                .where(rechargeTransaction.user.id.in(referredUser)
                        .and(rechargeTransaction.processedAt.between(startOfDay,endOfDay)))
                .fetch();


        List<AmznUserExchangeDTO> userExchanges = queryFactory.select(Projections.constructor(AmznUserExchangeDTO.class,
                        user.id,
                        user.referredBy,
                        exchangeTransaction.exchangeAmount))
                .from(user)
                .leftJoin(exchangeTransaction).on(exchangeTransaction.user.id.eq(user.id))
                .where(exchangeTransaction.user.id.in(referredUser)
                        .and(exchangeTransaction.processedAt.between(startOfDay,endOfDay)))
                .fetch();


        //스포츠머니를 추천인으로 그룹화하여 누적합계 구함
        Map<String, Long> userWalletAmountMap = userWallets.stream()
                .collect(Collectors.groupingBy(
                        AmznUserResDTO::getReferredBy,
                        Collectors.summingLong(AmznUserResDTO::getSportsBalance)
                ));

        //포인트를 추천인으로 그룹화하여 누적합계 구함
        Map<String, Long> userPointAmountMap = userWallets.stream()
                .collect(Collectors.groupingBy(
                        AmznUserResDTO::getReferredBy,
                        Collectors.summingLong(AmznUserResDTO::getPoint)
                ));

        //입금액을 추천인으로 그룹화하여 누적합계 구함
        Map<String, Long> userRechargeAmountMap = userRecharges.stream()
                .collect(Collectors.groupingBy(
                        AmznUserRechargeDTO::getReferredBy,
                        Collectors.summingLong(AmznUserRechargeDTO::getRechargeAmount)
                ));

        //환전액을 추천인으로 그룹화하여 누적합계 구함
        Map<String, Long> userExchangeAmountMap = userExchanges.stream()
                .collect(Collectors.groupingBy(
                        AmznUserExchangeDTO::getReferredBy,
                        Collectors.summingLong(AmznUserExchangeDTO::getExchangeAmount)
                ));



        userWallets.forEach(userTap -> {
            String referredBy = userTap.getReferredBy();
            long totalSportsBalance = userWalletAmountMap.getOrDefault(referredBy, 0L);
            long totalPoint = userPointAmountMap.getOrDefault(referredBy, 0L);
            long totalRecharge = userRechargeAmountMap.getOrDefault(referredBy, 0L);
            long totalExchange = userExchangeAmountMap.getOrDefault(referredBy, 0L);
            long totalSettlement = totalRecharge - totalExchange;
            userTap.setSportsBalance(totalSportsBalance);
            userTap.setPoint(totalPoint);
            userTap.setRechargeAmount(totalRecharge);
            userTap.setExchangeAmount(totalExchange);
            userTap.setTotalSettlement(totalSettlement);
        });

        partnerInfo.forEach(partner -> {
            String username = partner.getUsername();
            Optional<AmznUserResDTO> matchingUser = userWallets.stream()
                    .filter(user -> user.getReferredBy().equals(username))
                    .findFirst();
            matchingUser.ifPresent(partner::setUsers);
        });


        //슬롯 200 ~ 299
        //카지노 1 ~ 99
        //아케이드(미니게임) 10002 ~ 10003 and 300 ~ 301

        //외래키에 대한 조건만 갖고 해당 테이블 셀렉트
        //이후 스트림으로 필터링하여 처리한다
        List<AmznKplayResDTO> debits = queryFactory.select(Projections.constructor(AmznKplayResDTO.class,
                        user.referredBy,
                        debit.prd_id,
                        debit.amount))
                .from(user)
                .leftJoin(debit).on(debit.user_id.in(kplayUserAasId))
                .where(debit.createdAt.between(startOfDay,endOfDay))
                .fetch();



        List<AmznCredit> credits = queryFactory.select(Projections.constructor(AmznCredit.class,
                        user.referredBy,
                        credit.prd_id,
                        credit.amount,
                        credit.is_cancel))
                .from(user)
                .leftJoin(credit).on(credit.user_id.in(kplayUserAasId))
                .where(credit.createdAt.between(startOfDay,endOfDay)
                        .and(credit.amount.notIn(0))
                        .and(credit.is_cancel.notIn(1)))
                .fetch();



        //슬롯 스트림
        Map<String, Long> userDebitAmountMap = debits.stream()
                .filter(slot -> slot.getPrdId() >= 200 && slot.getPrdId() <= 299)
                .collect(Collectors.groupingBy(
                        AmznKplayResDTO::getReferredBy,
                        Collectors.summingLong(AmznKplayResDTO::getDebitAmount)
                ));

        Map<String, Long> userCreditAmountMap = credits.stream()
                .filter(credit -> credit.getPrdId() >= 200 && credit.getPrdId() <= 299)
                .collect(Collectors.groupingBy(
                        AmznCredit::getReferredBy,
                        Collectors.summingLong(AmznCredit::getAmount)
                ));

        Map<Long, Long> slotRollingAmountMap = rollingAmounts.stream()
                .filter(rollingAmount -> rollingAmount.getCategory().equals("슬롯"))
                .collect(Collectors.groupingBy(
                        AmznRollingDTO::getId,
                        Collectors.summingLong(AmznRollingDTO::getRollingAmount)
                ));




        debits.forEach(slotTap -> {
            String referredBy = slotTap.getReferredBy();
            long totalDebitAmount = userDebitAmountMap.getOrDefault(referredBy, 0L);
            long totalCreditAmount = userCreditAmountMap.getOrDefault(referredBy, 0L);
            long totalRollingAmount = slotRollingAmountMap.getOrDefault(referredBy, 0L);
            long totalSettlement = totalDebitAmount - totalCreditAmount;
            slotTap.setDebitAmount((int) totalDebitAmount);
            slotTap.setCreditAmount(totalCreditAmount);
            slotTap.setPartnerRollingAmount(totalRollingAmount);
            slotTap.setTotalSettlement(totalSettlement);
        });

        partnerInfo.forEach(partner -> {
            String username = partner.getUsername();
            Optional<AmznKplayResDTO> matchingSlot = debits.stream()
                    .filter(user -> user.getReferredBy().equals(username))
                    .findFirst();
            matchingSlot.ifPresent(partner::setSlot);
        });


        //카지노 스트림
        Map<String, Long> userCasinoDebitAmountMap = debits.stream()
                .filter(slot -> slot.getPrdId() >= 1 && slot.getPrdId() <= 99)
                .collect(Collectors.groupingBy(
                        AmznKplayResDTO::getReferredBy,
                        Collectors.summingLong(AmznKplayResDTO::getDebitAmount)
                ));

        Map<String, Long> userCasinoCreditAmountMap = credits.stream()
                .filter(credit -> credit.getPrdId() >= 1 && credit.getPrdId() <= 99)
                .collect(Collectors.groupingBy(
                        AmznCredit::getReferredBy,
                        Collectors.summingLong(AmznCredit::getAmount)
                ));

        Map<Long, Long> casinoRollingAmountMap = rollingAmounts.stream()
                .filter(rollingAmount -> rollingAmount.getCategory().equals("카지노"))
                .collect(Collectors.groupingBy(
                        AmznRollingDTO::getId,
                        Collectors.summingLong(AmznRollingDTO::getRollingAmount)
                ));





        debits.forEach(casinoTap -> {
            String referredBy = casinoTap.getReferredBy();
            long totalDebitAmount = userCasinoDebitAmountMap.getOrDefault(referredBy, 0L);
            long totalCreditAmount = userCasinoCreditAmountMap.getOrDefault(referredBy, 0L);
            long totalRollingAmount = casinoRollingAmountMap.getOrDefault(referredBy, 0L);
            long totalSettlement = totalDebitAmount - totalCreditAmount;
            casinoTap.setDebitAmount((int) totalDebitAmount);
            casinoTap.setCreditAmount(totalCreditAmount);
            casinoTap.setPartnerRollingAmount(totalRollingAmount);
            casinoTap.setTotalSettlement(totalSettlement);
        });

        partnerInfo.forEach(partner -> {
            String username = partner.getUsername();
            Optional<AmznKplayResDTO> matchingCasino = debits.stream()
                    .filter(user -> user.getReferredBy().equals(username))
                    .findFirst();
            matchingCasino.ifPresent(partner::setCasino);
        });


        //아케이드 스트림
        Map<String, Long> userArcadeDebitAmountMap = debits.stream()
                .filter(arcade -> arcade.getPrdId() >= 10002 && arcade.getPrdId() <= 10003 || arcade.getPrdId() >= 300 && arcade.getPrdId() <= 301)
                .collect(Collectors.groupingBy(
                        AmznKplayResDTO::getReferredBy,
                        Collectors.summingLong(AmznKplayResDTO::getDebitAmount)
                ));

        Map<String, Long> userArcadeCreditAmountMap = credits.stream()
                .filter(credit -> credit.getPrdId() >= 1 && credit.getPrdId() <= 99)
                .collect(Collectors.groupingBy(
                        AmznCredit::getReferredBy,
                        Collectors.summingLong(AmznCredit::getAmount)
                ));

        Map<Long, Long> arcadeRollingAmountMap = rollingAmounts.stream()
                .filter(rollingAmount -> rollingAmount.getCategory().equals("아케이드"))
                .collect(Collectors.groupingBy(
                        AmznRollingDTO::getId,
                        Collectors.summingLong(AmznRollingDTO::getRollingAmount)
                ));


        debits.forEach(arcadeTap -> {
            String referredBy = arcadeTap.getReferredBy();
            long totalDebitAmount = userArcadeDebitAmountMap.getOrDefault(referredBy, 0L);
            long totalCreditAmount = userArcadeCreditAmountMap.getOrDefault(referredBy, 0L);
            long totalRollingAmount = arcadeRollingAmountMap.getOrDefault(referredBy, 0L);
            long totalSettlement = totalDebitAmount - totalCreditAmount;
            arcadeTap.setDebitAmount((int) totalDebitAmount);
            arcadeTap.setCreditAmount(totalCreditAmount);
            arcadeTap.setPartnerRollingAmount(totalRollingAmount);
            arcadeTap.setTotalSettlement(totalSettlement);
        });

        partnerInfo.forEach(partner -> {
            String username = partner.getUsername();
            Optional<AmznKplayResDTO> matchingCasino = debits.stream()
                    .filter(user -> user.getReferredBy().equals(username))
                    .findFirst();
            matchingCasino.ifPresent(partner::setArcade);
        });


        //중복되는 그룹아이디 제거
        List<Long> distinctBetGroupIds = queryFactory.select(Projections.constructor(Long.class,
                        betHistory.betGroupId))
                .from(betHistory)
                .where(betHistory.betStartTime.between(startOfDay,endOfDay)
                        .and(betHistory.user.id.in(referredUser)))
                .distinct()
                .fetch();

        //스포츠 베팅
        List<AmznSportResDTO> betLists = queryFactory.select(Projections.constructor(AmznSportResDTO.class,
                        user.referredBy,
                        betHistory.bet,
                        betHistory.betReward))
                .from(user)
                .leftJoin(betHistory).on(betHistory.user.id.eq(user.id))
                .where(betHistory.betGroupId.in(distinctBetGroupIds))
                .fetch();


        //금액 필드 형변환
        List<AmznSportResDTO> cvtLists = new ArrayList<>();
        for (AmznSportResDTO cvtObj : betLists) {
            long cvtBet = Long.parseLong(cvtObj.getBetAmount());
            long cvtReward = Long.parseLong(cvtObj.getBetReward());
            cvtObj.setCvtBetAmount(cvtBet);
            cvtObj.setCvtBetReward(cvtReward);
            cvtLists.add(cvtObj);
        }


        Map<String, Long> sportsBetAmountMap = cvtLists.stream()
                .collect(Collectors.groupingBy(
                        AmznSportResDTO::getReferredBy,
                        Collectors.summingLong(AmznSportResDTO::getCvtBetAmount)
                ));


        Map<String, Long> sportsBetRewardMap = cvtLists.stream()
                .collect(Collectors.groupingBy(
                        AmznSportResDTO::getReferredBy,
                        Collectors.summingLong(AmznSportResDTO::getCvtBetReward)
                ));


        Map<Long, Long> sportsRollingAmountMap = rollingAmounts.stream()
                .filter(rollingAmount -> rollingAmount.getCategory().equals("스포츠"))
                .collect(Collectors.groupingBy(
                        AmznRollingDTO::getId,
                        Collectors.summingLong(AmznRollingDTO::getRollingAmount)
                ));


        cvtLists.forEach(sportsTap -> {
            String referredBy = sportsTap.getReferredBy();
            long totalBetAmount = sportsBetAmountMap.getOrDefault(referredBy, 0L);
            long totalBetReward = sportsBetRewardMap.getOrDefault(referredBy, 0L);
            long totalRollingAmount = sportsRollingAmountMap.getOrDefault(referredBy, 0L);
            long totalSettlement = totalBetAmount - totalBetReward;
            sportsTap.setCvtBetAmount(totalBetAmount);
            sportsTap.setCvtBetReward(totalBetReward);
            sportsTap.setPartnerRollingAmount(totalRollingAmount);
            sportsTap.setTotalSettlement(totalSettlement);
        });

        partnerInfo.forEach(partner -> {
            String username = partner.getUsername();
            Optional<AmznSportResDTO> matchingCasino = cvtLists.stream()
                    .filter(user -> user.getReferredBy().equals(username))
                    .findFirst();
            matchingCasino.ifPresent(partner::setSport);
        });

        return partnerInfo;
    }



}
