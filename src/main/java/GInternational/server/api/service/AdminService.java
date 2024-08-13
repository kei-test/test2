package GInternational.server.api.service;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.UserUpdatedRecord;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.mapper.UserResponseMapper;
import GInternational.server.api.repository.*;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.AdminEnum;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.common.ipinfo.service.IpInfoService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AdminService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final UserResponseMapper userResponseMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IpInfoService ipInfoService;
    private final WalletRepository walletRepository;
    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserUpdatedRecordService userUpdatedRecordService;
    private final UserUpdatedRecordRepository userUpdatedRecordRepository;

    /**
     * 최상위 관리자를 생성.
     *
     * @param adminRequestDTO 관리자 생성에 필요한 데이터를 담은 DTO
     * @param request 클라이언트 요청 정보
     * @return 생성된 관리자의 정보를 담은 DTO
     */

    //최상위 관리자 생성 메서드
    public UserResponseDTO createAdmin(AdminRequestDTO adminRequestDTO, HttpServletRequest request) {
        String ip = ipInfoService.getClientIp(request);
        User user = new User();
        userService.validateUsername(adminRequestDTO.getUsername());
        user.setUsername(adminRequestDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(adminRequestDTO.getPassword()));
        user.setNickname(adminRequestDTO.getNickname());
        user.setPhone(adminRequestDTO.getPhone());
        user.setApproveIp(adminRequestDTO.getApproveIp());
        user.setAdminEnum(adminRequestDTO.getAdminEnum());

        // 나머지 필드는 기본값으로 설정
        user.setLv(10);
        user.setIp(ip);
        user.setUserGubunEnum(UserGubunEnum.정상);
        user.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        user.setRole("ROLE_ADMIN");
        user.setBirth("기본값");
        user.setEmail("기본값");
        user.setReferredBy("관리자");
        user.setDistributor("");
        user.setExp(0);
        user.setAccount(null);
        user.setCreatedAt(LocalDateTime.now());
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

        Wallet wallet = new Wallet();
        wallet.setBankName("기본값");
        wallet.setBankPassword("기본값");
        wallet.setOwnerName("기본값");
        wallet.setNumber(1111L);
        wallet.setAmazonMoney(0);
        wallet.setAmazonBonus(0);
        wallet.setAmazonPoint(0);
        wallet.setAmazonMileage(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);

        walletRepository.save(wallet);
        User savedAdmin = userRepository.save(user);
        return userResponseMapper.toDto(savedAdmin);
    }

    /**
     * 사용자를 삭제. (Hard Delete)
     *
     * @param userId 삭제할 사용자의 ID
     * @param principalDetails 인증된 사용자의 정보
     */
    @AuditLogService.Audit("회원 삭제")
    public void deleteUser(Long userId, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userService.validateUser(userId);
        userService.detailUser(principalDetails.getUser().getId(),principalDetails);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "회원 삭제");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        userRepository.delete(user);
    }

    /**
     * 사용자 소프트 딜리트(상태 변경). 사용자의 삭제 상태를 업데이트하여 소프트 딜리트.
     * 이 기능에 의해 처리된 유저는 로그인 불가.
     *
     * @param userId 삭제 상태를 업데이트할 사용자의 ID
     * @param userGubunEnum 업데이트할 사용자의 상태값
     * @param principalDetails 요청자의 인증 정보
     * @return 상태가 업데이트된 사용자의 응답 DTO
     */
    public UserResponseDTO updateIsDeleted(Long userId, UserGubunEnum userGubunEnum, PrincipalDetails principalDetails) {
        User user = userService.validateUser(userId);
        // 사용자 삭제
        user.setDeleted(true);
        user.setDeletedAt(LocalDateTime.now());
        user.setUserGubunEnum(userGubunEnum);
        User savedUser = userRepository.save(user);
        return userResponseMapper.toDto(savedUser);
    }

    /**
     * 사용자 소프트 딜리트(상태 변경) 복구메서드.
     *
     * @param userId 삭제 상태를 업데이트할 사용자의 ID
     * @param principalDetails 요청자의 인증 정보
     * @return 상태가 업데이트된 사용자의 응답 DTO
     */
    public UserResponseDTO recoverUser(Long userId, PrincipalDetails principalDetails) {
        User user = userService.validateUser(userId);

        user.setDeleted(false);
        user.setUserGubunEnum(UserGubunEnum.정상);
        User savedUser = userRepository.save(user);
        return userResponseMapper.toDto(savedUser);
    }

    /**
     * 사용자 정보 업데이트.
     *
     * @param userId 업데이트할 사용자의 ID
     * @param userRequestDTO 업데이트할 사용자 정보를 담은 DTO
     * @param principalDetails 인증된 사용자의 정보
     * @return 업데이트된 사용자의 정보를 담은 DTO
     */
    @AuditLogService.Audit("유저 정보 업데이트")
    public UserResponseDTO updateUser(Long userId, UserRequestDTO userRequestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userService.detailUser(userId, principalDetails);
        Wallet wallet = user.getWallet();

        // 변경 전 상태 캡처
        Map<String, String> prevState = userUpdatedRecordService.capturePreviousState(user);

        // DTO의 데이터를 사용하여 User와 Wallet 업데이트
        Optional.ofNullable(userRequestDTO.getNickname()).ifPresent(user::setNickname);
        Optional.ofNullable(userRequestDTO.getPassword()).ifPresent(password -> {
            String encodedPassword = bCryptPasswordEncoder.encode(password);
            user.setPassword(encodedPassword);
        });
        Optional.ofNullable(userRequestDTO.getPhone()).ifPresent(user::setPhone);
        Optional.ofNullable(userRequestDTO.getBankName()).ifPresent(wallet::setBankName);
        Optional.ofNullable(userRequestDTO.getNumber()).ifPresent(wallet::setNumber);
        Optional.ofNullable(userRequestDTO.getEmail()).ifPresent(user::setEmail);
        Optional.ofNullable(userRequestDTO.getOwnerName()).ifPresent(wallet::setOwnerName);
        Optional.ofNullable(userRequestDTO.getLv()).ifPresent(user::setLv);
        Optional.ofNullable(userRequestDTO.getUserGubunEnum()).ifPresent(user::setUserGubunEnum);
        Optional.ofNullable(userRequestDTO.getReferredBy()).ifPresent(user::setReferredBy);
        Optional.ofNullable(userRequestDTO.getDistributor()).ifPresent(user::setDistributor);
        Optional.ofNullable(userRequestDTO.getStore()).ifPresent(user::setStore);
        Optional.ofNullable(userRequestDTO.getName()).ifPresent(user::setName);
        Optional.ofNullable(userRequestDTO.getRole()).ifPresent(user::setRole);
        Optional.ofNullable(userRequestDTO.getEnabled()).ifPresent(user::setEnabled);
        Optional.ofNullable(userRequestDTO.getIsDeleted()).ifPresent(user::setDeleted);
        Optional.ofNullable(userRequestDTO.getKakaoRegistered()).ifPresent(user::setKakaoRegistered);
        Optional.ofNullable(userRequestDTO.getKakaoId()).ifPresent(user::setKakaoId);
        Optional.ofNullable(userRequestDTO.getTelegramRegistered()).ifPresent(user::setTelegramRegistered);
        Optional.ofNullable(userRequestDTO.getTelegramId()).ifPresent(user::setTelegramId);
        Optional.ofNullable(userRequestDTO.getSmsReceipt()).ifPresent(user::setSmsReceipt);
        Optional.ofNullable(userRequestDTO.getAmazonVisible()).ifPresent(user::setAmazonVisible);
        Optional.ofNullable(userRequestDTO.getAccountVisible()).ifPresent(user::setAccountVisible);
        Optional.ofNullable(userRequestDTO.getCanRecommend()).ifPresent(user::setCanRecommend);
        Optional.ofNullable(userRequestDTO.getCanPost()).ifPresent(user::setCanPost);
        Optional.ofNullable(userRequestDTO.getCanComment()).ifPresent(user::setCanComment);
        Optional.ofNullable(userRequestDTO.getCanBonus()).ifPresent(user::setCanBonus);
        Optional.ofNullable(userRequestDTO.getMemo1()).ifPresent(user::setMemo1);
        Optional.ofNullable(userRequestDTO.getMemo2()).ifPresent(user::setMemo2);
        Optional.ofNullable(userRequestDTO.getMemo3()).ifPresent(user::setMemo3);
        Optional.ofNullable(userRequestDTO.getMemo4()).ifPresent(user::setMemo4);
        Optional.ofNullable(userRequestDTO.getMemo5()).ifPresent(user::setMemo5);
        Optional.ofNullable(userRequestDTO.getMemo6()).ifPresent(user::setMemo6);
        Optional.ofNullable(userRequestDTO.getVirtualAccountEnabled()).ifPresent(user::setVirtualAccountEnabled);
        Optional.ofNullable(userRequestDTO.getVirtualAccountNumber()).ifPresent(user::setVirtualAccountNumber);
        Optional.ofNullable(userRequestDTO.getVirtualAccountOwnerName()).ifPresent(user::setVirtualAccountOwnerName);

        user.setUpdatedAt(LocalDateTime.now());

        userUpdatedRecordService.recordChanges(userId, user, prevState);

        walletRepository.save(wallet);
        User savedUser = userRepository.save(user);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "의 정보 업데이트");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return userResponseMapper.toDto(savedUser);
    }

    public void adminPasswordChange(AdminPasswordChangeReqDTO adminPasswordChangeReqDTO, PrincipalDetails principalDetails) {
        User user;

        // 아이디가 입력된 경우 해당 유저의 비밀번호를 변경
        if (adminPasswordChangeReqDTO.getUsername() != null && !adminPasswordChangeReqDTO.getUsername().isEmpty()) {
            user = userRepository.findByUsername(adminPasswordChangeReqDTO.getUsername());
            if (user == null) {
                throw new RestControllerException(ExceptionCode.USER_NOT_FOUND, "해당 유저를 찾을 수 없습니다.");
            }
        } else {
            // 아이디가 입력되지 않은 경우 현재 접속한 유저의 비밀번호를 변경
            user = userRepository.findById(principalDetails.getUser().getId())
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
        }

        // 현재 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(adminPasswordChangeReqDTO.getCurrentPassword(), user.getPassword())) {
            throw new RestControllerException(ExceptionCode.PASSWORD_NOT_MATCH, "현재 비밀번호와 일치하지 않습니다.");
        }

        // 새 비밀번호와 새 비밀번호 확인 일치 여부 확인
        if (!adminPasswordChangeReqDTO.getNewPassword().equals(adminPasswordChangeReqDTO.getCheckNewPassword())) {
            throw new RestControllerException(ExceptionCode.PASSWORD_NOT_MATCH, "변경할 비밀번호가 일치하지 않습니다.");
        }

        String encodedPassword = bCryptPasswordEncoder.encode(adminPasswordChangeReqDTO.getNewPassword());
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    /**
     * 레벨별 사용자 수 조회.
     *
     * @param level 조회할 최대 레벨
     * @param principalDetails 인증된 사용자의 정보
     * @return 레벨별 사용자 수 정보를 담은 Map
     */
    public Map<String, Map<UserGubunEnum, Long>> getLvCountList(int level, PrincipalDetails principalDetails) {
        Map<String, Map<UserGubunEnum, Long>> gameInfoMap = new HashMap<>();

        for (int i = 1; i <= level; i++) {
            Map<UserGubunEnum, Long> userGubunCountMap = userRepository.getCountByUserGubunForLevel(i);

            // Null 키를 빈 문자열로 변환
            String key = String.valueOf(i);

            // 데이터가 없을 경우 빈 맵 대신 0으로 초기화된 맵을 생성
            if (userGubunCountMap.isEmpty()) {
                userGubunCountMap = new HashMap<>();
                for (UserGubunEnum userGubunEnum : UserGubunEnum.values()) {
                    userGubunCountMap.put(userGubunEnum, 0L);
                }
            }

            gameInfoMap.put(key, userGubunCountMap);
        }
        return gameInfoMap;
    }

    /**
     * 지정된 기간 동안의 레벨별 정산 정보 조회.
     *
     * @param startDate 시작 날짜
     * @param endDate 종료 날짜
     * @param principalDetails 인증된 사용자의 정보
     * @return 지정된 기간 동안의 레벨별 정산 정보를 담은 List
     */
    public List<UserCalculateDTO> searchCalculate(LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.plusDays(1).atStartOfDay();

        List<UserCalculateDTO> resultList = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            List<UserCalculateDTO> levelResults = userRepository.getTotalAmountForAllLevelForPeriod(i, startDateTime, endDateTime);

            if (levelResults.isEmpty()) {
                UserCalculateDTO emptyResult = new UserCalculateDTO();
                emptyResult.setLv(i);
                emptyResult.setSportsBalance(0);
                resultList.add(emptyResult);
            } else {
                resultList.addAll(levelResults);
            }
        }
        return resultList;
    }

    /**
     * 관리자 목록 조회. ROLE_ADMIN 역할을 가진 모든 사용자를 조회하여 반환.
     *
     * @param principalDetails 현재 인증된 사용자의 세부 정보.
     * @return 관리자 목록을 담은 {@link AdminListResDTO} 객체 리스트.
     */
    public List<AdminListResDTO> getAllAdmins(PrincipalDetails principalDetails) {
        List<User> admins = userRepository.findByRole("ROLE_ADMIN");
        return admins.stream().map(user -> {
            AdminListResDTO dto = new AdminListResDTO();
            dto.setId(user.getId());
            dto.setUsername(user.getUsername());
            dto.setNickname(user.getNickname());
            dto.setRole(user.getRole());
            dto.setAdminEnum(user.getAdminEnum());
            dto.setCreatedAt(user.getCreatedAt());
            dto.setVisitCount(user.getVisitCount());
            dto.setLastAccessedIp(user.getLastAccessedIp());
            dto.setLastVisit(user.getLastVisit());
            dto.setApproveIp(user.getApproveIp());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 주어진 사용자 ID의 관리자 상세 정보 업데이트.
     * 변경 가능한 정보에는 승인된 IP 주소, 계정의 차단 상태, 비밀번호가 포함.
     *
     * @param userId 업데이트할 관리자의 사용자 ID.
     * @param adminListUpdateDTO 업데이트할 정보를 담은 {@link AdminListUpdateDTO} 객체.
     * @param principalDetails 현재 인증된 사용자의 세부 정보.
     * @return 업데이트된 관리자 정보를 담은 {@link AdminListResDTO} 객체.
     * @throws RestControllerException 사용자를 찾을 수 없을 경우 예외 발생.
     */
    public AdminListResDTO updateAdminDetails(Long userId, AdminListUpdateDTO adminListUpdateDTO, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND));
        boolean isUpdated = false;

        if (adminListUpdateDTO.getApproveIp() != null && !adminListUpdateDTO.getApproveIp().equals(user.getApproveIp())) {
            user.setApproveIp(adminListUpdateDTO.getApproveIp());
            isUpdated = true;
        }

        if (adminListUpdateDTO.getAdminEnum() != null && !adminListUpdateDTO.getAdminEnum().equals(user.getAdminEnum())) {
            user.setAdminEnum(adminListUpdateDTO.getAdminEnum());
            isUpdated = true;
        }

        if (adminListUpdateDTO.getPassword() != null && !bCryptPasswordEncoder.matches(adminListUpdateDTO.getPassword(), user.getPassword())) {
            user.setPassword(bCryptPasswordEncoder.encode(adminListUpdateDTO.getPassword()));
            isUpdated = true;
        }

        if (isUpdated) {
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        AdminListResDTO dto = new AdminListResDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setRole(user.getRole());
        dto.setAdminEnum(user.getAdminEnum());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setVisitCount(user.getVisitCount());
        dto.setLastAccessedIp(user.getLastAccessedIp());
        dto.setLastVisit(user.getLastVisit());
        dto.setApproveIp(user.getApproveIp());

        return dto;
    }
}
