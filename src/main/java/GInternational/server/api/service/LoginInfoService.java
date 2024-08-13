package GInternational.server.api.service;

import GInternational.server.api.dto.LoginInfoResponseDTO;
import GInternational.server.api.dto.UserLoginCountDTO;
import GInternational.server.api.entity.LoginInfo;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.LoginInfoRepository;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class LoginInfoService {

    private final LoginInfoRepository loginInfoRepository;
    private final UserRepository userRepository;

    /**
     * 로그인 정보 기록.
     *
     * @param username 유저 ID
     * @param nickname 닉네임
     * @param distributor 총판
     * @param accessedIp 접속 IP
     * @param accessedDevice 접속 기기 (P, M)
     */
    public void saveLoginInfo(String username, String nickname, String distributor, String store, String accessedIp, String accessedDevice) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다.");
        }

        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setUser(user);
        loginInfo.setUsername(username);
        loginInfo.setNickname(nickname);
        loginInfo.setDistributor(distributor);
        loginInfo.setStore(store);
        loginInfo.setAccessedIp(accessedIp);
        loginInfo.setAccessedDevice(accessedDevice);
        loginInfo.setLastVisit(LocalDateTime.now());
        loginInfoRepository.save(loginInfo);
    }

    /**
     * 조건 및 날짜 범위에 따라 로그인 정보를 조회.
     * 사용자명(username), 별명(nickname), 접근 IP(accessedIp) 중 하나의 조건을 선택하여 로그인 정보 검색 가능.
     * 추가적으로, 시작 날짜(startDate)와 종료 날짜(endDate)를 지정하여 해당 기간 내의 로그인 정보만 조회 가능.
     * 선택된 파라미터와 날짜 범위에 해당하는 로그인 정보를 기준으로 데이터를 필터링하고, 해당 IP로 그룹화하여 결과 반환.
     * 조회 결과는 'lastVisit' 필드에 따라 내림차순으로 정렬.
     *
     * @param username 선택적으로 제공될 수 있는 사용자명. 제공될 경우, 해당 사용자명을 가진 로그인 정보를 우선적으로 처리.
     * @param nickname 선택적으로 제공될 수 있는 별명. 제공될 경우, 해당 별명을 가진 로그인 정보를 우선적으로 처리.
     * @param accessedIp 선택적으로 제공될 수 있는 접근 IP. 제공될 경우, 해당 IP로 접근한 로그인 정보를 우선적으로 처리.
     * @param startDate 조회를 시작할 날짜. 선택적으로 제공될 수 있음.
     * @param endDate 조회를 종료할 날짜. 선택적으로 제공될 수 있음.
     * @return 조건과 날짜 범위에 맞는 {@link LoginInfoResponseDTO} 객체 목록 반환. 각 객체는 로그인 정보의 요약 포함.
     */
    public List<LoginInfoResponseDTO> findByCriteria(Optional<String> username, Optional<String> nickname, Optional<String> accessedIp,
                                                     Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate, PrincipalDetails principalDetails) {
        List<LoginInfo> allLoginInfos = loginInfoRepository.findAll(Sort.by(Sort.Direction.DESC, "lastVisit"));

        // 필터링 로직 수정
        Set<String> matchingIps = allLoginInfos.stream()
                .filter(info ->
                        username.map(u -> u.equals(info.getUser().getUsername())).orElse(true) &&
                                nickname.map(n -> n.equals(info.getUser().getNickname())).orElse(true) &&
                                accessedIp.map(ip -> ip.equals(info.getAccessedIp())).orElse(true) &&
                                startDate.map(sd -> !info.getLastVisit().isBefore(sd)).orElse(true) &&
                                endDate.map(ed -> !info.getLastVisit().isAfter(ed)).orElse(true))
                .map(LoginInfo::getAccessedIp)
                .collect(Collectors.toSet());

        // 찾아낸 IP를 기준으로 전체 로그인 정보 중 해당 IP를 가진 정보만 필터링하여 그룹화
        Map<String, List<LoginInfo>> groupedByIp = allLoginInfos.stream()
                .filter(info -> matchingIps.contains(info.getAccessedIp()))
                .collect(Collectors.groupingBy(LoginInfo::getAccessedIp));

        List<LoginInfoResponseDTO> results = new ArrayList<>();

        for (Map.Entry<String, List<LoginInfo>> entry : groupedByIp.entrySet()) {
            List<LoginInfo> infos = entry.getValue();

            // 조건에 따라 기준이 되는 로그인 정보 결정
            LoginInfo baseLoginInfo = null;
            if (username.isPresent()) {
                baseLoginInfo = infos.stream().filter(info -> info.getUser().getUsername().equals(username.get())).findFirst().orElse(null);
            } else if (nickname.isPresent()) {
                baseLoginInfo = infos.stream().filter(info -> info.getUser().getNickname().equals(nickname.get())).findFirst().orElse(null);
            } else if (accessedIp.isPresent()) {
                baseLoginInfo = infos.get(0); // IP 기준 조회 시 가장 최근 정보 사용
            } else {
                baseLoginInfo = infos.get(0); // 파라미터 없는 경우 가장 최근 정보 사용
            }

            // 사용자명 리스트 생성
            List<String> usernameList = infos.stream()
                    .map(info -> info.getUser().getUsername() + "(" + info.getUser().getNickname() + ")")
                    .distinct()
                    .collect(Collectors.toList());

            // 파라미터가 하나만 선택 가능하므로, 각각에 대한 처리를 순차적으로 진행
            if (username.isPresent()) {
                String targetUsername = username.get();
                // 해당하는 username을 찾아서 맨 앞으로 이동
                usernameList.sort((o1, o2) -> o1.startsWith(targetUsername + "(") ? -1 : o2.startsWith(targetUsername + "(") ? 1 : o1.compareTo(o2));
            } else if (nickname.isPresent()) {
                String targetNickname = nickname.get();
                // 해당하는 nickname을 가진 항목을 맨 앞으로 이동
                usernameList.sort((o1, o2) -> o1.contains("(" + targetNickname + ")") ? -1 : o2.contains("(" + targetNickname + ")") ? 1 : o1.compareTo(o2));
            }

            String usernames = String.join(", ", usernameList);

            // 기준 정보를 바탕으로 DTO 생성
            if (baseLoginInfo != null) {
                LoginInfoResponseDTO dto = new LoginInfoResponseDTO(
                        baseLoginInfo.getId(),
                        baseLoginInfo.getUser().getId(),
                        usernames,
                        baseLoginInfo.getUser().getNickname(),
                        baseLoginInfo.getDistributor(),
                        baseLoginInfo.getStore(),
                        baseLoginInfo.getAccessedIp(),
                        baseLoginInfo.getAccessedDevice(),
                        baseLoginInfo.getLastVisit(),
                        baseLoginInfo.getUser().getSite()
                );
                results.add(dto);
            }
        }

        return results.stream()
                .sorted(Comparator.comparing(LoginInfoResponseDTO::getLastVisit).reversed())
                .collect(Collectors.toList());
    }

    /**
     * 날짜 범위 내에서 각 유저의 로그인 횟수를 카운트하여 반환.
     *
     * @param startDate 조회를 시작할 날짜. 선택적으로 제공될 수 있음.
     * @param endDate 조회를 종료할 날짜. 선택적으로 제공될 수 있음.
     * @return 날짜 범위 내에서 각 유저의 로그인 횟수를 담은 {@link UserLoginCountDTO} 리스트.
     */
    public List<UserLoginCountDTO> getUserLoginCountsByDateRange(Optional<LocalDateTime> startDate, Optional<LocalDateTime> endDate, PrincipalDetails principalDetails) {
        List<LoginInfo> allLoginInfos = loginInfoRepository.findAll(Sort.by(Sort.Direction.DESC, "lastVisit"));

        // 날짜 범위에 맞는 로그인 정보 필터링
        List<LoginInfo> filteredLoginInfos = allLoginInfos.stream()
                .filter(info ->
                        startDate.map(sd -> !info.getLastVisit().isBefore(sd)).orElse(true) &&
                                endDate.map(ed -> !info.getLastVisit().isAfter(ed)).orElse(true))
                .collect(Collectors.toList());

        // 유저별 로그인 횟수 카운트
        Map<Long, Long> userLoginCounts = filteredLoginInfos.stream()
                .collect(Collectors.groupingBy(info -> info.getUser().getId(), Collectors.counting()));

        // 유저 정보 매핑
        Map<Long, User> userInfoMap = filteredLoginInfos.stream()
                .collect(Collectors.toMap(info -> info.getUser().getId(), LoginInfo::getUser, (existing, replacement) -> existing));

        // DTO 리스트 생성

        return userLoginCounts.entrySet().stream()
                .map(entry -> {
                    User user = userInfoMap.get(entry.getKey());
                    return new UserLoginCountDTO(user.getId(), user.getUsername(), user.getNickname(), entry.getValue());
                })
                .sorted(Comparator.comparing(UserLoginCountDTO::getLoginCount).reversed())
                .collect(Collectors.toList());
    }
}
