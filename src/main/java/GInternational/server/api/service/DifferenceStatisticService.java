package GInternational.server.api.service;

import GInternational.server.api.dto.DifferenceStatisticAccountRequestDTO;
import GInternational.server.api.dto.DifferenceStatisticRequestDTO;
import GInternational.server.api.entity.DifferenceStatistic;
import GInternational.server.api.entity.DifferenceStatisticAccount;
import GInternational.server.api.repository.*;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.ManagementAccountEnum;
import GInternational.server.api.vo.OrderStatusEnum;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class DifferenceStatisticService {

    private final DifferenceStatisticRepository differenceStatisticRepository;
    private final WalletRepository walletRepository;
    private final BetHistoryRepository betHistoryRepository;
    private final UserRepository userRepository;
    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final ExchangeRepository exchangeRepository;
    private final DifferenceStatisticAccountRepository differenceStatisticAccountRepository;

    // 계좌 정보 추가
    public void addTempSavedAccount(DifferenceStatisticAccountRequestDTO accountRequestDTO, PrincipalDetails principalDetails) {
        DifferenceStatisticAccount account = new DifferenceStatisticAccount();
        account.setTurn(accountRequestDTO.getTurn());
        account.setUsage(accountRequestDTO.getUsage());
        account.setOwnerName(accountRequestDTO.getOwnerName());
        account.setNumber(accountRequestDTO.getNumber());
        account.setSource(accountRequestDTO.getSource());
        account.setIsUse(accountRequestDTO.getIsUse());
        account.setTransferLimit(accountRequestDTO.getTransferLimit());
        account.setCurrentMoney(accountRequestDTO.getCurrentMoney());
        account.setMemo(accountRequestDTO.getMemo());
        account.setCreatedAt(LocalDateTime.now());
        differenceStatisticAccountRepository.save(account);
    }

    // 계좌 정보 조회
    public List<DifferenceStatisticAccountRequestDTO> getTempSavedAccounts(PrincipalDetails principalDetails) {
        return differenceStatisticAccountRepository.findAll().stream().map(account -> {
            DifferenceStatisticAccountRequestDTO dto = new DifferenceStatisticAccountRequestDTO();
            dto.setAccountId(account.getId());
            dto.setTurn(account.getTurn());
            dto.setUsage(account.getUsage());
            dto.setOwnerName(account.getOwnerName());
            dto.setNumber(account.getNumber());
            dto.setSource(account.getSource());
            dto.setIsUse(account.getIsUse());
            dto.setTransferLimit(account.getTransferLimit());
            dto.setCurrentMoney(account.getCurrentMoney());
            dto.setMemo(account.getMemo());
            dto.setCreatedAt(account.getCreatedAt());
            dto.setUpdatedAt(account.getUpdatedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    // 계좌 정보 업데이트
    public void updateAccount(DifferenceStatisticAccountRequestDTO updatedAccount, PrincipalDetails principalDetails) {
        DifferenceStatisticAccount account = differenceStatisticAccountRepository.findById(updatedAccount.getAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        if (updatedAccount.getTurn() != null) {
            account.setTurn(updatedAccount.getTurn());
        }
        if (updatedAccount.getUsage() != null) {
            account.setUsage(updatedAccount.getUsage());
        }
        if (updatedAccount.getOwnerName() != null) {
            account.setOwnerName(updatedAccount.getOwnerName());
        }
        if (updatedAccount.getNumber() != null) {
            account.setNumber(updatedAccount.getNumber());
        }
        if (updatedAccount.getSource() != null) {
            account.setSource(updatedAccount.getSource());
        }
        if (updatedAccount.getIsUse() != null) {
            account.setIsUse(updatedAccount.getIsUse());
        }
        if (updatedAccount.getTransferLimit() != null) {
            account.setTransferLimit(updatedAccount.getTransferLimit());
        }
        if (updatedAccount.getCurrentMoney() != null) {
            account.setCurrentMoney(updatedAccount.getCurrentMoney());
        }
        if (updatedAccount.getMemo() != null) {
            account.setMemo(updatedAccount.getMemo());
        }

        account.setUpdatedAt(LocalDateTime.now());
        differenceStatisticAccountRepository.save(account);
    }

    // 계좌 정보 삭제
    public void deleteAccount(Long accountId, PrincipalDetails principalDetails) {
        differenceStatisticAccountRepository.deleteById(accountId);
    }

    // 세션에서 임시 저장된 계좌 정보와 차액값들을 실제 데이터베이스에 저장하는 메서드
    // 이 메서드는 사용자가 '저장'을 클릭할 때 호출됩니다.
    public void saveAccountsFromSessionToDatabase(DifferenceStatisticRequestDTO requestDTO, PrincipalDetails principalDetails) {
        List<DifferenceStatisticAccount> accounts = differenceStatisticAccountRepository.findAll();

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfNow = LocalDateTime.now();

        // 앞방, 중간방, 뒷방에 따른 현재 보유금 합계 계산
        long frontAccountSum = accounts.stream()
                .filter(a -> ManagementAccountEnum.앞방.equals(a.getUsage()))
                .mapToLong(DifferenceStatisticAccount::getCurrentMoney)
                .sum();
        long middleAccountSum = accounts.stream()
                .filter(a -> ManagementAccountEnum.중간방.equals(a.getUsage()))
                .mapToLong(DifferenceStatisticAccount::getCurrentMoney)
                .sum();
        long backAccountSum = accounts.stream()
                .filter(a -> ManagementAccountEnum.뒷방.equals(a.getUsage()))
                .mapToLong(DifferenceStatisticAccount::getCurrentMoney)
                .sum();

        // Wallet에서 모든 유저의 sportsBalance와 point 합계 조회
        Long totalSportsBalance = Optional.ofNullable(walletRepository.sumAllSportsBalance()).orElse(0L);
        Long totalPoint = Optional.ofNullable(walletRepository.sumAllPoint()).orElse(0L);

        // 오늘의 OrderStatusEnum.WAITING 상태인 총 bet 합계 조회
        Long totalBetForWaiting = betHistoryRepository.sumTodayTotalBetForWaitingStatus(startOfDay, endOfNow, OrderStatusEnum.WAITING.getValue());

        // 전체 유저 수 조회
        Long totalUserCount = userRepository.countByRole("ROLE_USER");

        // 오늘의 충전 금액 합계 조회
        Long totalRecharge = Optional.ofNullable(rechargeTransactionRepository.sumRechargeAmountByUserRolesAndProcessedAtBetweenAndStatus(
                "ROLE_USER", startOfDay, endOfNow, TransactionEnum.APPROVAL)).orElse(0L);

        // 커미션 계산: wonExchange * commissionPercent / 100
        long commission = (long) (requestDTO.getWonExchange() * requestDTO.getCommissionPercent() / 100.0);

        // 오늘의 환전 금액 합계 조회
        Long totalExchange = Optional.ofNullable(exchangeRepository.sumExchangeAmountByUserRolesAndProcessedAtBetweenAndStatus(
                "ROLE_USER", startOfDay, endOfNow, TransactionEnum.APPROVAL)).orElse(0L);

        long difference = (frontAccountSum + middleAccountSum + backAccountSum) - (totalSportsBalance + totalPoint);

        // DifferenceStatistic 엔티티 생성 및 값 설정
        DifferenceStatistic differenceStatistic = new DifferenceStatistic();
        differenceStatistic.setBigo(requestDTO.getBigo());
        differenceStatistic.setOperatingExpense(requestDTO.getOperatingExpense());
        differenceStatistic.setWonExchange(requestDTO.getWonExchange());
        differenceStatistic.setCommission(commission);
        differenceStatistic.setDongExchange(requestDTO.getDongExchange());

        differenceStatistic.setFrontAccount(frontAccountSum);
        differenceStatistic.setMiddleAccount(middleAccountSum);
        differenceStatistic.setBackAccount(backAccountSum);
        differenceStatistic.setTotalAccount(frontAccountSum + middleAccountSum + backAccountSum);
        differenceStatistic.setCurrentSportsBalance(totalSportsBalance != null ? totalSportsBalance : 0);
        differenceStatistic.setCurrentPoint(totalPoint != null ? totalPoint : 0);
        differenceStatistic.setTotalBet(totalBetForWaiting != null ? totalBetForWaiting : 0);
        differenceStatistic.setUserCount(totalUserCount != null ? totalUserCount : 0);
        differenceStatistic.setTotalRecharge(totalRecharge);
        differenceStatistic.setTotalExchange(totalExchange);
        differenceStatistic.setSubtract(totalRecharge - totalExchange);
        differenceStatistic.setDifference(difference);
        differenceStatistic.setCreatedAt(LocalDateTime.now());
        differenceStatistic.setAccounts(new ArrayList<>(accounts));

        // 데이터베이스에 저장
        differenceStatisticRepository.save(differenceStatistic);
    }

    // 날짜 기준으로 DifferenceStatistic 목록 조회
    public List<DifferenceStatistic> findDifferenceStatisticsByDate(LocalDate date, PrincipalDetails principalDetails) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.plusDays(1).atStartOfDay();

        return differenceStatisticRepository.findByCreatedAtBetween(startOfDay, endOfDay);
    }

    public void deleteDifferenceStatisticById(Long id, PrincipalDetails principalDetails) {
        differenceStatisticRepository.deleteById(id);
    }
}
