package GInternational.server.api.service;

import GInternational.server.amzn.dto.version2.*;
import GInternational.server.amzn.dto.total.AmznTotalPartnerReqDTO2;
import GInternational.server.amzn.repo.AmznRepositoryCustom;
import GInternational.server.api.dto.*;
import GInternational.server.api.entity.JoinPoint;
import GInternational.server.api.repository.JoinPointRepository;
import GInternational.server.api.vo.ExpRecordEnum;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.mapper.AmazonUserResponseMapper;
import GInternational.server.api.vo.AmazonUserStatusEnum;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AmazonUserService {

    private final UserRepository userRepository;
    private final AmazonUserResponseMapper amazonUserResponseMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final WalletRepository walletRepository;
    private final UserService userService;
    private final JoinPointRepository joinPointRepository;
    private final ExpRecordService expRecordService;
    private final AmznRepositoryCustom amznRepositoryCustom;

    /**
     * 대본사 계정 생성.
     *
     * @param requestDTO 사용자 생성 요청 데이터
     * @param principalDetails 인증된 사용자 정보
     * @return 생성된 대본사 사용자 정보
     */
    public AmazonUserResponseDTO createBigHeadOffice(AmazonUserRequestDTO requestDTO, PrincipalDetails principalDetails) {
        validateUserRole(principalDetails, "ROLE_ADMIN");
        User bigHeadOffice = new User();
        
        bigHeadOffice.setUsername(requestDTO.getUsername());
        bigHeadOffice.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        bigHeadOffice.setNickname(requestDTO.getNickname());
        bigHeadOffice.setPhone(requestDTO.getPhone());
        bigHeadOffice.setAmazonCode(requestDTO.getAmazonCode());
        bigHeadOffice.setRole("ROLE_USER"); // 역할 설정
        bigHeadOffice.setAmazonUserStatus(AmazonUserStatusEnum.NORMAL);
        bigHeadOffice.setLv(1);
        bigHeadOffice.setCreatedAt(LocalDateTime.now());
        bigHeadOffice.setBirth("기본값");
        bigHeadOffice.setEmail("기본값");
        bigHeadOffice.setReferredBy(principalDetails.getUsername());
        bigHeadOffice.setPartnerType("대본사");
        bigHeadOffice.setUserGubunEnum(UserGubunEnum.정상);
        bigHeadOffice.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        bigHeadOffice.setSlotRolling(Math.round(requestDTO.getSlotRolling() * 100.0) / 100.0);
        bigHeadOffice.setCasinoRolling(Math.round(requestDTO.getCasinoRolling() * 100.0) / 100.0);
        User savedBigHeadOffice = userRepository.save(bigHeadOffice);

        Wallet wallet = new Wallet();
        wallet.setUser(savedBigHeadOffice);
        wallet.setOwnerName(requestDTO.getOwnername());
        wallet.setNumber(requestDTO.getNumber());
        wallet.setBankName(requestDTO.getBankname());
        wallet.setBankPassword(requestDTO.getBankPassword());

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return amazonUserResponseMapper.toDto(savedBigHeadOffice);
    }


    /**
     * 파트너의 하위 계정 생성.
     *
     * @param requestDTO 파트너 하위 계정 생성 정보
     * @return 생성된 하위 계정 정보를 담은 DTO
     * @throws RestControllerException 사용자를 찾을 수 없거나, 잘못된 파트너 타입인 경우 예외 발생
     */
    public AmazonUserResponseDTO createSubAccountForPartner(AmazonUserRequestDTO requestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User parentUser = userRepository.findById(principalDetails.getUser().getId())
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        String parentPartnerType = principalDetails.getUser().getPartnerType();

        // 파트너 타입이 대본사, 본사, 부본사, 총판 중 하나인지 검증
        if (!List.of("대본사", "본사", "부본사", "총판").contains(parentPartnerType)) {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "잘못된 파트너 타입입니다.");
        }

        if ("매장".equals(parentPartnerType)) {
            throw new IllegalArgumentException("매장의 하부는 추가할 수 없습니다.");
        }

        String ip = request.getRemoteAddr();

        User subAccount = new User();
        subAccount.setUsername(requestDTO.getUsername());
        subAccount.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        subAccount.setNickname(requestDTO.getNickname());
        subAccount.setPhone(requestDTO.getPhone());
        subAccount.setAmazonCode(requestDTO.getAmazonCode());
        subAccount.setRole("ROLE_USER");
        subAccount.setAmazonUserStatus(AmazonUserStatusEnum.NORMAL);
        subAccount.setLv(1);
        subAccount.setCreatedAt(LocalDateTime.now());
        subAccount.setBirth("기본값");
        subAccount.setEmail("기본값");
        subAccount.setReferredBy(principalDetails.getUsername());
        subAccount.setUserGubunEnum(UserGubunEnum.정상);
        subAccount.setMonitoringStatus(UserMonitoringStatusEnum.정상);

        User upperAccount = principalDetails.getUser();

        // 상위 계정의 롤링 값을 가져와서 비교
        double maxSlotRolling = upperAccount.getSlotRolling();
        double maxCasinoRolling = upperAccount.getCasinoRolling();

        // 요청받은 롤링 값이 상위 계정의 롤링 값 범위를 넘어가면 예외 처리
        if (requestDTO.getSlotRolling() > maxSlotRolling || requestDTO.getCasinoRolling() > maxCasinoRolling) {
            throw new RestControllerException(ExceptionCode.INVALID_REQUEST, "롤링 값이 상위 계정의 범위를 초과합니다.");
        }

        // 슬롯롤링적립과 카지노롤링적립 설정
        double slotRolling = Math.round(requestDTO.getSlotRolling() * 100.0) / 100.0;
        double casinoRolling = Math.round(requestDTO.getCasinoRolling() * 100.0) / 100.0;
        subAccount.setSlotRolling(slotRolling);
        subAccount.setCasinoRolling(casinoRolling);

        // 계정 귀속 설정
        assignSubAccountToParent(subAccount, parentUser, parentPartnerType);

        // 계정 저장
        User savedSubAccount = userRepository.save(subAccount);

        // 상위 계정의 추천인 및 롤링 값 차감
        List<String> recommendedUsers = upperAccount.getRecommendedUsers();
        if (recommendedUsers == null) {
            recommendedUsers = new ArrayList<>();
        }
        recommendedUsers.add(subAccount.getUsername());
        upperAccount.setRecommendedUsers(recommendedUsers);
        upperAccount.setRecommendedCount(upperAccount.getRecommendedCount() + 1);
        upperAccount.decreaseSlotRolling(slotRolling);
        upperAccount.decreaseCasinoRolling(casinoRolling);
        expRecordService.recordDailyExp(upperAccount.getId(), upperAccount.getUsername(), upperAccount.getNickname(), 30, ip, ExpRecordEnum.신규회원추천경험치);
        userRepository.save(upperAccount);

        // Wallet 생성 및 저장
        Wallet wallet = new Wallet();
        wallet.setUser(savedSubAccount);
        wallet.setOwnerName(requestDTO.getOwnername());
        wallet.setNumber(requestDTO.getNumber());
        wallet.setBankName(requestDTO.getBankname());
        wallet.setBankPassword(requestDTO.getBankPassword());

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return amazonUserResponseMapper.toDto(savedSubAccount);
    }

    /**
     * amazonCode를 통해 총판에 의해 추천된 모든 유저 조회.
     *
     * @param principalDetails 현재 로그인한 사용자의 정보
     * @return 조회된 사용자 목록
     */
    public List<AmazonUserInfoDTO> findUsersByRoleAndReferred(PrincipalDetails principalDetails) {
        User currentUser = principalDetails.getUser();
        String currentRole = currentUser.getRole();
        String currentUsername = currentUser.getUsername();
        String currentPartnerType = currentUser.getPartnerType();

        if (currentRole.equals("ROLE_ADMIN")) {
            // 관리자 계정인 경우: isAmazonUser가 true인 모든 회원 조회
            return userRepository.findUsersByIsAmazonUser();
        } else if (Arrays.asList("대본사", "본사", "부본사", "총판", "매장").contains(currentPartnerType)) {
            // 파트너 계정인 경우: 현재 유저가 모집한 회원만 조회
            return userRepository.findUsersByReferredByAndIsAmazonUser(currentUsername);
        } else {
            // 그 외의 경우: 빈 리스트 반환
            return new ArrayList<>();
        }
    }

    /**
     * 지정된 파트너의 상세 정보 조회.
     *
     * @param userId 조회할 파트너의 ID
     * @param principalDetails 현재 로그인한 사용자의 정보
     * @return 조회된 파트너 정보를 담은 DTO
     * @throws RestControllerException 사용자를 찾을 수 없는 경우 예외 발생
     */
    public AmazonUserResponseDTO findUserById(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        return amazonUserResponseMapper.toDto(user);
    }

    public List<?> getTotalList(PrincipalDetails principalDetails) {
        List<AmznTotalPartnerReqDTO2> list = amznRepositoryCustom.searchByTotalPartner2();

        User user = userRepository.findByUsername(principalDetails.getUsername());
        Long userId = user.getId();

        List<AmznDaePartnerResDTO2> daeList = new ArrayList<>();
        List<AmznBonPartnerResDTO2> bonList = new ArrayList<>();
        List<AmznBuPartnerResDTO2> buList = new ArrayList<>();
        List<AmznChongPartnerResDTO2> chongList = new ArrayList<>();
        List<AmznMaePartnerResDTO2> maeList = new ArrayList<>();


        for (AmznTotalPartnerReqDTO2 obj : list) {
            String partnerType = obj.getPartnerType();
            Long daeId = obj.getDaeId();
            Long bonId = obj.getBonId();
            Long buId = obj.getBuId();
            Long chongId = obj.getChongId();

            if (partnerType.equals("대본사")) {
                AmznDaePartnerResDTO2 existingDae = daeList.stream()
                        .filter(d -> d.getDae().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingDae == null) {
                    AmznDaeTotalPartnerListDTO2 daePartnerList = new AmznDaeTotalPartnerListDTO2();
                    daePartnerList.setId(obj.getId());
                    daePartnerList.setUsername(obj.getUsername());
                    daePartnerList.setNickname(obj.getNickname());
                    daePartnerList.setAmazonMoney(obj.getAmazonMoney());
                    daePartnerList.setAmazonPoint(obj.getAmazonPoint());
                    daePartnerList.setTodayDeposit(obj.getTodayDeposit());
                    daePartnerList.setTodayWithdraw(obj.getTodayWithdraw());
                    daePartnerList.setTotalAmazonDeposit(obj.getTotalAmazonDeposit());
                    daePartnerList.setTotalAmazonWithdraw(obj.getTotalAmazonWithdraw());
                    daePartnerList.setTotalAmazonSettlement(obj.getTotalAmazonSettlement());
                    daePartnerList.setRecommendedCount(obj.getRecommendedCount());
                    daePartnerList.setCreatedAt(obj.getCreatedAt());
                    daePartnerList.setRate(obj.getPartnerType());

                    AmznDaePartnerResDTO2 dae = new AmznDaePartnerResDTO2();
                    dae.setDae(daePartnerList);
                    daeList.add(dae);
                }
            } else if (daeId != null) {
                AmznBonPartnerResDTO2 existingBon = bonList.stream()
                        .filter(b -> b.getBon().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingBon == null) {
                    AmznBonTotalPartnerListDTO2 bonPartnerList = new AmznBonTotalPartnerListDTO2();
                    bonPartnerList.setId(obj.getId());
                    bonPartnerList.setUsername(obj.getUsername());
                    bonPartnerList.setNickname(obj.getNickname());
                    bonPartnerList.setAmazonMoney(obj.getAmazonMoney());
                    bonPartnerList.setAmazonPoint(obj.getAmazonPoint());
                    bonPartnerList.setTodayDeposit(obj.getTodayDeposit());
                    bonPartnerList.setTodayWithdraw(obj.getTodayWithdraw());
                    bonPartnerList.setTotalAmazonDeposit(obj.getTotalAmazonDeposit());
                    bonPartnerList.setTotalAmazonWithdraw(obj.getTotalAmazonWithdraw());
                    bonPartnerList.setTotalAmazonSettlement(obj.getTotalAmazonSettlement());
                    bonPartnerList.setRecommendedCount(obj.getRecommendedCount());
                    bonPartnerList.setCreatedAt(obj.getCreatedAt());
                    bonPartnerList.setRate(obj.getPartnerType());
                    bonPartnerList.setNum(daeId);

                    AmznBonPartnerResDTO2 bon = new AmznBonPartnerResDTO2();
                    bon.setBon(bonPartnerList);
                    bonList.add(bon);

                    daeList.stream()
                            .filter(d -> d.getDae().getId().equals(daeId))
                            .findFirst()
                            .ifPresent(d -> d.getDae().getBonList().add(bon));
                }
            } else if (bonId != null) {
                AmznBuPartnerResDTO2 existingBu = buList.stream()
                        .filter(bu -> bu.getBu().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingBu == null) {
                    AmznBuTotalPartnerListDTO2 buPartnerList = new AmznBuTotalPartnerListDTO2();
                    buPartnerList.setId(obj.getId());
                    buPartnerList.setUsername(obj.getUsername());
                    buPartnerList.setNickname(obj.getNickname());
                    buPartnerList.setAmazonMoney(obj.getAmazonMoney());
                    buPartnerList.setAmazonPoint(obj.getAmazonPoint());
                    buPartnerList.setTodayDeposit(obj.getTodayDeposit());
                    buPartnerList.setTodayWithdraw(obj.getTodayWithdraw());
                    buPartnerList.setTotalAmazonDeposit(obj.getTotalAmazonDeposit());
                    buPartnerList.setTotalAmazonWithdraw(obj.getTotalAmazonWithdraw());
                    buPartnerList.setTotalAmazonSettlement(obj.getTotalAmazonSettlement());
                    buPartnerList.setRecommendedCount(obj.getRecommendedCount());
                    buPartnerList.setCreatedAt(obj.getCreatedAt());
                    buPartnerList.setRate(obj.getPartnerType());
                    buPartnerList.setNum(bonId);

                    AmznBuPartnerResDTO2 bu = new AmznBuPartnerResDTO2();
                    bu.setBu(buPartnerList);
                    buList.add(bu);

                    bonList.stream()
                            .filter(b -> b.getBon().getId().equals(bonId))
                            .findFirst()
                            .ifPresent(b -> b.getBon().getBuList().add(bu));
                }

            } else if (buId != null) {
                AmznChongPartnerResDTO2 existingChong = chongList.stream()
                        .filter(c -> c.getChong().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingChong == null) {
                    AmznChongTotalPartnerListDTO2 chongPartnerList = new AmznChongTotalPartnerListDTO2();
                    chongPartnerList.setId(obj.getId());
                    chongPartnerList.setUsername(obj.getUsername());
                    chongPartnerList.setNickname(obj.getNickname());
                    chongPartnerList.setAmazonMoney(obj.getAmazonMoney());
                    chongPartnerList.setAmazonPoint(obj.getAmazonPoint());
                    chongPartnerList.setTodayDeposit(obj.getTodayDeposit());
                    chongPartnerList.setTodayWithdraw(obj.getTodayWithdraw());
                    chongPartnerList.setTotalAmazonDeposit(obj.getTotalAmazonDeposit());
                    chongPartnerList.setTotalAmazonWithdraw(obj.getTotalAmazonWithdraw());
                    chongPartnerList.setTotalAmazonSettlement(obj.getTotalAmazonSettlement());
                    chongPartnerList.setRecommendedCount(obj.getRecommendedCount());
                    chongPartnerList.setCreatedAt(obj.getCreatedAt());
                    chongPartnerList.setRate(obj.getPartnerType());
                    chongPartnerList.setNum(buId);

                    AmznChongPartnerResDTO2 chong = new AmznChongPartnerResDTO2();
                    chong.setChong(chongPartnerList);
                    chongList.add(chong);

                    buList.stream()
                            .filter(bu -> bu.getBu().getId().equals(buId))
                            .findFirst()
                            .ifPresent(bu -> bu.getBu().getChongList().add(chong));
                }
            }else if (chongId != null) {
                AmznMaePartnerResDTO2 existingMae = maeList.stream()
                        .filter(m -> m.getMae().getId().equals(obj.getId()))
                        .findFirst()
                        .orElse(null);

                if (existingMae == null) {
                    AmznMaeTotalPartnerListDTO2 maePartnerList = new AmznMaeTotalPartnerListDTO2();
                    maePartnerList.setId(obj.getId());
                    maePartnerList.setUsername(obj.getUsername());
                    maePartnerList.setNickname(obj.getNickname());
                    maePartnerList.setAmazonMoney(obj.getAmazonMoney());
                    maePartnerList.setAmazonPoint(obj.getAmazonPoint());
                    maePartnerList.setTodayDeposit(obj.getTodayDeposit());
                    maePartnerList.setTodayWithdraw(obj.getTodayWithdraw());
                    maePartnerList.setTotalAmazonDeposit(obj.getTotalAmazonDeposit());
                    maePartnerList.setTotalAmazonWithdraw(obj.getTotalAmazonWithdraw());
                    maePartnerList.setTotalAmazonSettlement(obj.getTotalAmazonSettlement());
                    maePartnerList.setRecommendedCount(obj.getRecommendedCount());
                    maePartnerList.setCreatedAt(obj.getCreatedAt());
                    maePartnerList.setRate(obj.getPartnerType());
                    maePartnerList.setNum(chongId);

                    AmznMaePartnerResDTO2 mae = new AmznMaePartnerResDTO2();
                    mae.setMae(maePartnerList);
                    maeList.add(mae);

                    chongList.stream()
                            .filter(c -> c.getChong().getId().equals(chongId))
                            .findFirst()
                            .ifPresent(c -> c.getChong().getMaeList().add(mae));
                }
            } else return null;
        }

        if (user.getRole().equals("ROLE_ADMIN") || user.getPartnerType().equals("대본사")) {
            return daeList;
        } else if (user.getPartnerType().equals("본사")) {
            bonList.removeIf(bon -> !bon.getBon().getId().equals(userId));
            return bonList;
        } else if (user.getPartnerType().equals("부본사")) {
            buList.removeIf(bu -> !bu.getBu().getId().equals(userId));
            return buList;
        } else if (user.getPartnerType().equals("총판")) {
            chongList.removeIf(chong -> !chong.getChong().getId().equals(userId));
            return chongList;
        } else if (user.getPartnerType().equals("매장")) {
            maeList.removeIf(chong -> !chong.getMae().getId().equals(userId));
            return maeList;
        } return null;
    }

    /**
     * 로그인한 사용자의 역할에 따라 하위 파트너를 조회.
     *
     * @param principalDetails 현재 로그인한 사용자의 정보
     * @param status 조회할 사용자의 상태
     * @return 조회된 하위 파트너 목록
     */
    public Map<String, List<AmazonUserResponseDTO>> findSubPartners(PrincipalDetails principalDetails, AmazonUserStatusEnum status) {
        User currentUser = principalDetails.getUser();
        Map<String, List<AmazonUserResponseDTO>> hierarchyMap = new HashMap<>();

        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            // 관리자인 경우: 모든 대본사와 그 하위 계층 조회
            List<User> allHeadOffices = userRepository.findByPartnerType("대본사");

            for (User headOffice : allHeadOffices) {
                Set<User> allSubPartners = getAllSubPartners(headOffice, status);
                hierarchyMap.put(headOffice.getUsername(), allSubPartners.stream()
                        .map(amazonUserResponseMapper::toDto)
                        .collect(Collectors.toList()));
            }
        } else {
            // 일반 파트너인 경우: 자신의 계층부터 하위 파트너 조회
            Set<User> allSubPartners = getAllSubPartners(currentUser, status);
            hierarchyMap.put(currentUser.getUsername(), allSubPartners.stream()
                    .map(amazonUserResponseMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return hierarchyMap;
    }

    private Set<User> getAllSubPartners(User user, AmazonUserStatusEnum status) {
        Set<User> subPartnerSet = new HashSet<>();
        subPartnerSet.add(user); // 현재 사용자 추가

        if (user.getRole().equals("ROLE_ADMIN")) {
            // 관리자: 모든 하위 계층을 조회
            List<User> allHeadOffices = userRepository.findByPartnerType("대본사");
            for (User headOffice : allHeadOffices) {
                // 본사 조회
                List<User> bonUsers = userRepository.findByDaeIdAndAmazonUserStatus(headOffice.getId(), status);
                for (User bonUser : bonUsers) {
                    // 부본사 조회
                    List<User> buUsers = userRepository.findByBonIdAndAmazonUserStatus(bonUser.getId(), status);
                    for (User buUser : buUsers) {
                        // 총판 조회
                        List<User> chongUsers = userRepository.findByBuIdAndAmazonUserStatus(buUser.getId(), status);
                        for (User chongUser : chongUsers) {
                            // 매장 조회
                            List<User> storeUsers = userRepository.findByChongIdAndAmazonUserStatus(chongUser.getId(), status);
                            subPartnerSet.addAll(storeUsers);
                        }
                        subPartnerSet.addAll(chongUsers);
                    }
                    subPartnerSet.addAll(buUsers);
                }
                subPartnerSet.addAll(bonUsers);
            }
        } else {
            // 일반 파트너의 경우: 자신의 계층부터 하위 파트너 조회
            List<User> nextLevelUsers = new ArrayList<>();
            if (user.getDaeId() != null) {
                nextLevelUsers = userRepository.findByDaeIdAndAmazonUserStatus(user.getDaeId(), status);
            } else if (user.getBonId() != null) {
                nextLevelUsers = userRepository.findByBonIdAndAmazonUserStatus(user.getBonId(), status);
            } else if (user.getBuId() != null) {
                nextLevelUsers = userRepository.findByBuIdAndAmazonUserStatus(user.getBuId(), status);
            } else if (user.getChongId() != null) {
                nextLevelUsers = userRepository.findByChongIdAndAmazonUserStatus(user.getChongId(), status);
            }

            for (User nextLevelUser : nextLevelUsers) {
                subPartnerSet.addAll(getAllSubPartners(nextLevelUser, status));
            }
        }

        return subPartnerSet;
    }

    /**
     * 지정된 파트너의 상위 계층 정보 조회.
     *
     * @param userId 조회할 파트너의 ID
     * @return 조회된 파트너의 상위 계층 정보를 담은 DTO
     * @throws RestControllerException 사용자를 찾을 수 없는 경우 예외 발생
     */
    public AmazonUserHierarchyResponseDTO findPartnerHierarchy(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        AmazonUserHierarchyResponseDTO responseDTO = new AmazonUserHierarchyResponseDTO();

        // 현재 계정이 속한 총판 정보 조회 및 설정
        User currentLevel = user;
        User nextLevel;

        if (currentLevel.getChongId() != null) {
            nextLevel = userRepository.findById(currentLevel.getChongId()).orElse(null);
            if (nextLevel != null) {
                responseDTO.setDistributorInfo(formatUserInfo(nextLevel));
                currentLevel = nextLevel;

                // 현재 계정이 속한 부본사 정보 조회 및 설정
                if (currentLevel.getBuId() != null) {
                    nextLevel = userRepository.findById(currentLevel.getBuId()).orElse(null);
                    if (nextLevel != null) {
                        responseDTO.setDeputyHeadOfficeInfo(formatUserInfo(nextLevel));
                        currentLevel = nextLevel;

                        // 현재 계정이 속한 본사 정보 조회 및 설정
                        if (currentLevel.getBonId() != null) {
                            nextLevel = userRepository.findById(currentLevel.getBonId()).orElse(null);
                            if (nextLevel != null) {
                                responseDTO.setHeadOfficeInfo(formatUserInfo(nextLevel));
                                currentLevel = nextLevel;

                                // 현재 계정이 속한 대본사 정보 조회 및 설정
                                if (currentLevel.getDaeId() != null) {
                                    nextLevel = userRepository.findById(currentLevel.getDaeId()).orElse(null);
                                    if (nextLevel != null) {
                                        responseDTO.setBigHeadOfficeInfo(formatUserInfo(nextLevel));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return responseDTO;
    }

    /**
     * 사용자 정보를 포멧팅.
     *
     * @param user 사용자 엔티티
     * @return 포맷팅된 사용자 정보 문자열
     */
    private String formatUserInfo(User user) {
        return user.getUsername() + "/" + user.getNickname() + " (" + user.getRole().toString() + ")";
    }

    /**
     * 지정된 파트너 정보 업데이트.
     *
     * @param userId 업데이트할 파트너의 사용자 ID
     * @param requestDTO 업데이트할 정보가 담긴 DTO
     * @param principalDetails 인증된 사용자 정보
     * @return 업데이트된 파트너 정보를 담은 DTO
     * @throws RestControllerException 사용자를 찾을 수 없는 경우 예외 발생
     */
    public AmazonUserResponseDTO updateUser(Long userId, AmazonUserRequestDTO requestDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 필요한 필드만 업데이트
        if (requestDTO.getPassword() != null && !requestDTO.getPassword().isEmpty()) {
            user.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        }
        if (requestDTO.getBankname() != null) {
            user.getWallet().setBankName(requestDTO.getBankname());
        }
        if (requestDTO.getNumber() > 0) {
            user.getWallet().setNumber(requestDTO.getNumber());
        }
        if (requestDTO.getOwnername() != null) {
            user.getWallet().setOwnerName(requestDTO.getOwnername());
        }
        if (requestDTO.getPhone() != null) {
            user.setPhone(requestDTO.getPhone());
        }
        if (requestDTO.getLv() > 0) {
            user.setLv(requestDTO.getLv());
        }
        if (requestDTO.getSlotRolling() != null) {
            user.setSlotRolling(requestDTO.getSlotRolling());
        }
        if (requestDTO.getCasinoRolling() != null) {
            user.setCasinoRolling(requestDTO.getCasinoRolling());
        }
        if (requestDTO.getAmazonCode() != null) {
            user.setAmazonCode(requestDTO.getAmazonCode());
        }

        User updatedUser = userRepository.save(user);
        return amazonUserResponseMapper.toDto(updatedUser);
    }

    /**
     * 사용자의 접속 실패 횟수를 0으로 초기화.
     *
     * @param userId 사용자의 ID
     * @param principalDetails 인증된 사용자 정보
     * @return 사용자 정보를 담은 DTO
     * @throws RestControllerException 사용자를 찾을 수 없는 경우 예외 발생
     */
    @Transactional
    public AmazonUserResponseDTO resetFailVisitCount(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        user.setFailVisitCount(0); // 접속 실패 횟수를 0으로 설정
        User updatedFartner = userRepository.save(user);
        return amazonUserResponseMapper.toDto(updatedFartner);
    }

    /**
     * 하위 계정을 상위 파트너에게 귀속시킴.
     *
     * @param subAccount 귀속될 하위 계정
     * @param parentUser 상위 파트너 사용자
     * @param parentPartnerType 상위 파트너의 파트너 타입
     * @throws IllegalArgumentException 잘못된 파트너 타입인 경우 예외 발생
     */
    private void assignSubAccountToParent(User subAccount, User parentUser, String parentPartnerType) {
        switch (parentPartnerType) {
            case "대본사":
                subAccount.setDaeId(parentUser.getId());
                subAccount.setPartnerType("본사");
                break;
            case "본사":
                subAccount.setBonId(parentUser.getId());
                subAccount.setPartnerType("부본사");
                break;
            case "부본사":
                subAccount.setBuId(parentUser.getId());
                subAccount.setPartnerType("총판");
                break;
            case "총판":
                subAccount.setChongId(parentUser.getId());
                subAccount.setPartnerType("매장");
                break;
            default:
                throw new IllegalArgumentException("잘못된 파트너 타입입니다.");
        }
    }

    /**
     * 주어진 역할에 따라 사용자 접근 권한을 검증.
     *
     * @param principalDetails 인증된 사용자 정보
     * @param expectedRoles 기대되는 역할 목록
     * @throws RestControllerException 권한이 없는 경우 예외 발생
     */
    private void validateUserRole(PrincipalDetails principalDetails, String... expectedRoles) {
        User user = userService.validateUser(principalDetails.getUser().getId());
        Set<String> expectedRolesSet = new HashSet<>(Arrays.asList(expectedRoles));

        if (!expectedRolesSet.contains(user.getRole())) {
            throw new RestControllerException(ExceptionCode.UNAUTHORIZED_ACCESS, "권한이 없습니다.");
        }
    }

    private void validatePartnerType(PrincipalDetails principalDetails, String... expectedPartnerType) {
        User user = userService.validateUser(principalDetails.getUser().getId());
        Set<String> expectedPartnerTypesSet = new HashSet<>(Arrays.asList(expectedPartnerType));

        if (!expectedPartnerTypesSet.contains(user.getPartnerType())) {
            throw new RestControllerException(ExceptionCode.UNAUTHORIZED_ACCESS, "권한이 없습니다.");
        }
    }
}
