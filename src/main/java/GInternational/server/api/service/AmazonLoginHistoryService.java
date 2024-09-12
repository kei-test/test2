package GInternational.server.api.service;


import GInternational.server.api.dto.AmazonLoginHistoryDTO;
import GInternational.server.api.dto.LoginRequestDto;
import GInternational.server.api.entity.AmazonLoginHistory;
import GInternational.server.api.mapper.AmazonLoginHistoryMapper;
import GInternational.server.api.repository.AmazonLoginHistoryRepository;
import GInternational.server.security.auth.PrincipalDetails;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public void saveAmazonLoginHistory(LoginRequestDto loginRequestDto, String attemptIP, String attemptNickname, String result, String gubun) {
        AmazonLoginHistory amazonLoginHistory = new AmazonLoginHistory();
        amazonLoginHistory.setAttemptUsername(loginRequestDto.getUsername());

        // 유저의 닉네임 정보가 있을 때만 저장
        if (attemptNickname != null) {
            amazonLoginHistory.setAttemptNickname(attemptNickname);
        }

        amazonLoginHistory.setAttemptPassword(loginRequestDto.getPassword());
        amazonLoginHistory.setAttemptIP(attemptIP);
        amazonLoginHistory.setResult(result);
        amazonLoginHistory.setGubun(gubun);

        amazonLoginHistoryRepository.save(amazonLoginHistory);
    }

    public List<AmazonLoginHistoryDTO> getAllAmazonLoginHistory(PrincipalDetails principalDetails, LocalDateTime startDateTime, LocalDateTime endDateTime, String username, String nickname) {
        Specification<AmazonLoginHistory> spec = Specification.where(null);

        if (username != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("attemptUsername"), username));
        }

        if (nickname != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("attemptNickname"), nickname));
        }

        if (startDateTime != null && endDateTime != null) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.between(root.get("attemptDate"), startDateTime, endDateTime));
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "attemptDate");

        List<AmazonLoginHistory> results = amazonLoginHistoryRepository.findAll(spec, sort);

        return results.stream()
                .map(amazonLoginHistoryMapper::toDto)
                .collect(Collectors.toList());
    }
}