package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.LoginStatistic;
import GInternational.server.api.repository.LoginStatisticRepository;

import GInternational.server.api.dto.LoginStatisticDTO;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LoginStatisticService {

    private final LoginStatisticRepository loginStatisticRepository;

    /**
     * 로그인 발생 시 로그인 통계 레코드를 생성.
     */
    public void recordLogin() {
        LoginStatistic loginStatistic = new LoginStatistic();
        loginStatistic.setDate(LocalDate.now());
        loginStatistic.setVisitCount(1);
        loginStatisticRepository.save(loginStatistic);
    }

    /**
     * 충전 발생 시 충전 통계 레코드를 생성.
     */
    public void recordRecharge() {
        LoginStatistic loginStatistic = new LoginStatistic();
        loginStatistic.setDate(LocalDate.now());
        loginStatistic.setRechargedCount(1);
        loginStatisticRepository.save(loginStatistic);
    }

    /**
     * 환전 발생 시 환전 통계 레코드를 생성.
     */
    public void recordExchange() {
        LoginStatistic loginStatistic = new LoginStatistic();
        loginStatistic.setDate(LocalDate.now());
        loginStatistic.setExchangeCount(1);
        loginStatisticRepository.save(loginStatistic);
    }

    /**
     * 회원가입 발생 시 회원가입 통계 레코드를 생성.
     */
    public void recordCreateUser() {
        LoginStatistic loginStatistic = new LoginStatistic();
        loginStatistic.setDate(LocalDate.now());
        loginStatistic.setCreateUserCount(1);
        loginStatisticRepository.save(loginStatistic);
    }

    /**
     * 베팅 발생 시 베팅 참여 통계 레코드를 생성.
     *
     * @param aasId 사용자의 AAS ID
     */
    public void recordDebitParticipant(int aasId) {
        LocalDate today = LocalDate.now();
        Set<Integer> existingAasIds = loginStatisticRepository.findDistinctAasIdByDate(today);

        if (!existingAasIds.contains(aasId)) {
            LoginStatistic loginStatistic = new LoginStatistic();
            loginStatistic.setDate(today);
            loginStatistic.setDebitCount(1);
            loginStatistic.setAasId(aasId);
            loginStatisticRepository.save(loginStatistic);
        }
    }

    /**
     * 지정된 날짜 범위의 모든 통계 데이터 조회.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param principalDetails 요청한 사용자의 상세 정보
     * @return List<LoginStatisticDTO> 조회된 통계 데이터 목록
     */
    public List<LoginStatisticDTO> getAllStatisticsForDateRange(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        return loginStatisticRepository.findAllStatisticsByDateRange(startDate, endDate);
    }
}
