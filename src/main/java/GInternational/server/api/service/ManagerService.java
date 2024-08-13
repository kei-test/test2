package GInternational.server.api.service;

import GInternational.server.api.dto.*;
import GInternational.server.api.entity.DailyLimit;
import GInternational.server.api.entity.JoinPoint;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.mapper.UserResponseMapper;
import GInternational.server.api.repository.*;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.TransactionEnum;
import GInternational.server.api.vo.UserGubunEnum;
import GInternational.server.api.vo.UserMonitoringStatusEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.common.ipinfo.service.IpInfoService;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class ManagerService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final RechargeTransactionRepository rechargeTransactionRepository;
    private final ExchangeRepository exchangeRepository;
    private final UserResponseMapper userResponseMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final IpInfoService ipInfoService;
    private final WalletRepository walletRepository;
    private final ArticleRepository articleRepository;
    private final BetHistoryRepository betHistoryRepository;
    private final JoinPointRepository joinPointRepository;


    /**
     * 중간 관리자 생성 메서드.
     *
     * @param adminRequestDTO 관리자 생성 요청 데이터
     * @param principalDetails 요청자의 인증 정보
     * @param request 클라이언트 요청 정보
     * @return 생성된 관리자의 응답 DTO
     */
    @AuditLogService.Audit("중간 관리자 생성")
    public UserResponseDTO createManager(AdminRequestDTO adminRequestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {

        String ip = ipInfoService.getClientIp(request);
        User user = new User();
        userService.validateUsername(adminRequestDTO.getUsername());
        user.setUsername(adminRequestDTO.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(adminRequestDTO.getPassword()));
        user.setNickname(adminRequestDTO.getNickname());
        user.setPhone(adminRequestDTO.getPhone());

        // 나머지 필드는 기본값으로 설정
        user.setLv(10);
        user.setIp(ip);
        user.setUserGubunEnum(UserGubunEnum.정상);
        user.setMonitoringStatus(UserMonitoringStatusEnum.정상);
        user.setRole("ROLE_MANAGER");
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
        User savedAdmin = userRepository.save(user);

        Wallet wallet = new Wallet();
        wallet.setBankName("기본값");
        wallet.setBankPassword("기본값");
        wallet.setNumber(1111L);
        wallet.setOwnerName("기본값");
        wallet.setAmazonMoney(0);
        wallet.setAmazonBonus(0);
        wallet.setAmazonPoint(0);
        wallet.setAmazonMileage(0);
        wallet.setAccumulatedSportsBet(0);
        wallet.setAccumulatedCasinoBet(0);
        wallet.setAccumulatedSlotBet(0);
        walletRepository.save(wallet);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails("중간 관리자 생성, 관리자 아이디: " + user.getUsername());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        return userResponseMapper.toDto(savedAdmin);
    }

    /**
     * 관리자의 역할 변경 메서드. ROLE_GUEST일 경우에만 역할 변경 가능.
     *
     * @param userId 변경할 사용자의 ID
     * @param userRoleUpdateDTO 역할 변경 요청 데이터
     * @param principalDetails 요청자의 인증 정보
     * @return 역할이 변경된 사용자의 응답 DTO
     */
    @AuditLogService.Audit("유저 역할 변경")
    public UserResponseDTO updateUserRole(Long userId, UserRoleUpdateDTO userRoleUpdateDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userService.detailUser(userId,principalDetails);
        if ("ROLE_GUEST".equals(user.getRole())) {
            user.setRole(userRoleUpdateDTO.getRole());
            user.setEnabled(userRoleUpdateDTO.isEnabled());
            user.setUserGubunEnum(UserGubunEnum.정상);
            user.setUpdatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);

            AuditContext context = AuditContextHolder.getContext();
            String clientIp = request.getRemoteAddr();
            context.setIp(clientIp);
            context.setTargetId(String.valueOf(user.getId()));
            context.setUsername(user.getUsername());
            context.setDetails(user.getUsername() + "유저의 역할 ROLE_USER로 변경");
            context.setAdminUsername(principalDetails.getUsername());
            context.setTimestamp(LocalDateTime.now());

            JoinPoint joinPoint = joinPointRepository.findById(1L)
                    .orElseThrow(() -> new RestControllerException(ExceptionCode.DATA_NOT_FOUND, "JoinPoint 설정을 찾을 수 없습니다."));
            int point = joinPoint.getPoint();
            Wallet userWallet = user.getWallet();
            userWallet.setPoint(userWallet.getPoint() + point);
            walletRepository.save(userWallet);

            return userResponseMapper.toDto(savedUser);
        } else {
            throw new RuntimeException("ROLE_GUEST 만 역할 변경이 가능합니다.");
        }
    }

    /**
     * 사용자 레벨 업데이트 메서드.
     *
     * @param userId 업데이트할 사용자의 ID
     * @param userLevelUpdateDTO 사용자 레벨 업데이트 요청 데이터
     * @param principalDetails 요청자의 인증 정보
     * @return 레벨이 업데이트된 사용자의 응답 DTO
     */
    @AuditLogService.Audit("유저 레벨 변경")
    public UserResponseDTO updateUserLevel(Long userId, UserLvUpdateDTO userLevelUpdateDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userService.detailUser(userId,principalDetails);

        int beforeLv = user.getLv();
        int newLevel = userLevelUpdateDTO.getLv();
        if (newLevel >= 1 && newLevel <= 10) {
            user.setLv(newLevel);
            user.setUpdatedAt(LocalDateTime.now());
            User savedUser = userRepository.save(user);

            AuditContext context = AuditContextHolder.getContext();
            String clientIp = request.getRemoteAddr();
            context.setIp(clientIp);
            context.setTargetId(String.valueOf(user.getId()));
            context.setUsername(user.getUsername());
            context.setDetails(user.getUsername() + "의 레벨 " + beforeLv + "레벨에서 " + newLevel + "레벨로 변경");
            context.setAdminUsername(principalDetails.getUsername());
            context.setTimestamp(LocalDateTime.now());

            return userResponseMapper.toDto(savedUser);
        } else {
            throw new RestControllerException(ExceptionCode.INVALID_LEVEL, "레벨은 1부터 10 사이의 값이어야 합니다.");
        }
    }

    /**
     * 사용자 구분(Gubun) 업데이트 메서드.
     *
     * @param userId 업데이트할 사용자의 ID
     * @param userGubunUpdateDTO 사용자 구분 업데이트 요청 데이터
     * @param principalDetails 요청자의 인증 정보
     */
    @AuditLogService.Audit("유저 구분 변경")
    public void updateUserGubun(Long userId, UserGubunUpdateDTO userGubunUpdateDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userService.detailUser(userId,principalDetails);
        user.setUserGubunEnum(userGubunUpdateDTO.getUserGubunEnum());
        userRepository.save(user);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "의 변경된 유저 구분: " + user.getUserGubunEnum());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());
    }

    /**
     * 사용자 모니터링 상태 업데이트 메서드.
     *
     * @param userId 업데이트할 사용자의 ID
     * @param monitoringStatusUpdateDTO 사용자 모니터링 상태 업데이트 요청 데이터
     * @param principalDetails 요청자의 인증 정보
     * @param request 클라이언트 요청 정보
     */
    @AuditLogService.Audit("유저 모니터링 상태 변경")
    public void updateUserMonitoringStatus(Long userId, MonitoringStatusUpdateDTO monitoringStatusUpdateDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        User user = userService.detailUser(userId, principalDetails);

        user.setMonitoringStatus(monitoringStatusUpdateDTO.getMonitoringStatus());
        userRepository.save(user);

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(user.getUsername() + "의 변경된 모니터링 상태: " + user.getMonitoringStatus());
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());
    }

    /**
     * 관리자 전용 모든 회원 조회 기능.
     *
     * @param principalDetails 요청자의 인증 정보
     * @return 페이징 처리된 사용자 목록
     */
    @Transactional(value = "clientServerTransactionManager", readOnly = true)
    public List<User> findAllUser(PrincipalDetails principalDetails, Integer lv, UserGubunEnum userGubunEnum,
                                  LocalDate startDate, LocalDate endDate, String username, String phone,
                                  String distributor, String store) {
        Specification<User> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (lv != null) {
                predicates.add(cb.equal(root.get("lv"), lv));
            }

            if (userGubunEnum != null) {
                predicates.add(cb.equal(root.get("userGubunEnum"), userGubunEnum));
            }

            if (startDate != null && endDate != null) {
                LocalDateTime startDateTime = startDate.atStartOfDay();
                LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
                predicates.add(cb.between(root.get("createdAt"), startDateTime, endDateTime));
            }

            if (username != null && !username.isEmpty()) {
                predicates.add(cb.equal(root.get("username"), username));
            }

            if (phone != null && !phone.isEmpty()) {
                predicates.add(cb.equal(root.get("phone"), phone));
            }

            if (distributor != null && !distributor.isEmpty()) {
                predicates.add(cb.equal(root.get("distributor"), distributor));
            }

            if (store != null && !store.isEmpty()) {
                predicates.add(cb.equal(root.get("store"), store));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return userRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    /**
     * 탈퇴 회원 정보 업데이트 메서드. 탈퇴 취소 처리.
     *
     * @param userIds 철회할 탈퇴 회원의 ID 목록
     * @param principalDetails 요청자의 인증 정보
     */
    @AuditLogService.Audit("유저 탈퇴 취소처리")
    public void deleteUpdate(List<Long> userIds, PrincipalDetails principalDetails, HttpServletRequest request) {
        for (Long userId : userIds) {
            User user = userRepository.findById(userId).orElseThrow(()->new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 없음"));
            user.setDeleted(false);

            AuditContext context = AuditContextHolder.getContext();
            String clientIp = request.getRemoteAddr();
            context.setIp(clientIp);
            context.setTargetId(String.valueOf(user.getId()));
            context.setUsername(user.getUsername());
            context.setDetails(user.getUsername() + "유저의 탈퇴를 취소처리 함");
            context.setAdminUsername(principalDetails.getUsername());
            context.setTimestamp(LocalDateTime.now());

            userRepository.save(user);
        }
    }

    /**
     * 사용자 정보 조회 메서드. 시작 날짜와 종료 날짜를 기반으로 사용자 정보 조회.
     *
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param principalDetails 요청자의 인증 정보
     * @return 페이징 처리된 사용자 정보 목록
     */
    @Transactional(value = "clientServerTransactionManager", readOnly = true)
    public Page<UserResponseDTO> findUsersInfo(int page, int size, LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }

        if (endDate == null) {
            endDate = LocalDate.now();
        }

        Page<User> usersPage = userRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        List<UserResponseDTO> userDetailsDTOList = usersPage.getContent().stream()
                .map(user -> new UserResponseDTO(user, null, null))
                .collect(Collectors.toList());

        return new PageImpl<>(userDetailsDTOList, usersPage.getPageable(), usersPage.getTotalElements());
    }

    /**
     * 탈퇴한 회원 정보 종합 조회 메서드. 지정된 날짜 범위 내에서 탈퇴한 회원 정보 조회.
     *
     * @param page 페이지 번호
     * @param size 페이지당 항목 수
     * @param startDate 조회 시작 날짜
     * @param endDate 조회 종료 날짜
     * @param principalDetails 요청자의 인증 정보
     * @return 페이징 처리된 탈퇴한 회원 정보 목록
     */
    public Page<User> deletedUsers(int page, int size,LocalDate startDate, LocalDate endDate, PrincipalDetails principalDetails) {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        Page<User> deletedUsers = userRepository.deletedUserInfo(PageRequest.of(page - 1, size, Sort.by("id").descending()),startDate,endDate);
        List<User> deletedUsersInfo = new ArrayList<>();

        for (User user : deletedUsers.getContent()) {
            long totalRechargeAmount = rechargeTransactionRepository.sumRechargeAmountByProcessedAt(user.getId(), startDate, endDate);
            long totalExchangeAmount = exchangeRepository.sumExchangeAmountByProcessedAt(user.getId(),startDate, endDate);
            user.getWallet().setDepositTotal(totalRechargeAmount);
            user.getWallet().setWithdrawTotal(totalExchangeAmount);
            user.getWallet().setTotalSettlement(totalRechargeAmount - totalExchangeAmount);
            deletedUsersInfo.add(user);
        }
        return new PageImpl<>(deletedUsersInfo, deletedUsers.getPageable(), deletedUsers.getTotalElements());
    }

    public MainPageCountDTO countByStatus(PrincipalDetails principalDetails) {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        Long rechargeUnreadCount = rechargeTransactionRepository.countByStatus(TransactionEnum.UNREAD);
        Long rechargeWaitingCount = rechargeTransactionRepository.countByStatus(TransactionEnum.WAITING);
        Set<TransactionEnum> approvalStatuses = EnumSet.of(TransactionEnum.APPROVAL, TransactionEnum.AUTO_APPROVAL);
        Long rechargeApprovalCount = rechargeTransactionRepository.countByStatusesAndDateBetween(approvalStatuses, startOfDay, endOfDay);
        Long exchangeUnreadCount = exchangeRepository.countByStatus(TransactionEnum.UNREAD);
        Long exchangeWaitingCount = exchangeRepository.countByStatus(TransactionEnum.WAITING);
        Long exchangeApprovalCount = exchangeRepository.countApprovalByStatus(TransactionEnum.APPROVAL, startOfDay, endOfDay);
        List<String> requestAnswerStatuses = Arrays.asList("답변요청", "로그인문의 답변요청");
        Long requestAnswerCount = articleRepository.countByAnswerStatuses(requestAnswerStatuses);
        List<String> waitingAnswerStatuses = Arrays.asList("답변대기", "로그인문의 답변대기");
        Long waitingAnswerCount = articleRepository.countByAnswerStatuses(waitingAnswerStatuses);
        Long userCount = userRepository.countByRole("ROLE_USER");
        Long guestCount = userRepository.countByRole("ROLE_GUEST");

        Pageable firstItem = PageRequest.of(0, 1);
        List<String> oldestGuestReferredByList = userRepository.findOldestGuestReferredBy("ROLE_GUEST", firstItem);
        String referredByOldGuestUser = oldestGuestReferredByList.isEmpty() ? "" : oldestGuestReferredByList.get(0);

        // "초과베팅"인 유저 중 "미확인"인 가장 오래된 유저 찾기
        List<String> oldestExceedingBetUnreadUsernameList = betHistoryRepository.findUsernameByMonitoringStatusAndUnread(UserMonitoringStatusEnum.초과베팅, firstItem);
        String oldestExceedingBetUnreadUsername = oldestExceedingBetUnreadUsernameList.isEmpty() ? "" : oldestExceedingBetUnreadUsernameList.get(0);

        // "주시베팅"인 유저 중 "미확인"인 가장 오래된 유저 찾기
        List<String> oldestMonitoringBetUnreadUsernameList = betHistoryRepository.findUsernameByMonitoringStatusAndUnread(UserMonitoringStatusEnum.주시베팅, firstItem);
        String oldestMonitoringBetUnreadUsername = oldestMonitoringBetUnreadUsernameList.isEmpty() ? "" : oldestMonitoringBetUnreadUsernameList.get(0);

        return new MainPageCountDTO(rechargeUnreadCount, rechargeWaitingCount, rechargeApprovalCount,
                exchangeUnreadCount, exchangeWaitingCount, exchangeApprovalCount,
                requestAnswerCount, waitingAnswerCount, userCount, guestCount,
                referredByOldGuestUser, oldestExceedingBetUnreadUsername, oldestMonitoringBetUnreadUsername);
    }
}
