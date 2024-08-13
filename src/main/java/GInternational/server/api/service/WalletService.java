package GInternational.server.api.service;

import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.dto.WalletRequestDTO;
import GInternational.server.api.dto.WalletResponseDTO;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.mapper.WalletResponseMapper;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class WalletService {

    private final UserService userService;
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final WalletResponseMapper walletResponseMapper;

    /**
     * 지갑 업데이트.
     *
     * @param walletId           지갑 ID
     * @param walletRequestDTO   업데이트할 지갑 정보 DTO
     * @param principalDetails   현재 사용자의 인증 정보
     * @return                   업데이트된 지갑 정보의 응답 DTO
     */
    @AuditLogService.Audit("지갑 업데이트")
    public WalletResponseDTO updateWallet(Long walletId, WalletRequestDTO walletRequestDTO, PrincipalDetails principalDetails, HttpServletRequest request) {
        if (walletId == null) {
            throw new IllegalArgumentException("walletId must not be null");
        }
        Wallet wallet = walletRepository.findById(walletId).orElseThrow(
                () -> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "지갑 정보 없음"));

        Long userId = walletRequestDTO.getUserId();
        if (userId == null) {
            throw new IllegalArgumentException("userId must not be null");
        }

        User updatedUser = userService.detailUser(userId, principalDetails);
        Optional.ofNullable(walletRequestDTO.getNumber()).ifPresent(wallet::setNumber);
        Optional.ofNullable(walletRequestDTO.getBankName()).ifPresent(wallet::setBankName);
        Optional.ofNullable(walletRequestDTO.getOwnerName()).ifPresent(wallet::setOwnerName);
        Optional.ofNullable(walletRequestDTO.getBankPassword()).ifPresent(wallet::setBankPassword);
        wallet.setUpdatedAt(LocalDateTime.now());

        AuditContext context = AuditContextHolder.getContext();
        String clientIp = request.getRemoteAddr();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(walletRequestDTO.getUserId()));
        context.setUsername(wallet.getUser().getUsername());
        context.setDetails(wallet.getUser().getUsername() + "의 지갑정보 업데이트");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());

        Wallet savedWallet = walletRepository.save(wallet);
        userRepository.save(updatedUser);
        return walletResponseMapper.toDto(savedWallet);
    }

    /**
     * 사용자의 지갑 정보조회.
     *
     * @param userId             사용자 ID
     * @param principalDetails   현재 사용자의 인증 정보
     * @return                   지갑 정보
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public Wallet detailWallet(Long userId, PrincipalDetails principalDetails) {
        Wallet wallet = validateWallet(userId);
        return wallet;
    }

    /**
     * 사용자의 총 정산 정보 조회.
     *
     * @param userId             사용자 ID
     * @param principalDetails   현재 사용자의 인증 정보
     * @return                   총 정산 정보를 포함한 지갑 정보
     */
    public Wallet totalSettlement(Long userId, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId).orElseThrow
                (()-> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저 정보 없음"));
        Wallet wallet = walletRepository.findById(user.getWallet().getId()).orElseThrow
                (()-> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "지갑 정보 없음"));
        long totalSettle = wallet.getDepositTotal() - (wallet.getSportsBalance() + wallet.getWithdrawTotal());
        wallet.setTotalSettlement(wallet.getDepositTotal() - wallet.getWithdrawTotal());
        wallet.setTotalSettlement(totalSettle);
        Wallet savedWallet = walletRepository.save(wallet);
        return savedWallet;
    }

    /**
     * 모든 지갑 목록을 페이지로 반환.
     *
     * @param page               페이지 번호
     * @param size               페이지 크기
     * @param principalDetails   현재 사용자의 인증 정보
     * @return                   페이지로 구성된 지갑 목록
     */
    @Transactional(value = "clientServerTransactionManager",readOnly = true)
    public Page<Wallet> findAllWallet(int page, int size,PrincipalDetails principalDetails) {
        return walletRepository.findAll(PageRequest.of(page-1, size,Sort.by("id").descending()));
    }

    /**
     * 사용자의 지갑을 유효성 검사하고 반환.
     *
     * @param userId   사용자 ID
     * @return         검증된 지갑 정보
     */
    public Wallet validateWallet(Long userId) {
        Optional<Wallet> wallet = walletRepository.findByUserId(userId);
        Wallet findWallet = wallet.orElseThrow(()-> new RestControllerException(ExceptionCode.WALLET_INFO_NOT_FOUND, "지갑 정보 없음"));
        return findWallet;
    }

    /**
     * 사용자의 지갑을 가져옴.
     *
     * @param userId   사용자 ID
     * @return         사용자의 지갑
     */
    public Wallet getUserWallet(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for user id: " + userId));
    }

    /**
     * 지갑 정보를 업데이트.
     *
     * @param wallet   업데이트할 지갑 정보
     */
    public void updateWalletBalance(Wallet wallet) {
        walletRepository.save(wallet);
    }

    /**
     * 매일 자정에 실행되어 todayPoints 필드를 초기화합니다.
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void resetTodayPoints() {
        List<Wallet> wallets = walletRepository.findAll();
        for (Wallet wallet : wallets) {
            wallet.setTodayPoints(0);
        }
        walletRepository.saveAll(wallets);
    }
}
