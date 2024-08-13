package GInternational.server.api.service;


import GInternational.server.api.dto.AmazonLoginHistoryDTO;
import GInternational.server.api.dto.LoginRequestDto;
import GInternational.server.api.entity.AmazonLoginHistory;
import GInternational.server.api.mapper.AmazonLoginHistoryMapper;
import GInternational.server.api.repository.AmazonLoginHistoryRepository;
import GInternational.server.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonLoginHistoryService {

    private final AmazonLoginHistoryRepository amazonLoginHistoryRepository;
    private final AmazonLoginHistoryMapper amazonLoginHistoryMapper;

    /**
     * 로그인 시도 정보를 저장합니다.
     *
     * @param loginRequestDto 로그인 요청 정보
     * @param attemptIP 시도한 IP 주소
     * @param attemptNickname 시도한 사용자의 닉네임
     */
    public void saveAmazonLoginHistory(LoginRequestDto loginRequestDto, String attemptIP, String attemptNickname) {
        AmazonLoginHistory amazonLoginHistory = new AmazonLoginHistory();
        amazonLoginHistory.setAttemptUsername(loginRequestDto.getUsername());

        // 유저의 닉네임 정보가 있을 때만 저장
        if (attemptNickname != null) {
            amazonLoginHistory.setAttemptNickname(attemptNickname);
        }

        amazonLoginHistory.setAttemptPassword(loginRequestDto.getPassword());
        amazonLoginHistory.setAttemptIP(attemptIP);

        amazonLoginHistoryRepository.save(amazonLoginHistory);
    }

    /**
     * 모든 로그인 이력을 조회.
     *
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 로그인 이력 목록
     */
    public List<AmazonLoginHistoryDTO> getAllAmazonLoginHistory(PrincipalDetails principalDetails) {
        Sort sort = Sort.by(Sort.Direction.DESC, "attemptDate");
        return amazonLoginHistoryRepository.findAll(sort)
                .stream()
                .map(amazonLoginHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 사용자명을 기준으로 로그인 이력을 조회.
     *
     * @param username 사용자명
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 로그인 이력 목록
     */
    public List<AmazonLoginHistoryDTO> getAmazonLoginHistoryByUsername(String username, PrincipalDetails principalDetails) {
        return amazonLoginHistoryRepository.findByAttemptUsername(username)
                .stream()
                .map(amazonLoginHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 닉네임을 기준으로 로그인 이력을 조회.
     *
     * @param nickname 닉네임
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 로그인 이력 목록
     */
    public List<AmazonLoginHistoryDTO> getAmazonLoginHistoryByNickname(String nickname, PrincipalDetails principalDetails) {
        return amazonLoginHistoryRepository.findByAttemptNickname(nickname)
                .stream()
                .map(amazonLoginHistoryMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * 특정 날짜 범위 내의 로그인 이력을 조회.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param principalDetails 현재 인증된 사용자의 상세 정보
     * @return 로그인 이력 목록
     */
    public List<AmazonLoginHistoryDTO> getAmazonLoginHistoryByDateRange(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        return amazonLoginHistoryRepository.findByAttemptDateBetween(startDateTime, endDateTime)
                .stream()
                .map(amazonLoginHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}