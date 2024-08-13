package GInternational.server.api.service;

import GInternational.server.api.dto.AddPointRequestDTO;
import GInternational.server.api.dto.AddSportsBalanceRequestDTO;
import GInternational.server.api.entity.User;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.UserRepository;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.MoneyLogCategoryEnum;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AddSportsBalanceService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final MoneyLogService moneyLogService;

    @AuditLogService.Audit("머니 처리")
    public void modifySportsBalance(Long userId, Long walletId, AddSportsBalanceRequestDTO requestDTO, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));

        if ("지급".equals(requestDTO.getOperation())) {
            wallet.setSportsBalance(wallet.getSportsBalance() + requestDTO.getSportsBalance());
            audit("머니수동지급", requestDTO.getSportsBalance(), user, principalDetails, request, requestDTO.getMemo());
        } else if ("차감".equals(requestDTO.getOperation())) {
            wallet.setSportsBalance(wallet.getSportsBalance() - requestDTO.getSportsBalance());
            audit("머니수동차감", requestDTO.getSportsBalance(), user, principalDetails, request, requestDTO.getMemo());
        }

        walletRepository.save(wallet);
    }

    private void audit(String message, Long sportsBalance, User user, PrincipalDetails principalDetails, HttpServletRequest request, String memo) {
        String clientIp = request.getRemoteAddr();
        moneyLogService.recordMoneyUsage(user.getId(), sportsBalance, user.getWallet().getSportsBalance(), MoneyLogCategoryEnum.valueOf(message), memo);

        AuditContext context = AuditContextHolder.getContext();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(message + ", 지급 대상 아이디: " + user.getUsername() + ", 처리 금액: " + sportsBalance + "금액");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());
    }
}
