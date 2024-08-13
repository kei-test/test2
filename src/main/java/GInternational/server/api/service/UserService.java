package GInternational.server.api.service;


import GInternational.server.api.dto.*;
import GInternational.server.api.entity.*;
import GInternational.server.api.mapper.UserRequestMapper;
import GInternational.server.api.mapper.UserResponseMapper;
import GInternational.server.api.mapper.WalletRequestMapper;
import GInternational.server.api.repository.*;
import GInternational.server.api.vo.ExpRecordEnum;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class UserService {


    private final UserRepository userRepository;
    private final UserRequestMapper userRequestMapper;
    private final UserResponseMapper userResponseMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final WalletRequestMapper walletRequestMapper;
    private final WalletRepository walletRepository;
    private final IpRepository repository;
    private final LoginStatisticService loginStatisticService;
    private final ExpRecordService expRecordService;
    private final JoinPointRepository joinPointRepository;





    /*
    ADMIN,MANAGER,본인만 수정,삭제 가능하도록 검증이 필요함
     */


    /**
     * USER (GUEST) 회원 가입 및 계좌 등록. 사용자 생성과 동시에 계좌를 등록함.
     *
     * @param userRequestDTO 회원 가입 요청 데이터
     * @param request 클라이언트 요청 정보
     * @return 생성된 사용자의 응답 DTO
     */
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        Ip validateCheckIp = repository.findByIpContent(ip);
        if (validateCheckIp != null) {
            throw new RestControllerException(ExceptionCode.BLOCKED_IP, "접근이 차단된 IP입니다.");
        }

        validateUsername(userRequestDTO.getUsername());

        if (userRepository.findByPhone(userRequestDTO.getPhone()).isPresent()) {
            throw new RestControllerException(ExceptionCode.DUPLICATE_PHONE, "이미 존재하는 전화번호입니다.");
        }

        User user = userRequestMapper.toEntity(userRequestDTO);
        user.setPassword(bCryptPasswordEncoder.encode(userRequestDTO.getPassword()));
        user.setRole("ROLE_GUEST");
        user.setLv(1);
        user.setExp(0);
        user.setIp(ip);
        user.setUserGubunEnum(UserGubunEnum.대기);
        user.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        user.setCreatedAt(LocalDateTime.now());
        user.setReferredBy(userRequestDTO.getReferredBy());
        user.setKakaoRegistered(false);
        user.setKakaoId("");
        user.setTelegramRegistered(false);
        user.setTelegramId("");
        user.setSmsReceipt(true);
        user.setAmazonVisible(false);
        user.setAccountVisible(false);
        user.setCanPost(true);
        user.setCanComment(true);
        user.setCanBonus(true);
        user.setCanRecommend(true);
        user.setRecommendationCode(null);
        user.setRecommendationCodeIssuedAt(null);

        String referrerInput = userRequestDTO.getReferredBy();
        User referrer = userRepository.findByUsernameOrRecommendationCode(referrerInput, referrerInput);

        boolean isAmazonCode = false;
        if (referrer == null) {
            List<User> referrerList = userRepository.findByAmazonCode(referrerInput);
            if (!referrerList.isEmpty()) {
                referrer = referrerList.get(0); // 첫 번째 요소를 추천인으로 설정
                isAmazonCode = true; // 아마존 코드로 찾아진 경우
                // 파트너 유형에 따라 분기 처리
                if ("대본사".equals(referrer.getPartnerType())) {
                    user.setDistributor(referrer.getDistributor());
                    user.setStructure(calculateStructure(referrer)); // 계층 구조 계산 및 설정
                } else if (Arrays.asList("본사", "부본사", "총판", "매장").contains(referrer.getPartnerType())) {
                    user.setStore(referrer.getDistributor());
                    user.setStructure(calculateStructure(referrer)); // 계층 구조 계산 및 설정
                }
            }
        }

        if (referrer != null) {
            if (!referrer.isCanRecommend()) {
                throw new RestControllerException(ExceptionCode.CANNOT_RECOMMEND, "해당 추천인은 추천이 불가능한 유저입니다.");
            }

            user.setReferredBy(referrer.getUsername()); // 추천인의 username을 저장
            user.setDistributor(""); // 총판에 의한 추천이 아니므로 빈 문자열 저장
            user.setAmazonUser(isAmazonCode); // 총판 추천에 의해 가입된 유저인지 구분

            List<String> recommendedUsers = referrer.getRecommendedUsers();
            if (recommendedUsers == null) {
                recommendedUsers = new ArrayList<>();
            }
            recommendedUsers.add(user.getUsername());
            referrer.setRecommendedUsers(recommendedUsers);
            referrer.setRecommendedCount(referrer.getRecommendedCount() + 1);

            expRecordService.recordDailyExp(referrer.getId(), referrer.getUsername(), referrer.getNickname(), 30, ip, ExpRecordEnum.신규회원추천경험치);

            userRepository.save(referrer);
        } else {
            throw new RestControllerException(ExceptionCode.REFERRER_NOT_FOUNT, "존재하지 않는 추천인(추천코드)입니다.");
        }

        User savedUser = userRepository.save(user);
        loginStatisticService.recordCreateUser();

        //회원 정보 먼저 저장시킨 뒤 저장된 회원의 정보를 가져와 계좌 테이블에 저장하는 로직 (해당 회원의 계좌 자동 등록)
        WalletRequestDTO walletRequestDTO = new WalletRequestDTO();
        Wallet wallet = walletRequestMapper.toEntity(walletRequestDTO);
        wallet.setOwnerName(userRequestDTO.getOwnerName());
        wallet.setBankName(userRequestDTO.getBankName());
        wallet.setNumber(userRequestDTO.getNumber());
        wallet.setBankPassword(userRequestDTO.getBankPassword());
        wallet.setUser(savedUser);
        wallet.setPoint(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        wallet.setTodayDeposit(0);
        wallet.setTodayWithdraw(0);
        wallet.setTotalAmazonDeposit(0);
        wallet.setTotalAmazonWithdraw(0);
        wallet.setTotalAmazonSettlement(0);
        walletRepository.save(wallet);

        return userResponseMapper.toDto(savedUser);
    }

    /**
     * referrer의 계층 구조를 계산하여 반환합니다.
     * @param referrer 추천인 User 객체
     * @return 계층 구조 문자열
     */
    private String calculateStructure(User referrer) {
        StringBuilder structure = new StringBuilder();

        // 상위 파트너 찾기
        User currentReferrer = referrer;
        while (currentReferrer != null) {
            if ("DST".equals(currentReferrer.getPartnerType())) {
                structure.insert(0, "대본사(" + currentReferrer.getUsername() + ")");
                return structure.toString(); // DST인 경우 즉시 반환
            }
            if (currentReferrer.getPartnerType() != null && !currentReferrer.getPartnerType().isEmpty()) {
                structure.insert(0, currentReferrer.getPartnerType() + "(" + currentReferrer.getUsername() + ")");
            } else {
                structure.insert(0, "()");
            }
            if ("대본사".equals(currentReferrer.getPartnerType())) {
                break;
            }
            currentReferrer = userRepository.findByUsername(currentReferrer.getReferredBy());
            if (currentReferrer != null) {
                structure.insert(0, "-");
            }
        }

        // 필요한 계층을 모두 포함하도록 빈 문자열로 초기화된 파트너 타입을 설정
        String[] partnerTypes = {"본사", "부본사", "총판", "매장"};
        Map<String, String> partnerMap = new HashMap<>();
        for (String type : partnerTypes) {
            partnerMap.put(type, "");
        }

        // 하위 파트너 찾기
        String nextPartnerType = "본사";
        while (!nextPartnerType.isEmpty()) {
            assert referrer != null;
            String finalNextPartnerType = nextPartnerType;
            List<User> recommendedUsers = referrer.getRecommendedUsers().stream()
                    .map(userRepository::findByUsername)
                    .filter(user -> user != null && finalNextPartnerType.equals(user.getPartnerType()))
                    .sorted(Comparator.comparing(User::getCreatedAt))
                    .collect(Collectors.toList());

            if (!recommendedUsers.isEmpty()) {
                User nextReferrer = recommendedUsers.get(0);
                partnerMap.put(nextReferrer.getPartnerType(), nextReferrer.getUsername());
                referrer = nextReferrer;
            } else {
                break;
            }

            switch (nextPartnerType) {
                case "본사":
                    nextPartnerType = "부본사";
                    break;
                case "부본사":
                    nextPartnerType = "총판";
                    break;
                case "총판":
                    nextPartnerType = "매장";
                    break;
                default:
                    nextPartnerType = "";
            }

            if ("매장".equals(nextPartnerType)) {
                break;
            }
        }

        // 모든 계층을 문자열로 조합
        structure = new StringBuilder("대본사(" + referrer.getUsername() + ")");
        for (String type : partnerTypes) {
            structure.append("-").append(type).append("(").append(partnerMap.get(type)).append(")");
        }

        return structure.toString();
    }

    public UserResponseDTO createUserOrTest(UserRequestDTO userRequestDTO, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        Ip validateCheckIp = repository.findByIpContent(ip);
        if (validateCheckIp != null) {
            throw new RestControllerException(ExceptionCode.BLOCKED_IP, "접근이 차단된 IP입니다.");
        }

        validateUsername(userRequestDTO.getUsername());

        // 전화번호 중복 확인
        if (userRepository.findByPhone(userRequestDTO.getPhone()).isPresent()) {
            throw new RestControllerException(ExceptionCode.DUPLICATE_PHONE, "이미 존재하는 전화번호입니다.");
        }

        User user = userRequestMapper.toEntity(userRequestDTO);
        user.setPassword(bCryptPasswordEncoder.encode(userRequestDTO.getPassword()));
        user.setRole(userRequestDTO.getRole());
        user.setLv(1);
        user.setExp(0);
        user.setIp(ip);
        user.setUserGubunEnum(UserGubunEnum.정상);
        user.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        user.setCreatedAt(LocalDateTime.now());
        user.setReferredBy(userRequestDTO.getReferredBy());
        user.setKakaoRegistered(false);
        user.setKakaoId("");
        user.setTelegramRegistered(false);
        user.setTelegramId("");
        user.setSmsReceipt(true);
        user.setAmazonVisible(false);
        user.setAccountVisible(false);
        user.setCanPost(true);
        user.setCanComment(true);
        user.setCanBonus(true);
        user.setCanRecommend(true);
        user.setRecommendationCode(null);
        user.setRecommendationCodeIssuedAt(null);

        String referrerInput = userRequestDTO.getReferredBy();
        User referrer = userRepository.findByUsernameOrRecommendationCode(referrerInput, referrerInput);

        boolean isAmazonCode = false;
        if (referrer == null) {
            List<User> referrerList = userRepository.findByAmazonCode(referrerInput);
            if (!referrerList.isEmpty()) {
                referrer = referrerList.get(0); // 첫 번째 요소를 추천인으로 설정
                isAmazonCode = true; // 아마존 코드로 찾아진 경우
                user.setDistributor(referrer.getDistributor());
                user.setStructure(calculateStructure(referrer)); // 계층 구조 계산 및 설정
            }
        }

        if (referrer != null) {
            if (!referrer.isCanRecommend()) {
                throw new RestControllerException(ExceptionCode.CANNOT_RECOMMEND, "해당 추천인은 추천이 불가능한 유저입니다.");
            }

            user.setReferredBy(referrer.getUsername()); // 추천인의 username을 저장
            user.setDistributor(""); // 총판에 의한 추천이 아니므로 빈 문자열 저장
            user.setAmazonUser(isAmazonCode); // 총판 추천에 의해 가입된 유저인지 구분

            List<String> recommendedUsers = referrer.getRecommendedUsers();
            if (recommendedUsers == null) {
                recommendedUsers = new ArrayList<>();
            }
            recommendedUsers.add(user.getUsername());
            referrer.setRecommendedUsers(recommendedUsers);
            referrer.setRecommendedCount(referrer.getRecommendedCount() + 1);

            expRecordService.recordDailyExp(referrer.getId(), referrer.getUsername(), referrer.getNickname(), 30, ip, ExpRecordEnum.신규회원추천경험치);

            userRepository.save(referrer);
        } else {
            throw new RestControllerException(ExceptionCode.REFERRER_NOT_FOUNT, "존재하지 않는 추천인(추천코드)입니다.");
        }

        User savedUser = userRepository.save(user);
        if ("ROLE_USER".equals(user.getRole())) {
            loginStatisticService.recordCreateUser();
        }

        //회원 정보 먼저 저장시킨 뒤 저장된 회원의 정보를 가져와 계좌 테이블에 저장하는 로직 (해당 회원의 계좌 자동 등록)
        WalletRequestDTO walletRequestDTO = new WalletRequestDTO();
        Wallet wallet = walletRequestMapper.toEntity(walletRequestDTO);
        wallet.setOwnerName(userRequestDTO.getOwnerName());
        wallet.setBankName(userRequestDTO.getBankName());
        wallet.setNumber(userRequestDTO.getNumber());
        wallet.setBankPassword(userRequestDTO.getBankPassword());
        wallet.setUser(savedUser);
        wallet.setPoint(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        wallet.setTodayDeposit(0);
        wallet.setTodayWithdraw(0);
        wallet.setTotalAmazonDeposit(0);
        wallet.setTotalAmazonWithdraw(0);
        wallet.setTotalAmazonSettlement(0);
        walletRepository.save(wallet);

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return userResponseMapper.toDto(savedUser);
    }

    /**
     * 사용자 정보 업데이트. 주어진 정보로 사용자 정보를 업데이트.
     *
     * @param user 업데이트할 사용자 엔티티
     * @param principalDetails 요청자의 인증 정보
     * @return 업데이트된 사용자의 응답 DTO
     */
    public UserResponseDTO updateUser(User user, PrincipalDetails principalDetails) {
        User findUser = validateUser(principalDetails.getUser().getId());
        Optional.ofNullable(user.getPassword()).ifPresent(password -> findUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword())));
        Optional.ofNullable(user.getUsername()).ifPresent(username -> findUser.setUsername(username));
        Optional.ofNullable(user.getName()).ifPresent(name -> findUser.setName(name));
        Optional.ofNullable(user.getNickname()).ifPresent(nickname -> findUser.setNickname(nickname));
        findUser.setUpdatedAt(LocalDateTime.now());
        User savedUser = userRepository.save(findUser);
        return userResponseMapper.toDto(savedUser);
    }

    /**
     * AAS 정보 삽입 메서드. 사용자 ID를 기반으로 AAS 정보를 업데이트합니다.
     *
     * @param userId AAS 정보를 업데이트할 사용자의 ID
     * @param aasUserProfileDTO AAS 사용자 프로필 데이터
     * @return 업데이트된 사용자 엔티티
     */
    public User insertAAS(Long userId, AASUserProfileDTO aasUserProfileDTO) {
        User user = userRepository.findById(userId).orElseThrow(()-> new RuntimeException("유저 없음"));
        user.setAasId(aasUserProfileDTO.getAasId());
        return userRepository.save(user);
    }

    /**
     * 지정된 사용자 ID에 해당하는 사용자의 상세 정보 조회.
     * @param userId 조회할 사용자의 ID
     * @param principalDetails 요청을 수행하는 사용자의 인증 정보
     * @return 조회된 사용자 엔티티
     */
    public User detailUser(Long userId,PrincipalDetails principalDetails) {
        User user = validateUser(userId);
//        user.setPassword(userRepository.getPasswordByUserId(userId));
        return user;
    }

    /**
     * 지정된 사용자 ID에 해당하는 사용자의 상세 정보를 조회. 이 메서드는 사용자 엔티티를 직접 반환.
     * @param userId 조회할 사용자의 ID
     * @param principalDetails 요청을 수행하는 사용자의 인증 정보
     * @return 조회된 사용자 엔티티
     */
    public UserResponseDTO detailUserInfo(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다"));

        // 추천인 정보 조회
        UserReferralInfoDTO referrerInfo = null;
        if (user.getReferredBy() != null && !user.getReferredBy().isEmpty()) {
            User referrer = userRepository.findByUsername(user.getReferredBy());
            if (referrer != null) {
                referrerInfo = buildUserReferralInfoDTO(referrer);
            }
        }

        // 추천한 유저들의 정보 조회
        List<UserReferralInfoDTO> recommendedUsersInfo = new ArrayList<>();
        if (user.getRecommendedUsers() != null) {
            for (String recommendedUsername : user.getRecommendedUsers()) {
                User recommendedUser = userRepository.findByUsername(recommendedUsername);
                if (recommendedUser != null) {
                    recommendedUsersInfo.add(buildUserReferralInfoDTO(recommendedUser));
                }
            }
        }

        Hibernate.initialize(user.getRecommendedUsers());
        return new UserResponseDTO(user, referrerInfo, recommendedUsersInfo);
    }

    private UserReferralInfoDTO buildUserReferralInfoDTO(User user) {
        return new UserReferralInfoDTO(
                user.getId(),
                user.getUsername(),
                user.getNickname(),
                "상태",
                user.getUserGubunEnum(),
                user.getCreatedAt()
        );
    }

    /**
     * 지정된 유저네임이 중복되는지 검사.
     * @param username 검사할 유저네임
     * @return 중복 여부 (true: 중복됨, false: 중복되지 않음)
     */
    public boolean isUsernameDuplicate(String username) {
        if (username.isBlank()) {
            throw new IllegalArgumentException("유저네임을 입력하세요");
        }
        return userRepository.existsByUsername(username);
    }

    /**
     * 지정된 닉네임이 중복되는지 검사.
     * @param nickname 검사할 닉네임
     * @return 중복 여부 (true: 중복됨, false: 중복되지 않음)
     */
    public boolean isNicknameDuplicate(String nickname) {
        if (nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임을 입력하세요");
        }
        return userRepository.existsByNickname(nickname);
    }

    /**
     * 지정된 사용자 ID를 검증하고, 존재하는 사용자를 반환.
     * @param userId 검증할 사용자의 ID
     * @return 검증된 사용자 엔티티
     */
    public User validateUser(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        User findUser = optionalUser.orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        return findUser;
    }

    /**
     * 지정된 유저네임의 유효성 검사.
     * @param username 검사할 유저네임
     */
    public void validateUsername(String username) {
        if (username.isBlank()) {
            throw new RestControllerException(ExceptionCode.USERNAME_NOT_PROVIDED, "id를 입력하세요.");
        }
        if (username.contains(" ")) {
            throw new RestControllerException(ExceptionCode.INVALID_USERNAME, "아이디에 띄어쓰기를 포함할 수 없습니다.");
        }
        if (userRepository.existsByUsername(username)) {
            throw new RestControllerException(ExceptionCode.USERNAME_DUPLICATE, "중복된 아이디 입니다.");
        }
    }

    /**
     * 추천된 사용자 목록 조회.
     * @param userId 조회를 요청하는 사용자의 ID
     * @param principalDetails 요청을 수행하는 사용자의 인증 정보
     * @return 추천된 사용자의 목록
     */
    public List<RecommendedUserDTO> getRecommendedUsers(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new RestControllerException(ExceptionCode.USER_NOT_FOUND));

        List<RecommendedUserDTO> recommendedUsers = new ArrayList<>();
        for (String recommendedUsername : user.getRecommendedUsers()) {
            User recommendedUser = userRepository.findByUsername(recommendedUsername);
            if (recommendedUser != null) {
                RecommendedUserDTO dto = new RecommendedUserDTO();
                dto.setUsername(recommendedUser.getUsername());
                dto.setCreatedAt(recommendedUser.getCreatedAt());
                dto.setLastVisit(recommendedUser.getLastVisit());
                dto.setNickname(recommendedUser.getNickname());
                dto.setUserGubun(recommendedUser.getUserGubunEnum().get표시이름());
                recommendedUsers.add(dto);
            }
        }
        return recommendedUsers;
    }

    public void updateUserLastVisit(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));

        user.setLastVisit(LocalDateTime.now());
        userRepository.save(user);
    }
}

