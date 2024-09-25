package GInternational.server.api.service;

import GInternational.server.api.dto.AddPointRequestDTO;
import GInternational.server.common.exception.ExceptionCode;
import GInternational.server.common.exception.RestControllerException;
import GInternational.server.api.entity.Wallet;
import GInternational.server.api.repository.WalletRepository;
import GInternational.server.api.utilities.AuditContext;
import GInternational.server.api.utilities.AuditContextHolder;
import GInternational.server.api.vo.PointLogCategoryEnum;
import GInternational.server.security.auth.PrincipalDetails;
import GInternational.server.api.entity.User;
import GInternational.server.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Service
@Transactional(value = "clientServerTransactionManager")
@RequiredArgsConstructor
public class AddPointService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;
    private final PointLogService pointLogService;

    @AuditLogService.Audit("포인트 처리")
    public void modifyPoints(Long userId, Long walletId, AddPointRequestDTO requestDTO, HttpServletRequest request, PrincipalDetails principalDetails) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.USER_NOT_FOUND, "유저를 찾을 수 없습니다."));
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new RestControllerException(ExceptionCode.WALLET_NOT_FOUND, "지갑을 찾을 수 없습니다."));

        String message;
        PointLogCategoryEnum category;

        // 메모가 있으면 메모를 message로 설정, category는 enum 값으로 설정
        if (requestDTO.getMemo() != null && !requestDTO.getMemo().isEmpty()) {
            message = requestDTO.getMemo();
            category = "지급".equals(requestDTO.getOperation()) ? PointLogCategoryEnum.포인트수동지급 : PointLogCategoryEnum.포인트수동차감;
        } else {
            // 메모가 없으면 "포인트수동지급" 또는 "포인트수동차감" 설정
            message = "지급".equals(requestDTO.getOperation()) ? "포인트수동지급" : "포인트수동차감";
            category = PointLogCategoryEnum.valueOf(message);
        }

        if ("지급".equals(requestDTO.getOperation())) {
            wallet.setPoint(wallet.getPoint() + requestDTO.getPoint());
            audit(message, category, requestDTO.getPoint(), user, principalDetails, request, requestDTO.getMemo());
        } else if ("차감".equals(requestDTO.getOperation())) {
            wallet.setPoint(wallet.getPoint() - requestDTO.getPoint());
            audit(message, category, requestDTO.getPoint(), user, principalDetails, request, requestDTO.getMemo());
        }

        walletRepository.save(wallet);
    }

    private void audit(String message, PointLogCategoryEnum category, Long points, User user, PrincipalDetails principalDetails, HttpServletRequest request, String memo) {
        String clientIp = request.getRemoteAddr();

        // category는 enum 값으로 기록, memo는 그대로 로그에 남김
        pointLogService.recordPointLog(user.getId(), points, category, clientIp, memo != null && !memo.isEmpty() ? memo : message);

        AuditContext context = AuditContextHolder.getContext();
        context.setIp(clientIp);
        context.setTargetId(String.valueOf(user.getId()));
        context.setUsername(user.getUsername());
        context.setDetails(message + ", 지급 대상 아이디: " + user.getUsername() + ", 처리 포인트: " + points + "포인트");
        context.setAdminUsername(principalDetails.getUsername());
        context.setTimestamp(LocalDateTime.now());
    }
}
