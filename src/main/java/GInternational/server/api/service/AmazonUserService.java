package GInternational.server.api.service;

import GInternational.server.api.entity.JoinPoint;
import GInternational.server.api.repository.JoinPointRepository;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.dto.AmazonUserHierarchyResponseDTO;
import GInternational.server.api.dto.AmazonUserInfoDTO;
import GInternational.server.api.dto.AmazonUserRequestDTO;
import GInternational.server.api.dto.AmazonUserResponseDTO;
import GInternational.server.api.mapper.AmazonUserResponseMapper;
import GInternational.server.api.vo.AmazonUserStatusEnum;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//    /**
//     * 어드민 계정 생성.
//     *
//     * @param amazonUserRequestDTO 사용자 생성 요청 데이터
//     * @return 생성된 어드민 사용자 정보
//     */
//    public AmazonUserResponseDTO createAdmin(AmazonUserRequestDTO amazonUserRequestDTO) {
//        User admin = new User();
//        Wallet wallet = new Wallet();
//
//        admin.setUsername(amazonUserRequestDTO.getUsername());
//        admin.setPassword(bCryptPasswordEncoder.encode(amazonUserRequestDTO.getPassword()));
//        admin.setPhone(amazonUserRequestDTO.getPhone());
//        admin.setNickname(amazonUserRequestDTO.getNickname());
//        admin.setApproveIp(amazonUserRequestDTO.getApproveIP());
//        admin.setAmazonUserStatus(amazonUserRequestDTO.getAmazonUserStatus());
//
//        // 나머지 필드는 기본값으로 설정
//        admin.setLv(10);
//        admin.setRole("ROLE_ADMIN");
//        admin.setBirth("기본값");
//        admin.setEmail("기본값");
//        admin.setReferredBy("관리자");
//        admin.setAccount(null);
//        admin.setExp(0);
//        admin.setCreatedAt(LocalDateTime.now());
//        admin.setUserGubunEnum(UserGubunEnum.정상);
//        admin.setMonitoringStatus(UserMonitoringStatusEnum.정상);
//        admin.setKakaoRegistered(false);
//        admin.setKakaoId("");
//        admin.setTelegramRegistered(false);
//        admin.setTelegramId("");
//        admin.setSmsReceipt(true);
//        admin.setAmazonVisible(false);
//        admin.setAccountVisible(false);
//        admin.setCanPost(true);
//        admin.setCanComment(true);
//        admin.setCanBonus(true);
//        admin.setCanRecommend(true);
//        User savedAdmin = userRepository.save(admin);
//
//        wallet.setUser(savedAdmin);
//        wallet.setSportsBalance(0);
//        wallet.setCasinoBalance(0);
//        wallet.setPoint(0);
//        wallet.setBankName("기본값");
//        wallet.setBankPassword("기본값");
//        wallet.setNumber(1111L);
//        wallet.setOwnerName("기본값");
//        wallet.setTodayDeposit(0);
//        wallet.setTodayWithdraw(0);
//        wallet.setTotalAmazonDeposit(0);
//        wallet.setTotalAmazonWithdraw(0);
//        wallet.setTotalAmazonSettlement(0);
//        wallet.setAccumulatedSportsBet(0);
//        wallet.setAccumulatedCasinoBet(0);
//        wallet.setAccumulatedSlotBet(0);
//        walletRepository.save(wallet);
//
//        return amazonUserResponseMapper.toDto(savedAdmin);
//    }

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
        bigHeadOffice.setDistributor(requestDTO.getUsername());
        bigHeadOffice.setRole("ROLE_USER"); // 역할 설정
        bigHeadOffice.setAmazonUserStatus(AmazonUserStatusEnum.NORMAL);
        bigHeadOffice.setLv(1);
        bigHeadOffice.setExp(0);
        bigHeadOffice.setCreatedAt(LocalDateTime.now());
        bigHeadOffice.setBirth("널");
        bigHeadOffice.setEmail("널");
        bigHeadOffice.setReferredBy("널");
        bigHeadOffice.setPartnerType("대본사");
        bigHeadOffice.setUserGubunEnum(UserGubunEnum.정상);
        bigHeadOffice.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        bigHeadOffice.setKakaoRegistered(false);
        bigHeadOffice.setKakaoId("");
        bigHeadOffice.setTelegramRegistered(false);
        bigHeadOffice.setTelegramId("");
        bigHeadOffice.setSmsReceipt(true);
        bigHeadOffice.setAmazonVisible(false);
        bigHeadOffice.setAccountVisible(false);
        bigHeadOffice.setCanPost(true);
        bigHeadOffice.setCanComment(true);
        bigHeadOffice.setCanBonus(true);
        bigHeadOffice.setCanRecommend(true);
        bigHeadOffice.setSlotRolling(Math.round(requestDTO.getSlotRolling() * 100.0) / 100.0);
        bigHeadOffice.setCasinoRolling(Math.round(requestDTO.getCasinoRolling() * 100.0) / 100.0);
        User savedBigHeadOffice = userRepository.save(bigHeadOffice);

        Wallet wallet = new Wallet();
        wallet.setUser(savedBigHeadOffice);
        wallet.setSportsBalance(0);
        wallet.setCasinoBalance(0);
        wallet.setPoint(0);
        wallet.setOwnerName(requestDTO.getOwnername());
        wallet.setNumber(requestDTO.getNumber());
        wallet.setBankName(requestDTO.getBankname());
        wallet.setBankPassword(requestDTO.getBankPassword());
        wallet.setTodayDeposit(0);
        wallet.setTodayWithdraw(0);
        wallet.setTotalAmazonDeposit(0);
        wallet.setTotalAmazonWithdraw(0);
        wallet.setTotalAmazonSettlement(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        walletRepository.save(wallet);

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return amazonUserResponseMapper.toDto(savedBigHeadOffice);
    }

    /**
     * 본사 계정 생성.
     *
     * @param requestDTO 사용자 생성 요청 데이터
     * @param principalDetails 인증된 사용자 정보
     * @return 생성된 본사 사용자 정보
     */
    public AmazonUserResponseDTO createHeadOffice(AmazonUserRequestDTO requestDTO, PrincipalDetails principalDetails) {
        validateUserRole(principalDetails, "ROLE_ADMIN");
        // 본사 계정 생성
        User headOffice = new User();
        headOffice.setUsername(requestDTO.getUsername());
        headOffice.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        headOffice.setNickname(requestDTO.getNickname());
        headOffice.setPhone(requestDTO.getPhone());
        headOffice.setAmazonCode(requestDTO.getAmazonCode());
        headOffice.setDistributor(requestDTO.getUsername());
        headOffice.setRole("ROLE_USER"); // 역할 설정
        headOffice.setAmazonUserStatus(AmazonUserStatusEnum.NORMAL);
        headOffice.setLv(1);
        headOffice.setExp(0);
        headOffice.setCreatedAt(LocalDateTime.now());
        headOffice.setBirth("널");
        headOffice.setEmail("널");
        headOffice.setReferredBy("널");
        headOffice.setPartnerType("본사");
        headOffice.setUserGubunEnum(UserGubunEnum.정상);
        headOffice.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        headOffice.setKakaoRegistered(false);
        headOffice.setKakaoId("");
        headOffice.setTelegramRegistered(false);
        headOffice.setTelegramId("");
        headOffice.setSmsReceipt(true);
        headOffice.setAmazonVisible(false);
        headOffice.setAccountVisible(false);
        headOffice.setCanPost(true);
        headOffice.setCanComment(true);
        headOffice.setCanBonus(true);
        headOffice.setCanRecommend(true);

        // 롤링 값 설정
        // 관리자(admin)가 아닐 경우 상위 롤링 값의 범위를 체크
        if (!principalDetails.getUser().getRole().equals("ROLE_ADMIN")) {
            // 상위 계정의 롤링 값을 가져와서 비교
            double maxSlotRolling = principalDetails.getUser().getSlotRolling();
            double maxCasinoRolling = principalDetails.getUser().getCasinoRolling();

            // 요청받은 롤링 값이 상위 계정의 롤링 값 범위를 넘어가면 예외 처리
            if (requestDTO.getSlotRolling() > maxSlotRolling || requestDTO.getCasinoRolling() > maxCasinoRolling) {
                throw new IllegalArgumentException("롤링 값이 상위 계정의 범위를 초과합니다.");
            }
        }

        // 슬롯롤링적립과 카지노롤링적립 설정
        headOffice.setSlotRolling(Math.round(requestDTO.getSlotRolling() * 100.0) / 100.0);
        headOffice.setCasinoRolling(Math.round(requestDTO.getCasinoRolling() * 100.0) / 100.0);

        // 대본사에 귀속
        headOffice.setDaeId(principalDetails.getUser().getId());
        headOffice.setBonId(null);
        headOffice.setBuId(null);
        headOffice.setChongId(null);


        User savedHeadOffice = userRepository.save(headOffice);

        Wallet wallet = new Wallet();
        wallet.setUser(savedHeadOffice);
        wallet.setSportsBalance(0);
        wallet.setCasinoBalance(0);
        wallet.setPoint(0);
        wallet.setOwnerName(requestDTO.getOwnername());
        wallet.setNumber(requestDTO.getNumber());
        wallet.setBankName(requestDTO.getBankname());
        wallet.setBankPassword(requestDTO.getBankPassword());
        wallet.setTodayDeposit(0);
        wallet.setTodayWithdraw(0);
        wallet.setTotalAmazonDeposit(0);
        wallet.setTotalAmazonWithdraw(0);
        wallet.setTotalAmazonSettlement(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        walletRepository.save(wallet);

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return amazonUserResponseMapper.toDto(savedHeadOffice);
    }

    /**
     * 부본사 계정 생성.
     *
     * @param requestDTO 사용자 생성 요청 데이터
     * @param principalDetails 인증된 사용자 정보
     * @return 생성된 부본사 사용자 정보
     */

    public AmazonUserResponseDTO createDeputyHeadOffice(AmazonUserRequestDTO requestDTO, PrincipalDetails principalDetails) {
        validateUserRole(principalDetails, "ROLE_ADMIN");

        // 부본사 계정 생성
        User deputyHeadOffice = new User();
        // 필드 값 설정 (requestDTO로부터 받은 값 사용)
        deputyHeadOffice.setUsername(requestDTO.getUsername());
        deputyHeadOffice.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        deputyHeadOffice.setNickname(requestDTO.getNickname());
        deputyHeadOffice.setPhone(requestDTO.getPhone());
        deputyHeadOffice.setAmazonCode(requestDTO.getAmazonCode());
        deputyHeadOffice.setDistributor(requestDTO.getUsername());
        deputyHeadOffice.setRole("ROLE_USER");
        deputyHeadOffice.setAmazonUserStatus(AmazonUserStatusEnum.NORMAL);
        deputyHeadOffice.setLv(1);
        deputyHeadOffice.setExp(0);
        deputyHeadOffice.setCreatedAt(LocalDateTime.now());
        deputyHeadOffice.setBirth("널");
        deputyHeadOffice.setEmail("널");
        deputyHeadOffice.setReferredBy("널");
        deputyHeadOffice.setPartnerType("부본사");
        deputyHeadOffice.setUserGubunEnum(UserGubunEnum.정상);
        deputyHeadOffice.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        deputyHeadOffice.setKakaoRegistered(false);
        deputyHeadOffice.setKakaoId("");
        deputyHeadOffice.setTelegramRegistered(false);
        deputyHeadOffice.setTelegramId("");
        deputyHeadOffice.setSmsReceipt(true);
        deputyHeadOffice.setAmazonVisible(false);
        deputyHeadOffice.setAccountVisible(false);
        deputyHeadOffice.setCanPost(true);
        deputyHeadOffice.setCanComment(true);
        deputyHeadOffice.setCanBonus(true);
        deputyHeadOffice.setCanRecommend(true);

        // 롤링 값 설정
        // 관리자(admin)가 아닐 경우 상위 롤링 값의 범위를 체크
        if (!principalDetails.getUser().getRole().equals("ROLE_ADMIN")) {
            // 상위 계정의 롤링 값을 가져와서 비교
            double maxSlotRolling = principalDetails.getUser().getSlotRolling();
            double maxCasinoRolling = principalDetails.getUser().getCasinoRolling();

            // 요청받은 롤링 값이 상위 계정의 롤링 값 범위를 넘어가면 예외 처리
            if (requestDTO.getSlotRolling() > maxSlotRolling || requestDTO.getCasinoRolling() > maxCasinoRolling) {
                throw new IllegalArgumentException("롤링 값이 상위 계정의 범위를 초과합니다.");
            }
        }

        // 롤링 값 설정 (소수점 2자리까지)
        deputyHeadOffice.setSlotRolling(Math.round(requestDTO.getSlotRolling() * 100.0) / 100.0);
        deputyHeadOffice.setCasinoRolling(Math.round(requestDTO.getCasinoRolling() * 100.0) / 100.0);

        // 본사에 귀속 (본사 ID 사용)
        deputyHeadOffice.setDaeId(null);
        deputyHeadOffice.setBonId(principalDetails.getUser().getId());
        deputyHeadOffice.setBuId(null);
        deputyHeadOffice.setChongId(null);

        User savedDeputyHeadOffice = userRepository.save(deputyHeadOffice);

        Wallet wallet = new Wallet();
        wallet.setUser(savedDeputyHeadOffice);
        wallet.setSportsBalance(0);
        wallet.setCasinoBalance(0);
        wallet.setPoint(0);
        wallet.setOwnerName(requestDTO.getOwnername());
        wallet.setNumber(requestDTO.getNumber());
        wallet.setBankName(requestDTO.getBankname());
        wallet.setBankPassword(requestDTO.getBankPassword());
        wallet.setTodayDeposit(0);
        wallet.setTodayWithdraw(0);
        wallet.setTotalAmazonDeposit(0);
        wallet.setTotalAmazonWithdraw(0);
        wallet.setTotalAmazonSettlement(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        walletRepository.save(wallet);

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return amazonUserResponseMapper.toDto(savedDeputyHeadOffice);
    }

    /**
     * 총판 계정 생성.
     *
     * @param requestDTO 사용자 생성 요청 데이터
     * @param principalDetails 인증된 사용자 정보
     * @return 생성된 총판 사용자 정보
     */
    public AmazonUserResponseDTO createDistributor(AmazonUserRequestDTO requestDTO, PrincipalDetails principalDetails) {
        validateUserRole(principalDetails, "ROLE_ADMIN");
        // 총판 계정 생성
        User distributor = new User();
        // 필드 값 설정 (requestDTO로부터 받은 값 사용)
        distributor.setUsername(requestDTO.getUsername());
        distributor.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        distributor.setNickname(requestDTO.getNickname());
        distributor.setPhone(requestDTO.getPhone());
        distributor.setAmazonCode(requestDTO.getAmazonCode());
        distributor.setDistributor(requestDTO.getUsername());
        distributor.setRole("ROLE_USER");
        distributor.setAmazonUserStatus(AmazonUserStatusEnum.NORMAL);
        distributor.setLv(1);
        distributor.setExp(0);
        distributor.setCreatedAt(LocalDateTime.now());
        distributor.setBirth("널");
        distributor.setEmail("널");
        distributor.setReferredBy("널");
        distributor.setPartnerType("총판");
        distributor.setUserGubunEnum(UserGubunEnum.정상);
        distributor.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        distributor.setKakaoRegistered(false);
        distributor.setKakaoId("");
        distributor.setTelegramRegistered(false);
        distributor.setTelegramId("");
        distributor.setSmsReceipt(true);
        distributor.setAmazonVisible(false);
        distributor.setAccountVisible(false);
        distributor.setCanPost(true);
        distributor.setCanComment(true);
        distributor.setCanBonus(true);
        distributor.setCanRecommend(true);

        // 롤링 값 설정
        // 관리자(admin)가 아닐 경우 상위 롤링 값의 범위를 체크
        if (!principalDetails.getUser().getRole().equals("ROLE_ADMIN")) {
            // 상위 계정의 롤링 값을 가져와서 비교
            double maxSlotRolling = principalDetails.getUser().getSlotRolling();
            double maxCasinoRolling = principalDetails.getUser().getCasinoRolling();

            // 요청받은 롤링 값이 상위 계정의 롤링 값 범위를 넘어가면 예외 처리
            if (requestDTO.getSlotRolling() > maxSlotRolling || requestDTO.getCasinoRolling() > maxCasinoRolling) {
                throw new IllegalArgumentException("롤링 값이 상위 계정의 범위를 초과합니다.");
            }
        }

        // 롤링 값 설정 (소수점 2자리까지)
        distributor.setSlotRolling(Math.round(requestDTO.getSlotRolling() * 100.0) / 100.0);
        distributor.setCasinoRolling(Math.round(requestDTO.getCasinoRolling() * 100.0) / 100.0);

        // 부본사에 귀속 (부본사 ID 사용)
        distributor.setDaeId(null);
        distributor.setBonId(null);
        distributor.setBuId(principalDetails.getUser().getId());
        distributor.setChongId(null);

        User savedDistributor = userRepository.save(distributor);

        Wallet wallet = new Wallet();
        wallet.setUser(savedDistributor);
        wallet.setSportsBalance(0);
        wallet.setCasinoBalance(0);
        wallet.setPoint(0);
        wallet.setOwnerName(requestDTO.getOwnername());
        wallet.setNumber(requestDTO.getNumber());
        wallet.setBankName(requestDTO.getBankname());
        wallet.setBankPassword(requestDTO.getBankPassword());
        wallet.setTodayDeposit(0);
        wallet.setTodayWithdraw(0);
        wallet.setTotalAmazonDeposit(0);
        wallet.setTotalAmazonWithdraw(0);
        wallet.setTotalAmazonSettlement(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        walletRepository.save(wallet);

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return amazonUserResponseMapper.toDto(savedDistributor);
    }

    /**
     * 매장 계정 생성.
     *
     * @param requestDTO 사용자 생성 요청 데이터
     * @param principalDetails 인증된 사용자 정보
     * @return 생성된 매장 사용자 정보
     */
    public AmazonUserResponseDTO createStore(AmazonUserRequestDTO requestDTO, PrincipalDetails principalDetails) {
        validateUserRole(principalDetails, "ROLE_ADMIN");
        // 매장 계정 생성
        User store = new User();
        // 필드 값 설정 (requestDTO로부터 받은 값 사용)
        store.setUsername(requestDTO.getUsername());
        store.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        store.setNickname(requestDTO.getNickname());
        store.setPhone(requestDTO.getPhone());
        store.setAmazonCode(requestDTO.getAmazonCode());
        store.setDistributor(requestDTO.getUsername());
        store.setRole("ROLE_USER");
        store.setAmazonUserStatus(AmazonUserStatusEnum.NORMAL);
        store.setLv(1);
        store.setExp(0);
        store.setCreatedAt(LocalDateTime.now());
        store.setBirth("널");
        store.setEmail("널");
        store.setReferredBy("널");
        store.setPartnerType("매장");
        store.setUserGubunEnum(UserGubunEnum.정상);
        store.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        store.setKakaoRegistered(false);
        store.setKakaoId("");
        store.setTelegramRegistered(false);
        store.setTelegramId("");
        store.setSmsReceipt(true);
        store.setAmazonVisible(false);
        store.setAccountVisible(false);
        store.setCanPost(true);
        store.setCanComment(true);
        store.setCanBonus(true);
        store.setCanRecommend(true);

        // 롤링 값 설정
        // 관리자(admin)가 아닐 경우 상위 롤링 값의 범위를 체크
        if (!principalDetails.getUser().getRole().equals("ROLE_ADMIN")) {
            // 상위 계정의 롤링 값을 가져와서 비교
            double maxSlotRolling = principalDetails.getUser().getSlotRolling();
            double maxCasinoRolling = principalDetails.getUser().getCasinoRolling();

            // 요청받은 롤링 값이 상위 계정의 롤링 값 범위를 넘어가면 예외 처리
            if (requestDTO.getSlotRolling() > maxSlotRolling || requestDTO.getCasinoRolling() > maxCasinoRolling) {
                throw new IllegalArgumentException("롤링 값이 상위 계정의 범위를 초과합니다.");
            }
        }

        // 롤링 값 설정 (소수점 2자리까지)
        store.setSlotRolling(Math.round(requestDTO.getSlotRolling() * 100.0) / 100.0);
        store.setCasinoRolling(Math.round(requestDTO.getCasinoRolling() * 100.0) / 100.0);

        // 총판에 귀속 (총판 ID 사용)
        store.setDaeId(null);
        store.setBonId(null);
        store.setBuId(null);
        store.setChongId(principalDetails.getUser().getId());

        User savedStore = userRepository.save(store);

        Wallet wallet = new Wallet();
        wallet.setUser(savedStore);
        wallet.setSportsBalance(0);
        wallet.setCasinoBalance(0);
        wallet.setPoint(0);
        wallet.setOwnerName(requestDTO.getOwnername());
        wallet.setNumber(requestDTO.getNumber());
        wallet.setBankName(requestDTO.getBankname());
        wallet.setBankPassword(requestDTO.getBankPassword());
        wallet.setTodayDeposit(0);
        wallet.setTodayWithdraw(0);
        wallet.setTotalAmazonDeposit(0);
        wallet.setTotalAmazonWithdraw(0);
        wallet.setTotalAmazonSettlement(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        walletRepository.save(wallet);

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return amazonUserResponseMapper.toDto(savedStore);
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

    /**
     * 로그인한 사용자의 역할에 따라 직속 하위 파트너 조회.
     *
     * @param principalDetails 현재 로그인한 사용자의 정보
     * @param status 조회할 사용자의 상태
     * @return 조회된 하위 파트너 목록
     */
    public Map<String, List<AmazonUserResponseDTO>> findDirectSubFartners(PrincipalDetails principalDetails, AmazonUserStatusEnum status) {
        User currentUser = principalDetails.getUser();

        // 관리자인 경우: 모든 대본사와 그 하위 계층 조회
        if (currentUser.getRole().equals("ROLE_ADMIN")) {
            return findAllHierarchyForAdmin(status);
        } else {
            // 현재 사용자의 계층에 따라 하위 파트너 조회
            Set<User> subFartnerSet = getAllSubFartnersFromCurrentLevel(currentUser, status);
            return subFartnerSet.stream()
                    .map(amazonUserResponseMapper::toDto)
                    .collect(Collectors.groupingBy(userDTO -> userDTO.getDaeId() != null ? userDTO.getDaeId().toString() : "Independent"));
        }
    }

    /**
     * 관리자 계정으로 모든 대본사와 그 하위 계층을 조회.
     *
     * @param status 조회할 사용자의 상태
     * @return 조회된 대본사와 하위 계층 목록
     */
    private Map<String, List<AmazonUserResponseDTO>> findAllHierarchyForAdmin(AmazonUserStatusEnum status) {
        List<User> allHeadOffices = userRepository.findByPartnerType("대본사");
        Map<String, List<AmazonUserResponseDTO>> hierarchyMap = new HashMap<>();

        for (User headOffice : allHeadOffices) {
            Set<User> allSubFartners = getAllSubFartnersFromHeadOffice(headOffice, status);
            List<AmazonUserResponseDTO> responseDTOList = allSubFartners.stream()
                    .map(amazonUserResponseMapper::toDto)
                    .collect(Collectors.toList());
            hierarchyMap.put(headOffice.getUsername(), responseDTOList);
        }

        return hierarchyMap;
    }

    /**
     * 현재 사용자의 계층부터 시작하여 모든 하위 멤버 조회.
     *
     * @param currentUser 현재 사용자
     * @param status 조회할 사용자의 상태
     * @return 조회된 하위 멤버 세트
     */
    private Set<User> getAllSubFartnersFromCurrentLevel(User currentUser, AmazonUserStatusEnum status) {
        Set<User> directSubFartnerSet = new HashSet<>();
        Long currentLevelId = getCurrentLevelId(currentUser);

        List<User> headOffices = userRepository.findByDaeIdAndAmazonUserStatus(currentLevelId, status);
        for (User headOffice : headOffices) {
            directSubFartnerSet.add(headOffice);
            directSubFartnerSet.addAll(getSubFartnersForLevel("BonId", headOffice, status));
        }

        return directSubFartnerSet;
    }

    /**
     * 대본사부터 시작하여 모든 하위 파트너 조회.
     *
     * @param headOffice 대본사 사용자 엔티티
     * @param status 조회할 파트너의 상태
     * @return 조회된 하위 파트너 세트
     */
    private Set<User> getAllSubFartnersFromHeadOffice(User headOffice, AmazonUserStatusEnum status) {
        Set<User> subFartnerSet = new HashSet<>();
        subFartnerSet.add(headOffice);
        subFartnerSet.addAll(getSubFartnersForLevel("BonId", headOffice, status));
        return subFartnerSet;
    }

    /**
     * 지정된 계층의 모든 하위 파트너 조회.
     *
     * @param levelType 조회할 계층의 유형 (대본사, 본사, 부본사, 총판)
     * @param user 조회 시작점이 되는 사용자 엔티티
     * @param status 조회할 파트너의 상태
     * @return 조회된 하위 파트너 세트
     */
    private Set<User> getSubFartnersForLevel(String levelType, User user, AmazonUserStatusEnum status) {
        Set<User> subFartnerSet = new HashSet<>();

        switch (levelType) {
            case "DaeId":
                List<User> headOffices = userRepository.findByDaeIdAndAmazonUserStatus(user.getId(), status);
                for (User headOffice : headOffices) {
                    subFartnerSet.add(headOffice);
                    subFartnerSet.addAll(getSubFartnersForLevel("BonId", headOffice, status));
                }
                break;
            case "BonId":
                List<User> deputyHeadOffices = userRepository.findByBonIdAndAmazonUserStatus(user.getId(), status);
                for (User deputyHeadOffice : deputyHeadOffices) {
                    subFartnerSet.add(deputyHeadOffice);
                    subFartnerSet.addAll(getSubFartnersForLevel("BuId", deputyHeadOffice, status));
                }
                break;
            case "BuId":
                List<User> distributors = userRepository.findByBuIdAndAmazonUserStatus(user.getId(), status);
                for (User distributor : distributors) {
                    subFartnerSet.add(distributor);
                    subFartnerSet.addAll(getSubFartnersForLevel("ChongId", distributor, status));
                }
                break;
            case "ChongId":
                List<User> stores = userRepository.findByChongIdAndAmazonUserStatus(user.getId(), status);
                subFartnerSet.addAll(stores);
                break;
        }

        return subFartnerSet;
    }

    /**
     * 현재 사용자의 계층 ID를 가져옴.
     * 대본사, 본사, 부본사 또는 총판의 ID를 기반으로 해당 사용자가 속한 계층의 고유 ID를 반환.
     *
     * @param user 계층 ID를 조회할 사용자 엔티티
     * @return 현재 사용자가 속한 계층의 고유 ID
     */
    private Long getCurrentLevelId(User user) {
        if (user.getDaeId() != null) {
            return user.getDaeId();
        } else if (user.getBonId() != null) {
            return user.getBonId();
        } else if (user.getBuId() != null) {
            return user.getBuId();
        } else {
            return user.getId();
        }
    }

    /**
     * 지정된 파트너의 상위 계층 정보 조회.
     *
     * @param userId 조회할 파트너의 ID
     * @return 조회된 파트너의 상위 계층 정보를 담은 DTO
     * @throws RestControllerException 사용자를 찾을 수 없는 경우 예외 발생
     */
    public AmazonUserHierarchyResponseDTO findFartnerHierarchy(Long userId) {
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
        if (requestDTO.getPartnerType() != null) {
            user.setPartnerType(requestDTO.getPartnerType());
        }
        if (requestDTO.getDistributor() != null) {
            user.setDistributor(requestDTO.getDistributor());
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
     * 파트너의 하위 계정 생성.
     *
     * @param requestDTO 파트너 하위 계정 생성 정보
     * @param userId 상위 파트너의 사용자 ID
     * @return 생성된 하위 계정 정보를 담은 DTO
     * @throws RestControllerException 사용자를 찾을 수 없거나, 잘못된 파트너 타입인 경우 예외 발생
     */
    public AmazonUserResponseDTO createSubAccountForPartner(AmazonUserRequestDTO requestDTO, Long userId, PrincipalDetails principalDetails) {
        User parentUser = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        String parentPartnerType = parentUser.getPartnerType();

        if ("매장".equals(parentPartnerType)) {
            throw new IllegalArgumentException("매장의 하부는 추가할 수 없습니다.");
        }

        User subAccount = new User();
        subAccount.setUsername(requestDTO.getUsername());
        subAccount.setPassword(bCryptPasswordEncoder.encode(requestDTO.getPassword()));
        subAccount.setNickname(requestDTO.getNickname());
        subAccount.setPhone(requestDTO.getPhone());
        subAccount.setAmazonCode("널");
        subAccount.setDistributor(requestDTO.getUsername());
        subAccount.setRole("ROLE_USER");
        subAccount.setAmazonUserStatus(AmazonUserStatusEnum.NORMAL);
        subAccount.setLv(1);
        subAccount.setExp(0);
        subAccount.setCreatedAt(LocalDateTime.now());
        subAccount.setBirth("널");
        subAccount.setEmail("널");
        subAccount.setReferredBy("널");
        subAccount.setUserGubunEnum(UserGubunEnum.정상);
        subAccount.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        subAccount.setKakaoRegistered(false);
        subAccount.setKakaoId("");
        subAccount.setTelegramRegistered(false);
        subAccount.setTelegramId("");
        subAccount.setSmsReceipt(true);
        subAccount.setAmazonVisible(false);
        subAccount.setAccountVisible(false);
        subAccount.setCanPost(true);
        subAccount.setCanComment(true);
        subAccount.setCanBonus(true);
        subAccount.setCanRecommend(true);

        // 롤링 값 설정
        subAccount.setSlotRolling(Math.round(requestDTO.getSlotRolling() * 100.0) / 100.0);
        subAccount.setCasinoRolling(Math.round(requestDTO.getCasinoRolling() * 100.0) / 100.0);

        // 계정 귀속 설정
        assignSubAccountToParent(subAccount, parentUser, parentPartnerType);

        // 계정 저장
        User savedSubAccount = userRepository.save(subAccount);

        // Wallet 생성 및 저장
        Wallet wallet = new Wallet();
        wallet.setUser(savedSubAccount);
        wallet.setSportsBalance(0);
        wallet.setCasinoBalance(0);
        wallet.setPoint(0);
        wallet.setOwnerName(requestDTO.getOwnername());
        wallet.setNumber(requestDTO.getNumber());
        wallet.setBankName(requestDTO.getBankname());
        wallet.setBankPassword(requestDTO.getBankPassword());
        wallet.setTodayDeposit(0);
        wallet.setTodayWithdraw(0);
        wallet.setTotalAmazonDeposit(0);
        wallet.setTotalAmazonWithdraw(0);
        wallet.setTotalAmazonSettlement(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        walletRepository.save(wallet);

        JoinPoint joinPoint = joinPointRepository.findById(1L)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
        int point = joinPoint.getPoint();
        wallet.setPoint(wallet.getPoint() + point);
        walletRepository.save(wallet);

        return amazonUserResponseMapper.toDto(savedSubAccount);
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
}
